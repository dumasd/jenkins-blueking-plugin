package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BkInstObj implements Serializable {
    private static final long serialVersionUID = 3653724902228435789L;

    private String id;

    @JSONField(name = "bk_inst_id")
    private String bkInstId;

    @JSONField(name = "bk_inst_name")
    private String bkInstName;

    @JSONField(name = "bk_obj_id")
    private String bkObjId;

    @JSONField(name = "bk_obj_name")
    private String bkObjName;

    @JSONField(name = "default")
    private Integer def;

    private List<BkInstObj> child;
}
