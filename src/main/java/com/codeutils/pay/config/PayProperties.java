package com.codeutils.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 支付服务配置属性
 */
@Data
public class PayProperties {

    /**
     * 支付类型：wechat(微信)、alipay(支付宝)、unionpay(银联)
     */
    private String type = "wechat";

    /**
     * 微信支付配置
     */
    @NestedConfigurationProperty
    private WechatPayProperties wechat = new WechatPayProperties();

    /**
     * 支付宝支付配置
     */
    @NestedConfigurationProperty
    private AlipayProperties alipay = new AlipayProperties();

    /**
     * 银联支付配置
     */
    @NestedConfigurationProperty
    private UnionPayProperties unionpay = new UnionPayProperties();

    /**
     * 微信支付配置
     */
    @Data
    public static class WechatPayProperties {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 商户号
         */
        private String mchId;

        /**
         * 商户密钥
         */
        private String mchKey;

        /**
         * API V3密钥
         */
        private String apiV3Key;

        /**
         * 私钥路径
         */
        private String privateKeyPath;

        /**
         * 私钥证书路径
         */
        private String privateCertPath;

        /**
         * 证书路径
         */
        private String keyPath;

        /**
         * 证书序列号
         */
        private String certSerialNo;

        /**
         * 回调地址
         */
        private String notifyUrl;
    }

    /**
     * 支付宝支付配置
     */
    @Data
    public static class AlipayProperties {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 私钥
         */
        private String privateKey;

        /**
         * 公钥
         */
        private String publicKey;

        /**
         * 支付宝公钥
         */
        private String alipayPublicKey;

        /**
         * 网关地址
         */
        private String gatewayUrl = "https://openapi.alipay.com/gateway.do";

        /**
         * 回调地址
         */
        private String notifyUrl;

        /**
         * 返回地址
         */
        private String returnUrl;
    }

    /**
     * 银联支付配置
     */
    @Data
    public static class UnionPayProperties {
        /**
         * 商户号
         */
        private String mchId;

        /**
         * 证书路径
         */
        private String certPath;

        /**
         * 证书密码
         */
        private String certPwd;

        /**
         * 前台通知地址
         */
        private String frontUrl;

        /**
         * 后台通知地址
         */
        private String backUrl;
    }
} 