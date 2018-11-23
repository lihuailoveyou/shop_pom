package com.qf.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient的工具方法
 * @Author Kinglee
 * @Time 2018/11/21 21:55
 * @Version 1.0
 */
public class HttpClientUtil {

    /**
     * 通过httpclient发哦少年宫json数据
     * @param url 指的是要发送给谁对应的url
     * @param json
     */
    public static String sendJson(String url,String json) throws Exception {
        //构建httpclient对象
        CloseableHttpClient client = HttpClientBuilder.create().build();
        //构建一个post请求
        HttpPost post = new HttpPost(url);

        //设置请求头,告诉它的类型是application/json
        post.setHeader("Content-Type","application/json");

        //设置请求体的格式类型
        StringEntity stringEntity = new StringEntity(json,"utf-8");

        //把整个json当成请求体传过去，设置请求体(其中之后json)的内容
        post.setEntity(stringEntity);


        //发送post请求
        CloseableHttpResponse response = client.execute(post);

        //获得响应体
        HttpEntity entity = response.getEntity();

        //从响应体中解析响应结果
        String result = EntityUtils.toString(entity);
        System.out.println("响应结果："+result);

        //关闭httpclient
        client.close();

        return result;

    }
    //构建httpclient对象

    //构建一个get请求

    //相当于构建了一个浏览器，通过浏览器发送请求

    //获得响应体

    //从响应体中获得响应结果
}
