package com.uyibai.wechat.component;

import com.uyibai.wechat.exception.WechatBizException;
import com.uyibai.wechat.vo.ThirdAcountInfo;
import com.uyibai.wechat.vo.WechatErrorMsg;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * session util
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/6/28  下午4:18
 */
public class WechatOpenService {
    private static final Logger LOG = LoggerFactory.getLogger(WechatOpenService.class);

    WxMpService wxMpService;

    public WxMpService getWxMpService() {
        return wxMpService;
    }

    public WechatOpenService setWxMpService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
        return this;
    }

    /**
     * 根据 oauth code 获取用户信息
     *
     * @param code
     * @return
     * @throws WechatBizException
     */
    public ThirdAcountInfo getWechatOauth2UserInfo(String code) throws BizException {

        ThirdAcountInfo thirdAcountInfo = null;
        try {
            WxMpOAuth2AccessToken auth2AccessToken = wxMpService.oauth2getAccessToken(code);
            LOG.info("auth2AccessToken:{}", auth2AccessToken);
            String lang = "zh_CN";
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(auth2AccessToken, lang);

            LOG.info("wx mp user:{}", wxMpUser);
            if (wxMpUser == null) {
                throw new WechatBizException(WechatErrorMsg.GET_WECHAT_ACCOUNTINFO_FAIL);
            }

            thirdAcountInfo = new ThirdAcountInfo();
            thirdAcountInfo.setAvatar(wxMpUser.getHeadImgUrl());
            thirdAcountInfo.setNickname(wxMpUser.getNickname());
            thirdAcountInfo.setOpenid(wxMpUser.getOpenId());
            thirdAcountInfo.setUnionid(wxMpUser.getUnionId());
            thirdAcountInfo.setSex(wxMpUser.getSex());
            //微信平台标注的用户类型，随便设置的 1
            thirdAcountInfo.setUserType("1");
            LOG.info("thirdAcountInfo:{}", thirdAcountInfo);

        } catch (WxErrorException e) {
            LOG.info("getWechatOauth2UserInfo error:{}", e);
            throw new BizException(WechatErrorMsg.GET_WECHAT_ACCOUNTINFO_FAIL, e);
        }
        return thirdAcountInfo;
    }


}
