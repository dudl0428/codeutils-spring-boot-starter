package com.codeutils.sms.service;

import java.util.List;
import java.util.Map;

/**
 * 短信服务接口
 */
public interface SmsService {
    
    /**
     * 发送短信
     * @param phoneNumber 手机号码
     * @param params 短信参数
     * @return 是否发送成功
     */
    boolean sendSms(String phoneNumber, Map<String, String> params);
    
    /**
     * 发送短信（指定模板）
     * @param phoneNumber 手机号码
     * @param templateCode 模板代码
     * @param params 短信参数
     * @return 是否发送成功
     */
    boolean sendSms(String phoneNumber, String templateCode, Map<String, String> params);
    
    /**
     * 批量发送短信
     * @param phoneNumbers 手机号码列表
     * @param params 短信参数
     * @return 是否发送成功
     */
    boolean batchSendSms(List<String> phoneNumbers, Map<String, String> params);
    
    /**
     * 批量发送短信（指定模板）
     * @param phoneNumbers 手机号码列表
     * @param templateCode 模板代码
     * @param params 短信参数
     * @return 是否发送成功
     */
    boolean batchSendSms(List<String> phoneNumbers, String templateCode, Map<String, String> params);
} 