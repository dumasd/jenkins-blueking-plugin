package io.jenkins.plugins.blueking.model.req;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Bruce.Wu
 * @date 2024-12-11
 */
@Setter
@Getter
@ToString
public class SearchBizInstTopoRequest extends BaseRequest {

    @JSONField(name = "bk_supplier_account")
    private String bkSupplierAccount;

    @JSONField(name = "bk_biz_id")
    private Integer bkBizId;

    private Integer level;
}
