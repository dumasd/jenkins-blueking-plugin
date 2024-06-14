package io.jenkins.plugins.blueking.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
public final class HttpClientUtil {

    private HttpClientUtil() {}

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String HTTPS = "https";

    /**
     * 根据url构建HttpClient（区分http和https）
     *
     * @param url 请求地址
     * @return CloseableHttpClient实例
     */
    private static CloseableHttpClient buildHttpClient(String url) {
        try {
            return HttpClientBuilder.create()
                    .setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("HttpClient构建失败", e);
        }
    }

    /**
     * Get http请求
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @return 响应结果字符串
     */
    public static String get(String url, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpGet httpGet = new HttpGet(url);
        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpGet.setConfig(config.buildRequestConfig());
            config.getHeader().forEach(httpGet::addHeader);
            httpGet.addHeader(HttpHeaders.CONTENT_ENCODING, config.getCharset());
            return httpClient.execute(httpGet, new BasicHttpClientResponseHandler());
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * Post请求，请求内容必须为JSON格式的字符串
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @param json   JSON格式的字符串
     * @return 响应结果字符串
     */
    public static String post(String url, String json, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpPost.setConfig(config.buildRequestConfig());
            Map<String, String> header = config.getHeader();
            header.keySet().forEach(key -> httpPost.addHeader(key, header.get(key)));
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.addHeader(HttpHeaders.CONTENT_ENCODING, config.getCharset());
            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setText(json);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            entityBuilder.setContentEncoding(config.getCharset().toString());
            HttpEntity requestEntity = entityBuilder.build();
            httpPost.setEntity(requestEntity);
            return httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }

    /**
     * Post请求，请求内容必须为JSON格式的字符串
     *
     * @param url  请求地址
     * @param json JSON格式的字符串
     * @return 响应结果字符串
     */
    public static String post(String url, String json) {
        return HttpClientUtil.post(url, json, null);
    }

    /**
     * Post请求，请求内容必须为键值对参数
     *
     * @param url    请求地址
     * @param config 配置项，如果null则使用默认配置
     * @param body   请求内容键值对参数
     * @return 响应结果字符串
     */
    public static String post(String url, Map<String, String> body, HttpClientConfig config) {
        CloseableHttpClient httpClient = buildHttpClient(url);
        HttpPost httpPost = new HttpPost(url);
        if (config == null) {
            config = new HttpClientConfig();
        }
        try {
            httpPost.setConfig(config.buildRequestConfig());
            config.getHeader().forEach(httpPost::addHeader);
            httpPost.addHeader(HttpHeaders.CONTENT_ENCODING, config.getCharset());
            if (body != null && !body.isEmpty()) {
                List<NameValuePair> nvps = new ArrayList<>();
                body.forEach((k, v) -> nvps.add(new BasicNameValuePair(k, v)));
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, config.getCharset()));
            }
            return httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
        } catch (Exception e) {
            logger.error("HttpClient查询失败", e);
            throw new RuntimeException("HttpClient查询失败", e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error("HttpClient关闭连接失败", e);
            }
        }
    }
}
