package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import io.jenkins.plugins.blueking.model.dto.Condition;
import io.jenkins.plugins.blueking.model.dto.Page;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
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

    public ListBizHostsRequest() {
        String[] fieldArr = new String[] {"bk_host_id", "bk_host_innerip", "bk_host_outerip"};
        this.fields = Arrays.stream(fieldArr).collect(Collectors.toList());
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Integer getBkBizId() {
        return bkBizId;
    }

    public void setBkBizId(Integer bkBizId) {
        this.bkBizId = bkBizId;
    }

    public List<Integer> getBkSetIds() {
        return bkSetIds;
    }

    public void setBkSetIds(List<Integer> bkSetIds) {
        this.bkSetIds = bkSetIds;
    }

    public List<Integer> getBkModuleIds() {
        return bkModuleIds;
    }

    public List<Condition> getSetCond() {
        return setCond;
    }

    public void setSetCond(List<Condition> setCond) {
        this.setCond = setCond;
    }

    public List<Condition> getModuleCond() {
        return moduleCond;
    }

    public void setModuleCond(List<Condition> moduleCond) {
        this.moduleCond = moduleCond;
    }

    public void setBkModuleIds(List<Integer> bkModuleIds) {
        this.bkModuleIds = bkModuleIds;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
