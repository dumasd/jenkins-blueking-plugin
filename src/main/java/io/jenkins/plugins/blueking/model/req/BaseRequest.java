package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
public class BaseRequest {

    @JSONField(name = "bk_app_code")
    private String bkAppCode;

    @JSONField(name = "bk_app_secret")
    private String bkAppSecret;

    @JSONField(name = "bk_token")
    private String bkToken;

    @JSONField(name = "bk_username")
    private String bkUsername;

    public String getBkAppCode() {
        return bkAppCode;
    }

    public void setBkAppCode(String bkAppCode) {
        this.bkAppCode = bkAppCode;
    }

    public String getBkAppSecret() {
        return bkAppSecret;
    }

    public void setBkAppSecret(String bkAppSecret) {
        this.bkAppSecret = bkAppSecret;
    }

    public String getBkToken() {
        return bkToken;
    }

    public void setBkToken(String bkToken) {
        this.bkToken = bkToken;
    }

    public String getBkUsername() {
        return bkUsername;
    }

    public void setBkUsername(String bkUsername) {
        this.bkUsername = bkUsername;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
