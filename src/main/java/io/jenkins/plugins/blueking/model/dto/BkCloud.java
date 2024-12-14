package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BkCloud implements Serializable {
    private static final long serialVersionUID = -8163931669268317797L;

    private String id;

    @JSONField(name = "bk_cloud_id")
    private String bkCloudId;

    @JSONField(name = "bk_cloud_name")
    private String bkCloudName;
}
