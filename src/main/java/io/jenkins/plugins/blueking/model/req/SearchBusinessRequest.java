package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.Page;
import io.jenkins.plugins.blueking.model.dto.PropertyFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchBusinessRequest extends BaseRequest {

    private List<String> fields;

    private Map<String, Object> condition = new HashMap<>();

    @JSONField(name = "biz_property_filter")
    private PropertyFilter bizPropertyFilter;

    private Page page = new Page();

    public SearchBusinessRequest() {
        this.fields = new ArrayList<>(Arrays.asList("bk_biz_id", "bk_biz_name"));
    }

    public void setBizPropertyFilter(PropertyFilter bizPropertyFilter) {
        this.bizPropertyFilter = bizPropertyFilter;
    }
}
