package com.codeutils.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP工具类
 */
public class HttpUtils {

    private static final int CONNECT_TIMEOUT = 5000; // 连接超时时间，单位毫秒
    private static final int READ_TIMEOUT = 10000; // 读取超时时间，单位毫秒
    
    /**
     * 发送GET请求
     * 
     * @param url 请求URL
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }
    
    /**
     * 发送GET请求
     * 
     * @param url 请求URL
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            // 创建连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("GET");
            
            // 设置连接参数
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                return response.toString();
            } else {
                throw new IOException("HTTP请求失败，状态码: " + responseCode);
            }
        } finally {
            // 关闭资源
            if (reader != null) {
                reader.close();
            }
            
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 发送POST请求（JSON格式数据）
     * 
     * @param url 请求URL
     * @param jsonBody JSON格式请求体
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String postJson(String url, String jsonBody) throws IOException {
        return postJson(url, jsonBody, null);
    }
    
    /**
     * 发送POST请求（JSON格式数据）
     * 
     * @param url 请求URL
     * @param jsonBody JSON格式请求体
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String postJson(String url, String jsonBody, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;
        
        try {
            // 创建连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("POST");
            
            // 设置连接参数
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 发送请求体
            if (jsonBody != null) {
                outputStream = connection.getOutputStream();
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            
            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                return response.toString();
            } else {
                throw new IOException("HTTP请求失败，状态码: " + responseCode);
            }
        } finally {
            // 关闭资源
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (reader != null) {
                reader.close();
            }
            
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 发送POST请求（表单格式数据）
     * 
     * @param url 请求URL
     * @param formData 表单数据
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String postForm(String url, Map<String, String> formData) throws IOException {
        return postForm(url, formData, null);
    }
    
    /**
     * 发送POST请求（表单格式数据）
     * 
     * @param url 请求URL
     * @param formData 表单数据
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String postForm(String url, Map<String, String> formData, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;
        
        try {
            // 创建连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("POST");
            
            // 设置连接参数
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 构建表单数据
            StringBuilder formBody = new StringBuilder();
            
            if (formData != null) {
                for (Map.Entry<String, String> entry : formData.entrySet()) {
                    if (formBody.length() > 0) {
                        formBody.append("&");
                    }
                    formBody.append(entry.getKey()).append("=").append(entry.getValue());
                }
                
                // 发送请求体
                outputStream = connection.getOutputStream();
                byte[] input = formBody.toString().getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            
            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                return response.toString();
            } else {
                throw new IOException("HTTP请求失败，状态码: " + responseCode);
            }
        } finally {
            // 关闭资源
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (reader != null) {
                reader.close();
            }
            
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 发送PUT请求（JSON格式数据）
     * 
     * @param url 请求URL
     * @param jsonBody JSON格式请求体
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String putJson(String url, String jsonBody) throws IOException {
        return putJson(url, jsonBody, null);
    }
    
    /**
     * 发送PUT请求（JSON格式数据）
     * 
     * @param url 请求URL
     * @param jsonBody JSON格式请求体
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String putJson(String url, String jsonBody, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;
        
        try {
            // 创建连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("PUT");
            
            // 设置连接参数
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 发送请求体
            if (jsonBody != null) {
                outputStream = connection.getOutputStream();
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            
            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                return response.toString();
            } else {
                throw new IOException("HTTP请求失败，状态码: " + responseCode);
            }
        } finally {
            // 关闭资源
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (reader != null) {
                reader.close();
            }
            
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 发送DELETE请求
     * 
     * @param url 请求URL
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String delete(String url) throws IOException {
        return delete(url, null);
    }
    
    /**
     * 发送DELETE请求
     * 
     * @param url 请求URL
     * @param headers 请求头
     * @return 响应内容
     * @throws IOException IO异常
     */
    public static String delete(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            // 创建连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("DELETE");
            
            // 设置连接参数
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                if (connection.getInputStream() != null) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    return response.toString();
                } else {
                    return "";
                }
            } else {
                throw new IOException("HTTP请求失败，状态码: " + responseCode);
            }
        } finally {
            // 关闭资源
            if (reader != null) {
                reader.close();
            }
            
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
} 