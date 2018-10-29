package com.uyibai.wechat;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/3/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WechatAutoConfig.class)
@Documented
public @interface EnableWechat {
}
