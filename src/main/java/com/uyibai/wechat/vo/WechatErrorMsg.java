package com.uyibai.wechat.vo;

import com.xmx.common.vo.ResultMsg;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/3/29
 */
public class WechatErrorMsg extends ResultMsg {

    public WechatErrorMsg(Integer code, String msg) {
        super(code, msg);
    }

    public static final WechatErrorMsg GET_WECHAT_ACCOUNTINFO_FAIL = new WechatErrorMsg(10030005, "获取微信用户信息失败");

    public static final WechatErrorMsg WECHAT_LOGIN_FAIL = new WechatErrorMsg(10030006, "微信登录失败请重试");

    public static final WechatErrorMsg WECHAT_BINDINGS_FAIL = new WechatErrorMsg(1032, "此微信已经绑定了其他账号");



}
