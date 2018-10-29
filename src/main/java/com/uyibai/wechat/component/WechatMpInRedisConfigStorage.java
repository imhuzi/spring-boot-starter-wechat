package com.uyibai.wechat.component;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;

import javax.annotation.Resource;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 06/12/2017 10:54 AM
 */
public class WechatMpInRedisConfigStorage extends WxMpInMemoryConfigStorage {
    private static final String ACCESS_TOKEN_KEY = "wechat_access_token_";
    private static final String JSAPI_TICKET_KEY = "wechat_jsapi_ticket_";
    private static final String CARDAPI_TICKET_KEY = "wechat_cardapi_ticket_";

    @Resource
    protected RedisLink redisLink;

    public WechatMpInRedisConfigStorage() {
    }

    @Override
    public String getAccessToken() {
        return this.redisLink.getString("wechat_access_token_".concat(this.appId));
    }

    @Override
    public boolean isAccessTokenExpired() {
        return this.redisLink.ttl("wechat_access_token_".concat(this.appId)).longValue() < 2L;
    }

    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        this.redisLink.setString("wechat_access_token_".concat(this.appId), accessToken);
        this.redisLink.expire("wechat_access_token_".concat(this.appId), expiresInSeconds - 200);
    }

    @Override
    public void expireAccessToken() {
        this.redisLink.expire("wechat_access_token_".concat(this.appId), 0);
    }

    @Override
    public String getJsapiTicket() {
        return this.redisLink.getString("wechat_jsapi_ticket_".concat(this.appId));
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return this.redisLink.ttl("wechat_jsapi_ticket_".concat(this.appId)).longValue() < 2L;
    }

    @Override
    public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        this.redisLink.setString("wechat_jsapi_ticket_".concat(this.appId), jsapiTicket);
        this.redisLink.expire("wechat_jsapi_ticket_".concat(this.appId), expiresInSeconds - 200);
    }

    @Override
    public void expireJsapiTicket() {
        this.redisLink.expire("wechat_jsapi_ticket_".concat(this.appId), 0);
    }

    @Override
    public String getCardApiTicket() {
        return this.redisLink.getString("wechat_cardapi_ticket_".concat(this.appId));
    }

    @Override
    public boolean isCardApiTicketExpired() {
        return this.redisLink.ttl("wechat_cardapi_ticket_".concat(this.appId)).longValue() < 2L;
    }

    @Override
    public synchronized void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
        this.redisLink.setString("wechat_cardapi_ticket_".concat(this.appId), cardApiTicket);
        this.redisLink.expire("wechat_cardapi_ticket_".concat(this.appId), expiresInSeconds - 200);
    }

    @Override
    public void expireCardApiTicket() {
        this.redisLink.expire("wechat_cardapi_ticket_".concat(this.appId), 0);
    }

    public void setRedisLink(RedisLink redisLink) {
        this.redisLink = redisLink;
    }
}
