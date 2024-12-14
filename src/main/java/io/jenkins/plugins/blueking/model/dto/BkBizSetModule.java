package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BkBizSetModule implements Serializable {

    private static final long serialVersionUID = -6544147055314877349L;

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
}
