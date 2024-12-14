package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.Condition;
import io.jenkins.plugins.blueking.model.dto.Page;
import io.jenkins.plugins.blueking.model.dto.PropertyFilter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
@Setter
@Getter
public class ListBizHostsRequest extends BaseRequest {

    private Page page = new Page();

    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;

    @JSONField(name = "bk_set_ids")
    private List<Integer> bkSetIds;

    @JSONField(name = "bk_module_ids")
    private List<Integer> bkModuleIds;

    @JSONField(name = "set_cond")
    private List<Condition> setCond;

    @JSONField(name = "bk_module_cond")
    private List<Condition> moduleCond;

    private List<String> fields;

    @JSONField(name = "host_property_filter")
    private PropertyFilter hostPropertyFilter;

    public ListBizHostsRequest() {
        String[] fieldArr = new String[] {"bk_host_id", "bk_host_innerip", "bk_host_outerip"};
        this.fields = Arrays.stream(fieldArr).collect(Collectors.toList());
    }
}
