package io.jenkins.plugins.blueking.model.dto;

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
public class Condition implements Serializable {

    private static final long serialVersionUID = 4509601218964404537L;
    private String field;

    private String operator;

    private Object value;

    public Condition(String field, String operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public Condition() {}

    public static Condition eq(String field, Object value) {
        return new Condition(field, "$eq", value);
    }

    public static Condition ne(String field, Object value) {
        return new Condition(field, "$ne", value);
    }
}
