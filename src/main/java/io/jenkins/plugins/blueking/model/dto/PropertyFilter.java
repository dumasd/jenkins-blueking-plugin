package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.JSON;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PropertyFilter {

    private String condition;

    private String field;
    private String operator;
    private Serializable value;

    private List<PropertyFilter> rules = new ArrayList<>();

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static PropertyFilter createCondition(String condition, List<PropertyFilter> rules) {
        PropertyFilter filter = new PropertyFilter();
        filter.setCondition(condition);
        filter.setRules(rules);
        return filter;
    }

    public static PropertyFilter createField(String field, String operator, Serializable value) {
        PropertyFilter filter = new PropertyFilter();
        filter.setField(field);
        filter.setOperator(operator);
        filter.setValue(value);
        return filter;
    }
}
