package io.jenkins.plugins.blueking.model.dto;

/**
 * @author Bruce.Wu
 * @date 2024-06-13
 */
public class Page {

    private Integer start;
    private Integer limit;

    public Page() {
        this(0, 100);
    }

    public Page(int start, int limit) {
        this.start = start;
        this.limit = limit;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
