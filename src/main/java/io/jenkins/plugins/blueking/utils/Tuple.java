package io.jenkins.plugins.blueking.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tuple<L, R> {

    private L left;
    private R right;

    public Tuple() {}

    public Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
