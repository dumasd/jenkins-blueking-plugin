package io.jenkins.plugins.dumasd.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.config.RequestConfig;

public class HttpClientConfig {

    // 字符集
    private Charset charset = StandardCharsets.UTF_8;

    // 请求头
    private Map<String, String> header = new HashMap<>();

    // 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
    private int connectionRequestTimeout = 5000;

    public RequestConfig buildRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.MILLISECONDS);
        return builder.build();
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }
}
