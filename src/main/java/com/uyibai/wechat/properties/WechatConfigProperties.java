package com.uyibai.wechat.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/6/28  下午4:18
 */
@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatConfigProperties {

    /**
     * 微信 app配置
     */
    private AppConfigProperties app;

    /**
     * 微信公众号配置
     */
    private MpConfigProperties mp;


    public AppConfigProperties getApp() {
        return app;
    }

    public WechatConfigProperties setApp(AppConfigProperties app) {
        this.app = app;
        return this;
    }

    public MpConfigProperties getMp() {
        return mp;
    }

    public WechatConfigProperties setMp(MpConfigProperties mp) {
        this.mp = mp;
        return this;
    }
}
