package com.uyibai.wechat.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 第三方账号信息
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2017/5/9  下午4:16
 */
@Data
@Accessors(chain = true)
public class ThirdAcountInfo {

    /**
     * 平台用户昵称
     */
    private String nickname;
    /**
     * 平台用户头像
     */
    private String avatar;
    /**
     * 平台用户id
     */
    private String openid;
    /**
     * unionid 是微信平台特有
     */
    private String unionid;
    /**
     * 平台用户性别
     */
    private int sex;
    /**
     * 平台用户类型
     */
    private String userType;
    /**
     * iF 对应的用户id
     */
    private Integer uid;
    /**
     * 平台类型: wxpay, alipay
     */
    private String accountType;

    private String token;
}
