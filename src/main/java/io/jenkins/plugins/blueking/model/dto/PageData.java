package io.jenkins.plugins.blueking.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageData<T> {

    private Integer count;

    private List<T> info;
}
