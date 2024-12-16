package io.jenkins.plugins.blueking.model.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SelectedHost implements Serializable {

    private static final long serialVersionUID = 3149159959212400924L;

    private boolean selected;

    private String id;

    private String innerip;

    private String outerip;

    private String name;

    public SelectedHost() {}

    public SelectedHost(String id, String innerip, String outerip, String name) {
        this.id = id;
        this.innerip = innerip;
        this.outerip = outerip;
        this.name = name;
    }
}
