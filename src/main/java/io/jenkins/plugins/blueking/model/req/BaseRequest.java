package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
@Setter
@Getter
public class BaseRequest {

    @JSONField(name = "bk_app_code")
    private String bkAppCode;

    @JSONField(name = "bk_app_secret")
    private String bkAppSecret;

    @JSONField(name = "bk_token")
    private String bkToken;

    @JSONField(name = "bk_username")
    private String bkUsername;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
