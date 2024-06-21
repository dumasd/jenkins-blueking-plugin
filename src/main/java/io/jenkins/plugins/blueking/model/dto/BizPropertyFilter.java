package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BizPropertyFilter {

    private String condition;

    private List<Object> rules = new ArrayList<>();

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Setter
    @Getter
    public static class Rule {
        private String field;
        private String operator;
        private transient Object value;
    }
}
