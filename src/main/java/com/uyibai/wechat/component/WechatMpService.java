package com.uyibai.wechat.component;

import com.uyibai.wechat.properties.WechatConfigProperties;
import com.uyibai.wechat.vo.ThirdAcountInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * session util
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/6/28  下午4:18
 */
public class WechatMpService {
    private static final Logger LOG = LoggerFactory.getLogger(WechatMpService.class);

    @Resource
    RedisLink redisLink;

    @Autowired
    WechatConfigProperties wechatConfigProperties;

    WxMpService wxMpService;

    public WxMpService getWxMpService() {
        return wxMpService;
    }

    public WechatMpService setWxMpService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
        return this;
    }

    public ThirdAcountInfo getCurrentUser() {
        String token = CookieUtil.getToken();
        String redisKey = String.format(wechatConfigProperties.getMp().getAuthSessionRedisKey(), token);
        byte[] userBytes = redisLink.get(redisKey);
        ThirdAcountInfo user = null;
        if (null != userBytes) {
            user = JSONSerializerUtil.unserialize(userBytes, ThirdAcountInfo.class);
        }
        return user;
    }


    public ThirdAcountInfo getCurrentUser(String token) {
        if (StringUtils.isBlank(token)) {
            token = CookieUtil.getToken();
        }
        String redisKey = String.format(wechatConfigProperties.getMp().getAuthSessionRedisKey(), token);
        byte[] userBytes = redisLink.get(redisKey);
        ThirdAcountInfo user = null;
        if (null != userBytes) {
            user = JSONSerializerUtil.unserialize(userBytes, ThirdAcountInfo.class);
        }
        return user;
    }

    public String setCurrentUser(HttpServletResponse response, ThirdAcountInfo suyUser) {
        String cookieToken = CookieUtil.getToken();
        String token = cookieToken == null ? suyUser.getToken() : cookieToken;
        String redisKey = String.format(wechatConfigProperties.getMp().getAuthSessionRedisKey(), token);
        redisLink.setString(redisKey, JSONSerializerUtil.serialize(suyUser));

        setTokenToCookie(token, response);
        return token;
    }


    public void setTokenToCookie(String token, HttpServletResponse response) {
        setTokenToCookie(token, wechatConfigProperties.getMp().getAuthSessionPeriod(), response);
    }

    /**
     * 生成token
     *
     * @return
     */
    public String generateToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        return token;
    }

    /**
     * 设置用户token存入cookie
     *
     * @param token
     * @param cookieAge
     * @param response
     */
    public void setTokenToCookie(String token, int cookieAge, HttpServletResponse response) {

        Cookie cookie_com = new Cookie(AppContant.TOKEN_KEY, token);
        cookie_com.setPath("/");
        if (cookieAge != -1) {
            cookie_com.setMaxAge(cookieAge);
        }
        response.addCookie(cookie_com);
    }

    /**
     * 返回新的  微信state
     *
     * @return
     */
    public String getNewWxState() {
        String newState = RandomStringUtils.randomNumeric(8);
        String redisKey = String.format(wechatConfigProperties.getMp().getStateRedisKey(), newState);
        redisLink.setString(redisKey, newState);
        return newState;
    }

    /**
     * 判断微信 state是否存储
     *
     * @param state
     * @return
     */
    public boolean isExistState(String state) {
        String redisKey = String.format(wechatConfigProperties.getMp().getStateRedisKey(), state);
        return redisLink.exist(redisKey);
    }


    public void delState(String state) {
        String redisKey = String.format(wechatConfigProperties.getMp().getStateRedisKey(), state);
        redisLink.del(redisKey);
    }

    public WechatConfigProperties getWechatConfigProperties() {
        return wechatConfigProperties;
    }

    public WechatMpService setWechatConfigProperties(WechatConfigProperties wechatConfigProperties) {
        this.wechatConfigProperties = wechatConfigProperties;
        return this;
    }
}
