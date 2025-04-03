package com.codeutils.pay.service.impl;

import com.codeutils.pay.config.PayProperties;
import com.codeutils.pay.model.PayOrder;
import com.codeutils.pay.model.PayQueryResult;
import com.codeutils.pay.model.PayResult;
import com.codeutils.pay.model.RefundOrder;
import com.codeutils.pay.model.RefundResult;
import com.codeutils.pay.service.PayService;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务实现
 */
public class WechatPayServiceImpl implements PayService {

    private final PayProperties.WechatPayProperties wechatProperties;
    private final WXPay wxPay;

    public WechatPayServiceImpl(PayProperties.WechatPayProperties wechatProperties) {
        this.wechatProperties = wechatProperties;

        // 初始化微信支付配置和服务
        com.github.wxpay.sdk.WXPayConfig payConfig = new com.github.wxpay.sdk.WXPayConfig() {
            @Override
            public String getAppID() {
                return wechatProperties.getAppId();
            }

            @Override
            public String getMchID() {
                return wechatProperties.getMchId();
            }

            @Override
            public String getKey() {
                return wechatProperties.getMchKey();
            }

            @Override
            public java.io.InputStream getCertStream() {
                return null;
            }

            @Override
            public int getHttpConnectTimeoutMs() {
                return 8000;
            }

            @Override
            public int getHttpReadTimeoutMs() {
                return 10000;
            }
        };

        // 初始化WXPay
        try {
            this.wxPay = new WXPay(payConfig);
        } catch (Exception e) {
            throw new RuntimeException("初始化微信支付服务失败", e);
        }
    }

    @Override
    public PayResult createOrder(PayOrder order) {
        try {
            // 构建统一下单请求参数
            Map<String, String> reqData = new HashMap<>();
            reqData.put("body", order.getSubject());
            reqData.put("out_trade_no", order.getOutTradeNo());
            reqData.put("fee_type", "CNY");
            reqData.put("total_fee", String.valueOf(order.getAmount().multiply(new BigDecimal("100")).intValue()));
            reqData.put("spbill_create_ip", StringUtils.hasText(order.getClientIp()) ? order.getClientIp() : "127.0.0.1");
            reqData.put("notify_url", StringUtils.hasText(order.getNotifyUrl()) ? order.getNotifyUrl() : wechatProperties.getNotifyUrl());

            // 设置交易类型
            String tradeType = StringUtils.hasText(order.getTradeType()) ?
                    order.getTradeType().toUpperCase() : "NATIVE";
            reqData.put("trade_type", tradeType);

            // 如果是JSAPI支付，需要设置openid
            if ("JSAPI".equals(tradeType) && StringUtils.hasText(order.getOpenid())) {
                reqData.put("openid", order.getOpenid());
            }

            // 附加数据
            if (StringUtils.hasText(order.getAttach())) {
                reqData.put("attach", order.getAttach());
            }

            // 调用统一下单API
            Map<String, String> responseMap = wxPay.unifiedOrder(reqData);

            // 检查返回结果
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                PayResult.PayResultBuilder resultBuilder = PayResult.builder()
                        .status("SUCCESS")
                        .outTradeNo(order.getOutTradeNo())
                        .amount(order.getAmount())
                        .channel("wechat");

                // 根据交易类型返回不同的结果
                switch (tradeType) {
                    case "NATIVE":
                        // 扫码支付
                        resultBuilder.payUrl(responseMap.get("code_url"));
                        break;

                    case "JSAPI":
                        // 公众号支付
                        Map<String, String> payParams = new HashMap<>();
                        payParams.put("appId", wechatProperties.getAppId());
                        payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                        payParams.put("nonceStr", WXPayUtil.generateNonceStr());
                        payParams.put("package", "prepay_id=" + responseMap.get("prepay_id"));
                        payParams.put("signType", "MD5");

                        // 计算签名
                        String paySign = WXPayUtil.generateSignature(payParams, wechatProperties.getMchKey());
                        payParams.put("paySign", paySign);

                        resultBuilder.payParams(payParams);
                        break;

                    case "APP":
                        // APP支付
                        Map<String, String> appPayParams = new HashMap<>();
                        appPayParams.put("appid", wechatProperties.getAppId());
                        appPayParams.put("partnerid", wechatProperties.getMchId());
                        appPayParams.put("prepayid", responseMap.get("prepay_id"));
                        appPayParams.put("package", "Sign=WXPay");
                        appPayParams.put("noncestr", WXPayUtil.generateNonceStr());
                        appPayParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

                        // 计算签名
                        String appSign = WXPayUtil.generateSignature(appPayParams, wechatProperties.getMchKey());
                        appPayParams.put("sign", appSign);

                        resultBuilder.payParams(appPayParams);
                        break;

                    case "MWEB":
                        // H5支付
                        resultBuilder.payUrl(responseMap.get("mweb_url"));
                        break;

                    default:
                        throw new IllegalArgumentException("不支持的交易类型: " + tradeType);
                }

                return resultBuilder.build();
            } else {
                return PayResult.builder()
                        .status("FAIL")
                        .outTradeNo(order.getOutTradeNo())
                        .errorCode(responseMap.get("err_code"))
                        .errorMsg(responseMap.get("err_code_des"))
                        .build();
            }

        } catch (Exception e) {
            return PayResult.builder()
                    .status("FAIL")
                    .outTradeNo(order.getOutTradeNo())
                    .errorCode("SYSTEM_ERROR")
                    .errorMsg(e.getMessage())
                    .build();
        }
    }

    @Override
    public PayQueryResult queryOrder(String outTradeNo) {
        return queryOrder(outTradeNo, null);
    }

    @Override
    public PayQueryResult queryOrder(String outTradeNo, String tradeNo) {
        try {
            Map<String, String> reqData = new HashMap<>();

            if (StringUtils.hasText(outTradeNo)) {
                reqData.put("out_trade_no", outTradeNo);
            }

            if (StringUtils.hasText(tradeNo)) {
                reqData.put("transaction_id", tradeNo);
            }

            Map<String, String> responseMap = wxPay.orderQuery(reqData);

            // 检查返回结果
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                PayQueryResult.PayQueryResultBuilder builder = PayQueryResult.builder()
                        .outTradeNo(responseMap.get("out_trade_no"))
                        .tradeNo(responseMap.get("transaction_id"))
                        .channel("wechat");

                // 转换支付状态
                String tradeState = responseMap.get("trade_state");
                if ("SUCCESS".equals(tradeState)) {
                    builder.tradeStatus("TRADE_SUCCESS");
                } else if ("REFUND".equals(tradeState)) {
                    builder.tradeStatus("TRADE_SUCCESS");
                } else if ("NOTPAY".equals(tradeState)) {
                    builder.tradeStatus("WAIT_BUYER_PAY");
                } else if ("CLOSED".equals(tradeState)) {
                    builder.tradeStatus("TRADE_CLOSED");
                } else if ("USERPAYING".equals(tradeState)) {
                    builder.tradeStatus("WAIT_BUYER_PAY");
                } else if ("PAYERROR".equals(tradeState)) {
                    builder.tradeStatus("TRADE_CLOSED");
                } else {
                    builder.tradeStatus(tradeState);
                }

                // 设置金额
                if (responseMap.containsKey("total_fee")) {
                    int totalFee = Integer.parseInt(responseMap.get("total_fee"));
                    builder.amount(new BigDecimal(totalFee).divide(new BigDecimal("100")));
                }

                // 设置买家信息
                if (responseMap.containsKey("openid")) {
                    builder.buyerId(responseMap.get("openid"));
                }

                return builder.build();
            } else {
                return PayQueryResult.builder()
                        .outTradeNo(outTradeNo)
                        .channel("wechat")
                        .errorCode(responseMap.get("err_code"))
                        .errorMsg(responseMap.get("err_code_des"))
                        .build();
            }

        } catch (Exception e) {
            return PayQueryResult.builder()
                    .outTradeNo(outTradeNo)
                    .channel("wechat")
                    .errorCode("SYSTEM_ERROR")
                    .errorMsg(e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean closeOrder(String outTradeNo) {
        try {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("out_trade_no", outTradeNo);

            Map<String, String> responseMap = wxPay.closeOrder(reqData);

            return "SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public RefundResult refund(RefundOrder refundOrder) {
        try {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("out_trade_no", refundOrder.getOutTradeNo());
            reqData.put("out_refund_no", refundOrder.getOutRefundNo());

            // 设置退款金额和订单总金额（单位：分）
            int refundFee = refundOrder.getAmount().multiply(new BigDecimal("100")).intValue();
            reqData.put("refund_fee", String.valueOf(refundFee));

            // 如果未提供订单总金额，使用退款金额
            int totalFee = refundOrder.getTotalAmount() != null ?
                    refundOrder.getTotalAmount().multiply(new BigDecimal("100")).intValue() : refundFee;
            reqData.put("total_fee", String.valueOf(totalFee));

            // 设置退款原因
            if (StringUtils.hasText(refundOrder.getReason())) {
                reqData.put("refund_desc", refundOrder.getReason());
            }

            // 设置通知地址
            if (StringUtils.hasText(refundOrder.getNotifyUrl())) {
                reqData.put("notify_url", refundOrder.getNotifyUrl());
            }

            Map<String, String> responseMap = wxPay.refund(reqData);

            // 检查返回结果
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                RefundResult.RefundResultBuilder builder = RefundResult.builder()
                        .status("SUCCESS")
                        .outTradeNo(refundOrder.getOutTradeNo())
                        .outRefundNo(refundOrder.getOutRefundNo())
                        .refundNo(responseMap.get("refund_id"))
                        .amount(refundOrder.getAmount())
                        .channel("wechat");

                return builder.build();
            } else {
                return RefundResult.builder()
                        .status("FAIL")
                        .outTradeNo(refundOrder.getOutTradeNo())
                        .outRefundNo(refundOrder.getOutRefundNo())
                        .channel("wechat")
                        .errorCode(responseMap.get("err_code"))
                        .errorMsg(responseMap.get("err_code_des"))
                        .build();
            }

        } catch (Exception e) {
            return RefundResult.builder()
                    .status("FAIL")
                    .outTradeNo(refundOrder.getOutTradeNo())
                    .outRefundNo(refundOrder.getOutRefundNo())
                    .channel("wechat")
                    .errorCode("SYSTEM_ERROR")
                    .errorMsg(e.getMessage())
                    .build();
        }
    }

    @Override
    public RefundResult queryRefund(String outRefundNo) {
        try {
            Map<String, String> reqData = new HashMap<>();
            reqData.put("out_refund_no", outRefundNo);

            Map<String, String> responseMap = wxPay.refundQuery(reqData);

            // 检查返回结果
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                RefundResult.RefundResultBuilder builder = RefundResult.builder()
                        .outRefundNo(outRefundNo)
                        .outTradeNo(responseMap.get("out_trade_no"))
                        .refundNo(responseMap.get("refund_id_0"))
                        .channel("wechat");

                // 转换退款状态
                String refundStatus = responseMap.get("refund_status_0");
                if ("SUCCESS".equals(refundStatus)) {
                    builder.status("SUCCESS");
                } else if ("PROCESSING".equals(refundStatus)) {
                    builder.status("PROCESSING");
                } else if ("REFUNDCLOSE".equals(refundStatus)) {
                    builder.status("FAIL");
                } else {
                    builder.status(refundStatus);
                }

                // 设置退款金额
                if (responseMap.containsKey("refund_fee_0")) {
                    int refundFee = Integer.parseInt(responseMap.get("refund_fee_0"));
                    builder.amount(new BigDecimal(refundFee).divide(new BigDecimal("100")));
                }

                return builder.build();
            } else {
                return RefundResult.builder()
                        .status("FAIL")
                        .outRefundNo(outRefundNo)
                        .channel("wechat")
                        .errorCode(responseMap.get("err_code"))
                        .errorMsg(responseMap.get("err_code_des"))
                        .build();
            }

        } catch (Exception e) {
            return RefundResult.builder()
                    .status("FAIL")
                    .outRefundNo(outRefundNo)
                    .channel("wechat")
                    .errorCode("SYSTEM_ERROR")
                    .errorMsg(e.getMessage())
                    .build();
        }
    }

    @Override
    public String handleNotify(Map<String, String> params, Map<String, String> headers) {
        try {
            // 验证签名
            if (!wxPay.isPayResultNotifySignatureValid(params)) {
                return "签名验证失败";
            }

            String returnCode = params.get("return_code");
            String resultCode = params.get("result_code");

            // 处理返回结果
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                // 在这里可以处理支付成功的业务逻辑
                // ...

                // 返回成功的响应
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("return_code", "SUCCESS");
                responseMap.put("return_msg", "OK");
                return WXPayUtil.mapToXml(responseMap);
            } else {
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("return_code", "FAIL");
                responseMap.put("return_msg", "处理失败");
                return WXPayUtil.mapToXml(responseMap);
            }
        } catch (Exception e) {
            try {
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("return_code", "FAIL");
                responseMap.put("return_msg", e.getMessage());
                return WXPayUtil.mapToXml(responseMap);
            } catch (Exception ex) {
                return "处理失败";
            }
        }
    }
} 