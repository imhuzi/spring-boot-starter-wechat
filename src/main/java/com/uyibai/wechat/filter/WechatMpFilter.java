package com.uyibai.wechat.filter;

import com.uyibai.wechat.component.WechatMpService;
import com.uyibai.wechat.properties.WechatConfigProperties;
import com.uyibai.wechat.vo.ThirdAcountInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 微信公众号 登录filter
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/6/28  下午4:18
 */
public class WechatMpFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(WechatMpFilter.class);

    private WechatConfigProperties wechatConfigProperties;

    private WxMpService wxMpService;

    private WechatMpService wechatMpService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        MDC.put("logId", RandomStringUtils.randomNumeric(8));
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String uri = httpServletRequest.getRequestURI();
        logger.info("request url={} query={}", uri, httpServletRequest.getQueryString());
        logger.info("is wechat request={}", isWechat(httpServletRequest));
        logger.info("wechatMpFilter getHeadersInfo:{}", getHeadersInfo(httpServletRequest));
        logger.info("wechatMpFilter wechatConfigProperties.getMp().getFilterUri():{}", wechatConfigProperties.getMp().getFilterUri());

        String wxOauth2Code = request.getParameter("code");
        String state = request.getParameter("state");
        // 1.微信打开，带：MicroMessenger
        // 2.或者微信服务器重定向过来,wxOauth2Code,state;
        if (includeUris(uri)
                && isWechat(httpServletRequest)
                || (StringUtils.isNotBlank(wxOauth2Code) && StringUtils.isNotBlank(state))) {

            //未登录并且是在微信中打开，而且没有授权过，(没授权过state不存在)
            ThirdAcountInfo user = wechatMpService.getCurrentUser();
            logger.info("wechat mp user={}", user);
            if (includeUris(uri) && null == user && !wechatMpService.isExistState(state) && isWechat(httpServletRequest)) {
                logger.info("current user is null");

                String queryString = httpServletRequest.getQueryString();
                String redirectUrl = wechatConfigProperties.getMp().getPageDomain() + uri;
                if (StringUtils.isNotBlank(queryString)) {
                    //如果二次分享，会带有 oauth2Code 和 state 需要处理掉
                    redirectUrl += "?" + removeWechatAuthParam(queryString);
                }
                String scope = "snsapi_userinfo";
                String wxOauth2Url = wxMpService.oauth2buildAuthorizationUrl(redirectUrl, scope, wechatMpService.getNewWxState());
                logger.info("go wechat server:{},{},{}", wxOauth2Url, scope, state);
                httpServletResponse.sendRedirect(wxOauth2Url);
                //跳转到 微信登录页面
                return;

                //微信服务器重定向后带的code和state
            } else if (includeUris(uri) && null == user
                    && (StringUtils.isNotBlank(wxOauth2Code) && StringUtils.isNotBlank(state))
                    && wechatMpService.isExistState(state)) {

                //处理用户
                try {
                    WxMpOAuth2AccessToken auth2AccessToken = wxMpService.oauth2getAccessToken(wxOauth2Code);
                    WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(auth2AccessToken, "zh_CN");
                    logger.info("WxMpUser :{}", wxMpUser);
                    if (null != wxMpUser) {
                        user = new ThirdAcountInfo();
                        user.setAvatar(wxMpUser.getHeadImgUrl());
                        user.setNickname(wxMpUser.getNickname());
                        user.setOpenid(wxMpUser.getOpenId());
                        user.setUnionid(wxMpUser.getUnionId());
                        user.setToken(wechatMpService.generateToken());
                    }
                    //授权完成，把state从redis中删除
                    wechatMpService.delState(state);
                } catch (WxErrorException e) {
                    logger.error("get wechat user info error", e);
                    e.printStackTrace();
                }

            }


            if (null != user) {
                String token = wechatMpService.setCurrentUser(httpServletResponse, user);
                request.setAttribute("wechatAuthToken", token);
                request.setAttribute("wechatUnionId", user.getUnionid());
                request.setAttribute("wechatOpenId", user.getOpenid());
                request.setAttribute("wechatUser", user);
            }
        }

        logger.info(getClass().getName() + " 走这里了");

        chain.doFilter(httpServletRequest, response);
        logger.info("end");
    }

    private boolean includeUris(String currentUri) {
        for (String uri : wechatConfigProperties.getMp().getFilterUri()) {
            boolean ok = currentUri.startsWith(uri);
            logger.info("currentUri is match:{}", ok);
            if (ok) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }


    private boolean isWechat(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("user-agent").contains("MicroMessenger");
    }

    /**
     * @param oldParam
     * @return
     */
    private String removeWechatAuthParam(String oldParam) {

        Map<String, String> paramMap = getRequestParams(oldParam);
        logger.info("paramMap:{}", paramMap);
        Set<String> paramKeys = paramMap.keySet();
        String queryString = "";
        Iterator<String> it = paramKeys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if ("code".equals(key) || "state".equals(key)) {
                continue;
            }

            if (StringUtils.isBlank(queryString)) {
                queryString += key + "=" + paramMap.get(key);
            } else {
                queryString += "&" + key + "=" + paramMap.get(key);
            }
        }

        return queryString;

    }


    public static Map<String, String> getRequestParams(String paramsstr) {
        Map<String, String> params = new HashMap();
        try {
            String[] key_value = paramsstr.split("&");
            for (String kv : key_value) {
                String[] var = kv.split("=");
                if (var.length == 2) {
                    params.put(var[0], var[1]);
                }
            }
        } catch (Exception e) {
            logger.error("请求数据转换错误", e);
        }
        return params;
    }

    public WechatConfigProperties getWechatConfigProperties() {
        return wechatConfigProperties;
    }

    public WechatMpFilter setWechatConfigProperties(WechatConfigProperties wechatConfigProperties) {
        this.wechatConfigProperties = wechatConfigProperties;
        return this;
    }

    public WxMpService getWxMpService() {
        return wxMpService;
    }

    public WechatMpFilter setWxMpService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
        return this;
    }

    public WechatMpService getWechatMpService() {
        return wechatMpService;
    }

    public WechatMpFilter setWechatMpService(WechatMpService wechatMpService) {
        this.wechatMpService = wechatMpService;
        return this;
    }
}
