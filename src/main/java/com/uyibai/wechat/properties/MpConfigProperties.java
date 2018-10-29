package com.uyibai.wechat.properties;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/6/28  下午4:18
 */
@Data
@Accessors(chain = true)
public class MpConfigProperties {

    private String appkey;
    private String appid;
    private String appsecret;
    private String token;
    private String aeskey;
    private String partenerId;
    private String partenerKey;
    private String payNotify;
    private String pageDomain;

    /**
     * 授权之获取的用户信息存储 session
     */
    private String authSessionRedisKey;

    /**
     * 授权是需要的 state存储key
     */
    private String stateRedisKey;

    /**
     * 授权有效期
     */
    private int authSessionPeriod;

    /**
     * 那些uri需要微信登录
     */
    private String[] filterUri;

    /**
     * 微信拦截器需要拦截的前缀
     */
    private boolean filterEnable = false;
    private String[] filterPrefix;
    private String filterName;



}
