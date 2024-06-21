package io.jenkins.plugins.blueking.model.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
@Setter
@Getter
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer start;
    private Integer limit;

    public Page() {
        this(0, 100);
    }

    public Page(int start, int limit) {
        this.start = start;
        this.limit = limit;
    }
}
