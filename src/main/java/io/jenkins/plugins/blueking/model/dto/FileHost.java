package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileHost implements Serializable {
    private static final long serialVersionUID = -2050562811687901334L;

    private boolean selected;

    @JSONField(name = "host_id")
    private String hostId;

    @JSONField(name = "host_innerip")
    private String hostInnerip;

    @JSONField(name = "host_outerip")
    private String hostOuterip;

    @JSONField(name = "host_name")
    private String hostName;

    private String module;
}
