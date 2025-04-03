package com.codeutils.sms.service.impl;

import com.codeutils.sms.config.SmsProperties;
import com.codeutils.sms.service.SmsService;
import com.google.gson.Gson;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云短信服务实现
 */
public class TencentSmsServiceImpl implements SmsService {
    
    private final SmsProperties.TencentSmsProperties tencentProperties;
    private final SmsClient client;
    private final Gson gson = new Gson();
    
    public TencentSmsServiceImpl(SmsProperties.TencentSmsProperties tencentProperties) {
        this.tencentProperties = tencentProperties;
        
        try {
            // 实例化一个认证对象
            Credential credential = new Credential(
                tencentProperties.getSecretId(),
                tencentProperties.getSecretKey()
            );
            
            // 实例化一个http选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            
            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化SMS客户端
            this.client = new SmsClient(credential, "ap-guangzhou", clientProfile);
        } catch (Exception e) {
            throw new RuntimeException("初始化腾讯云短信客户端失败", e);
        }
    }
    
    @Override
    public boolean sendSms(String phoneNumber, Map<String, String> params) {
        return sendSms(phoneNumber, tencentProperties.getTemplateId(), params);
    }
    
    @Override
    public boolean sendSms(String phoneNumber, String templateCode, Map<String, String> params) {
        try {
            // 如果没有指定模板ID，使用配置中的默认模板
            if (!StringUtils.hasText(templateCode)) {
                templateCode = tencentProperties.getTemplateId();
            }
            
            // 格式化手机号，添加+86前缀
            String[] phoneNumbers = {formatPhoneNumber(phoneNumber)};
            
            // 将参数Map转换为字符串数组
            String[] templateParams = getTemplateParamsArray(params);
            
            // 实例化一个请求对象
            SendSmsRequest req = new SendSmsRequest();
            req.setPhoneNumberSet(phoneNumbers);
            req.setSmsSdkAppId(tencentProperties.getAppId());
            req.setSignName(tencentProperties.getSignName());
            req.setTemplateId(templateCode);
            req.setTemplateParamSet(templateParams);
            
            // 发送短信
            SendSmsResponse response = client.SendSms(req);
            
            // 验证发送结果
            return response.getSendStatusSet().length > 0 
                && "Ok".equals(response.getSendStatusSet()[0].getCode());
        } catch (Exception e) {
            throw new RuntimeException("发送腾讯云短信失败", e);
        }
    }
    
    @Override
    public boolean batchSendSms(List<String> phoneNumbers, Map<String, String> params) {
        return batchSendSms(phoneNumbers, tencentProperties.getTemplateId(), params);
    }
    
    @Override
    public boolean batchSendSms(List<String> phoneNumbers, String templateCode, Map<String, String> params) {
        try {
            // 如果没有指定模板ID，使用配置中的默认模板
            if (!StringUtils.hasText(templateCode)) {
                templateCode = tencentProperties.getTemplateId();
            }
            
            // 格式化所有手机号
            String[] formattedPhoneNumbers = phoneNumbers.stream()
                .map(this::formatPhoneNumber)
                .toArray(String[]::new);
            
            // 将参数Map转换为字符串数组
            String[] templateParams = getTemplateParamsArray(params);
            
            // 实例化一个请求对象
            SendSmsRequest req = new SendSmsRequest();
            req.setPhoneNumberSet(formattedPhoneNumbers);
            req.setSmsSdkAppId(tencentProperties.getAppId());
            req.setSignName(tencentProperties.getSignName());
            req.setTemplateId(templateCode);
            req.setTemplateParamSet(templateParams);
            
            // 发送短信
            SendSmsResponse response = client.SendSms(req);
            
            // 验证发送结果，所有手机号都成功才返回true
            boolean allSuccess = true;
            for (com.tencentcloudapi.sms.v20210111.models.SendStatus status : response.getSendStatusSet()) {
                if (!"Ok".equals(status.getCode())) {
                    allSuccess = false;
                    break;
                }
            }
            
            return allSuccess;
        } catch (Exception e) {
            throw new RuntimeException("批量发送腾讯云短信失败", e);
        }
    }
    
    /**
     * 格式化手机号码（添加+86前缀）
     * @param phoneNumber 手机号码
     * @return 格式化后的手机号
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        return "+86" + phoneNumber;
    }
    
    /**
     * 将参数Map转换为字符串数组
     * @param params 参数Map
     * @return 参数数组
     */
    private String[] getTemplateParamsArray(Map<String, String> params) {
        List<String> paramsList = new ArrayList<>();
        for (String value : params.values()) {
            paramsList.add(value);
        }
        return paramsList.toArray(new String[0]);
    }
} 