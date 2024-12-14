package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
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
public class BkHost implements Serializable {

    private static final long serialVersionUID = -1126805684671991230L;

    private boolean selected;

    @JSONField(name = "bk_host_id")
    private String bkHostId;

    @JSONField(name = "bk_host_outerip")
    private String bkHostOuterip;

    @JSONField(name = "bk_host_innerip")
    private String bkHostInnerip;

    @JSONField(name = "host_name")
    private String hostName;

    @JSONField(name = "bk_state")
    private String bkState;

    @JSONField(name = "bk_cloud_id")
    private String bkCloudId;

    @JSONField(name = "bk_cloud_name")
    private String bkCloudName;
}
