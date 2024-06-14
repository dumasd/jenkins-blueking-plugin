package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;

public class BkBizSetModule {

    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;

    @JSONField(name = "bk_biz_name")
    private String bkBizName;

    @JSONField(name = "bk_set_id")
    private Integer bkSetId;

    @JSONField(name = "bk_set_name")
    private String bkSetName;

    @JSONField(name = "bk_module_id")
    private Integer bkModuleId;

    @JSONField(name = "bk_module_name")
    private String bkModuleName;

    @JSONField(name = "default")
    private Integer defaultFlg;

    public Integer getBkBizId() {
        return bkBizId;
    }

    public void setBkBizId(Integer bkBizId) {
        this.bkBizId = bkBizId;
    }

    public String getBkBizName() {
        return bkBizName;
    }

    public void setBkBizName(String bkBizName) {
        this.bkBizName = bkBizName;
    }

    public Integer getBkSetId() {
        return bkSetId;
    }

    public void setBkSetId(Integer bkSetId) {
        this.bkSetId = bkSetId;
    }

    public String getBkSetName() {
        return bkSetName;
    }

    public void setBkSetName(String bkSetName) {
        this.bkSetName = bkSetName;
    }

    public Integer getBkModuleId() {
        return bkModuleId;
    }

    public void setBkModuleId(Integer bkModuleId) {
        this.bkModuleId = bkModuleId;
    }

    public String getBkModuleName() {
        return bkModuleName;
    }

    public void setBkModuleName(String bkModuleName) {
        this.bkModuleName = bkModuleName;
    }

    public Integer getDefaultFlg() {
        return defaultFlg;
    }

    public void setDefaultFlg(Integer defaultFlg) {
        this.defaultFlg = defaultFlg;
    }
}
