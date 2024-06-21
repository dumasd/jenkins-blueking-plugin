package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
@Setter
@Getter
@ToString
public class BkHost {

    @JSONField(name = "bk_host_id")
    private String bkHostId;

    @JSONField(name = "bk_host_outerip")
    private String bkHostOuterip;

    @JSONField(name = "bk_host_innerip")
    private String bkHostInnerip;
}
