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
public class AppConfigProperties {

    private String appid;
    private String appsecret;
    private String token;
    private String aeskey;
    private String partenerId;
    private String partenerKey;
    private String payNotify;
    private String pageDomain;

}
