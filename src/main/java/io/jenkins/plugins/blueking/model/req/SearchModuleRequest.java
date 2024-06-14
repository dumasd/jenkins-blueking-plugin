package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.Page;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchModuleRequest extends BaseRequest {
    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;

    @JSONField(name = "bk_set_id")
    private Integer bkSetId;

    private List<String> fields;

    private Map<String, Object> condition = new HashMap<>();

    private Page page;

    public SearchModuleRequest() {
        this.page = new Page();
        this.fields = new ArrayList<>(Arrays.asList("bk_module_name, bk_module_id, bk_set_id"));
    }

    public Integer getBkBizId() {
        return bkBizId;
    }

    public void setBkBizId(Integer bkBizId) {
        this.bkBizId = bkBizId;
    }

    public Integer getBkSetId() {
        return bkSetId;
    }

    public void setBkSetId(Integer bkSetId) {
        this.bkSetId = bkSetId;
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

    public void addIdOrNameCondition(String s) {
        try {
            int id = Integer.parseInt(s);
            condition.put("bk_module_id", id);
        } catch (Exception e) {
            condition.put("bk_module_id", s);
        }
    }

    public void addIdCondition(int id) {
        condition.put("bk_module_id", id);
    }
}
