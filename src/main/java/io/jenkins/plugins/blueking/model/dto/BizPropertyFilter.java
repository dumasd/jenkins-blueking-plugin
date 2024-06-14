package io.jenkins.plugins.blueking.model.dto;

import java.util.ArrayList;
import java.util.List;

public class BizPropertyFilter {

    private String condition;

    private List<Object> rules = new ArrayList<>();

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<Object> getRules() {
        return rules;
    }

    public void setRules(List<Object> rules) {
        this.rules = rules;
    }

    public static class Rule {
        private String field;
        private String operator;
        private Object value;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
