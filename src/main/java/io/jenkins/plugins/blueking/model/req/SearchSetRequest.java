package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.Page;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
public class SearchSetRequest extends BaseRequest {

    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;

    private List<String> fields;

    private Map<String, Object> condition;

    private Page page;

    public SearchSetRequest() {
        this.condition = new HashMap<>();
        this.page = new Page();
        this.fields = Arrays.asList("bk_set_name", "bk_set_id");
    }

    public Integer getBkBizId() {
        return bkBizId;
    }

    public void setBkBizId(Integer bkBizId) {
        this.bkBizId = bkBizId;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public void addIdOrNameCondition(String bkSet) {
        try {
            int bkSetId = Integer.parseInt(bkSet);
            condition.put("bk_set_id", bkSetId);
        } catch (Exception e) {
            condition.put("bk_set_name", bkSet);
        }
    }
}
