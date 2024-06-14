package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.BizPropertyFilter;
import io.jenkins.plugins.blueking.model.dto.Page;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchBusinessRequest extends BaseRequest {

    private List<String> fields;

    private Map<String, Object> condition = new HashMap<>();

    @JSONField(name = "biz_property_filter")
    private BizPropertyFilter bizPropertyFilter;

    private Page page = new Page();

    public SearchBusinessRequest() {
        this.fields = new ArrayList<>(Arrays.asList("bk_biz_id", "bk_biz_name"));
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public BizPropertyFilter getBizPropertyFilter() {
        return bizPropertyFilter;
    }

    public void setBizPropertyFilter(BizPropertyFilter bizPropertyFilter) {
        this.bizPropertyFilter = bizPropertyFilter;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }
}
