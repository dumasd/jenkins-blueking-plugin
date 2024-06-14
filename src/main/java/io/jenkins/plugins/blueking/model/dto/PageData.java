package io.jenkins.plugins.blueking.model.dto;

import java.util.List;

public class PageData<T> {

    private Integer count;

    private List<T> info;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getInfo() {
        return info;
    }

    public void setInfo(List<T> info) {
        this.info = info;
    }
}
