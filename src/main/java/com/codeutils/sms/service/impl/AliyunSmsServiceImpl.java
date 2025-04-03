package com.codeutils.sms.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.codeutils.sms.config.SmsProperties;
import com.codeutils.sms.service.SmsService;
import com.google.gson.Gson;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 阿里云短信服务实现
 */
public class AliyunSmsServiceImpl implements SmsService {
    
    private final SmsProperties.AliyunSmsProperties aliyunProperties;
    private final Client client;
    private final Gson gson = new Gson();
    
    public AliyunSmsServiceImpl(SmsProperties.AliyunSmsProperties aliyunProperties) {
        this.aliyunProperties = aliyunProperties;
        
        try {
            Config config = new Config()
                .setAccessKeyId(aliyunProperties.getAccessKey())
                .setAccessKeySecret(aliyunProperties.getSecretKey());
            
            // 访问的域名
            config.endpoint = "dysmsapi.aliyuncs.com";
            this.client = new Client(config);
        } catch (Exception e) {
            throw new RuntimeException("初始化阿里云短信客户端失败", e);
        }
    }
    
    @Override
    public boolean sendSms(String phoneNumber, Map<String, String> params) {
        return sendSms(phoneNumber, aliyunProperties.getTemplateCode(), params);
    }
    
    @Override
    public boolean sendSms(String phoneNumber, String templateCode, Map<String, String> params) {
        try {
            // 如果没有指定模板代码，使用配置中的默认模板
            if (!StringUtils.hasText(templateCode)) {
                templateCode = aliyunProperties.getTemplateCode();
            }
            
            SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(aliyunProperties.getSignName())
                .setTemplateCode(templateCode)
                .setTemplateParam(gson.toJson(params));
            
            SendSmsResponse response = client.sendSms(request);
            return "OK".equalsIgnoreCase(response.body.code);
        } catch (Exception e) {
            throw new RuntimeException("发送阿里云短信失败", e);
        }
    }
    
    @Override
    public boolean batchSendSms(List<String> phoneNumbers, Map<String, String> params) {
        return batchSendSms(phoneNumbers, aliyunProperties.getTemplateCode(), params);
    }
    
    @Override
    public boolean batchSendSms(List<String> phoneNumbers, String templateCode, Map<String, String> params) {
        try {
            // 如果没有指定模板代码，使用配置中的默认模板
            if (!StringUtils.hasText(templateCode)) {
                templateCode = aliyunProperties.getTemplateCode();
            }
            
            // 阿里云批量发送短信要求手机号和签名一一对应
            List<String> signNames = new ArrayList<>();
            for (int i = 0; i < phoneNumbers.size(); i++) {
                signNames.add(aliyunProperties.getSignName());
            }
            
            SendBatchSmsRequest request = new SendBatchSmsRequest()
                .setPhoneNumberJson(gson.toJson(phoneNumbers))
                .setSignNameJson(gson.toJson(signNames))
                .setTemplateCode(templateCode)
                .setTemplateParamJson(gson.toJson(params));
            
            SendBatchSmsResponse response = client.sendBatchSms(request);
            return "OK".equalsIgnoreCase(response.body.code);
        } catch (Exception e) {
            throw new RuntimeException("批量发送阿里云短信失败", e);
        }
    }
} 