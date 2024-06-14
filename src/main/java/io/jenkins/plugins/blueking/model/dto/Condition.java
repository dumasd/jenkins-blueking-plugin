package io.jenkins.plugins.blueking.model.dto;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
public class Condition {

    private String field;

    private String operator;

    private Object value;

    public Condition(String field, String operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public Condition() {}

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

    public static Condition eq(String field, Object value) {
        return new Condition(field, "$eq", value);
    }

    public static Condition ne(String field, Object value) {
        return new Condition(field, "$ne", value);
    }
}
