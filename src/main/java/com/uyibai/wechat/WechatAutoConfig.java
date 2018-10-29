package com.uyibai.wechat;

import com.uyibai.wechat.component.WechatMpInRedisConfigStorage;
import com.uyibai.wechat.component.WechatMpService;
import com.uyibai.wechat.component.WechatOpenService;
import com.uyibai.wechat.filter.WechatMpFilter;
import com.uyibai.wechat.properties.WechatConfigProperties;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 微信 配置
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/3/30
 */
@Configuration
@EnableConfigurationProperties(WechatConfigProperties.class)
public class WechatAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(WechatAutoConfig.class);

    @Autowired
    WechatConfigProperties wechatConfigProperties;

    @Resource
    private RedisLink redisLink;

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WechatMpInRedisConfigStorage configStorage = new WechatMpInRedisConfigStorage();
        configStorage.setRedisLink(redisLink);
        configStorage.setAppId(wechatConfigProperties.getMp().getAppid());
        configStorage.setSecret(wechatConfigProperties.getMp().getAppsecret());
        configStorage.setToken(wechatConfigProperties.getMp().getToken());
        configStorage.setAesKey(wechatConfigProperties.getMp().getAeskey());
        long expiresTime = 60 * 50 * 2;
        configStorage.setExpiresTime(expiresTime);
        log.info("init wxMpConfigStorage ok:{}", wechatConfigProperties.getMp());
        return configStorage;
    }


    @Bean(name = "wechtAppConfigStorage")
    public WxMpConfigStorage wechtAppConfigStorage() {
        WechatMpInRedisConfigStorage configStorage = new WechatMpInRedisConfigStorage();
        configStorage.setRedisLink(redisLink);
        configStorage.setAppId(wechatConfigProperties.getApp().getAppid());
        configStorage.setSecret(wechatConfigProperties.getApp().getAppsecret());
        configStorage.setToken(wechatConfigProperties.getApp().getToken());
        configStorage.setAesKey(wechatConfigProperties.getApp().getAeskey());
        long expiresTime = 60 * 50 * 2;
        configStorage.setExpiresTime(expiresTime);
        log.info("init wechtAppConfigStorage ok:{}", wechatConfigProperties.getApp());
        return configStorage;
    }

    @Bean
    public WechatMpService wechatMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        WechatMpService wechatMpService = new WechatMpService();
        wechatMpService.setWxMpService(wxMpService);
        log.info("init WechatMpService ok");
        return wechatMpService;
    }

    @Bean
    public WechatOpenService wechatOpenService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wechtAppConfigStorage());
        WechatOpenService wechatOpenService = new WechatOpenService();
        wechatOpenService.setWxMpService(wxMpService);
        log.info("init WechatOpenService ok");
        return wechatOpenService;
    }


    @Bean(name = "registrationWechatFilter")
    @ConditionalOnProperty(prefix = "wechat.mp", value = "filterEnable", havingValue = "true")
    @ConditionalOnBean(WechatMpService.class)
    public FilterRegistrationBean registrationWechatFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        String filterName = wechatConfigProperties.getMp().getFilterName();
        filterName = StringUtils.isBlank(filterName) ? "wechatFilter" : filterName;

        WechatMpFilter filter = new WechatMpFilter();
        filter.setWechatConfigProperties(wechatConfigProperties);
        WechatMpService mpService = wechatMpService();
        mpService.setWechatConfigProperties(wechatConfigProperties);
        filter.setWechatMpService(mpService);
        filter.setWxMpService(mpService.getWxMpService());

        registration.setFilter(filter);
        registration.addUrlPatterns(wechatConfigProperties.getMp().getFilterPrefix());
        registration.setName(filterName);
        registration.setOrder(3);
        return registration;
    }



}
