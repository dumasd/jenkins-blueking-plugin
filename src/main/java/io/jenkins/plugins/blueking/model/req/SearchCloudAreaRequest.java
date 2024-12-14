package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.Page;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SearchCloudAreaRequest extends BaseRequest {

    private Page page;

    private Condition condition;

    @Setter
    @Getter
    public static class Condition {
        @JSONField(name = "bk_cloud_id")
        private String bkCloudId;

        @JSONField(name = "bk_cloud_name")
        private String bkCloudName;
    }
}
