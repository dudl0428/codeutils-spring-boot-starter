package com.codeutils.pay.service;

import com.codeutils.pay.model.PayOrder;
import com.codeutils.pay.model.PayQueryResult;
import com.codeutils.pay.model.PayResult;
import com.codeutils.pay.model.RefundOrder;
import com.codeutils.pay.model.RefundResult;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PayService {
    
    /**
     * 创建支付订单
     * @param order 支付订单信息
     * @return 支付结果
     */
    PayResult createOrder(PayOrder order);
    
    /**
     * 查询支付结果
     * @param outTradeNo 商户订单号
     * @return 支付查询结果
     */
    PayQueryResult queryOrder(String outTradeNo);
    
    /**
     * 查询支付结果
     * @param outTradeNo 商户订单号
     * @param tradeNo 第三方平台订单号
     * @return 支付查询结果
     */
    PayQueryResult queryOrder(String outTradeNo, String tradeNo);
    
    /**
     * 关闭订单
     * @param outTradeNo 商户订单号
     * @return 是否关闭成功
     */
    boolean closeOrder(String outTradeNo);
    
    /**
     * 申请退款
     * @param refundOrder 退款订单信息
     * @return 退款结果
     */
    RefundResult refund(RefundOrder refundOrder);
    
    /**
     * 查询退款结果
     * @param outRefundNo 商户退款单号
     * @return 退款结果
     */
    RefundResult queryRefund(String outRefundNo);
    
    /**
     * 支付异步通知处理
     * @param params 通知参数
     * @param headers 请求头信息
     * @return 处理结果（成功返回"success"，失败返回错误信息）
     */
    String handleNotify(Map<String, String> params, Map<String, String> headers);
} 