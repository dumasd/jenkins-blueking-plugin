package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
public class BkHost {

    @JSONField(name = "bk_host_id")
    private String bkHostId;

    @JSONField(name = "bk_host_outerip")
    private String bkHostOuterip;

    @JSONField(name = "bk_host_innerip")
    private String bkHostInnerip;

    public String getBkHostId() {
        return bkHostId;
    }

    public void setBkHostId(String bkHostId) {
        this.bkHostId = bkHostId;
    }

    public String getBkHostOuterip() {
        return bkHostOuterip;
    }

    public void setBkHostOuterip(String bkHostOuterip) {
        this.bkHostOuterip = bkHostOuterip;
    }

    public String getBkHostInnerip() {
        return bkHostInnerip;
    }

    public void setBkHostInnerip(String bkHostInnerip) {
        this.bkHostInnerip = bkHostInnerip;
    }
}
