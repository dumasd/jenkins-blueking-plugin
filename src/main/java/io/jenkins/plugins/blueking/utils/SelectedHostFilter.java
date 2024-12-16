package io.jenkins.plugins.blueking.utils;

import io.jenkins.plugins.blueking.model.dto.SelectedHost;
import java.util.function.Predicate;

public class SelectedHostFilter implements Predicate<SelectedHost> {

    private final String keyword;

    public SelectedHostFilter(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean test(SelectedHost e) {
        if (Utils.isNullOrEmpty(keyword)) {
            return true;
        }

        if (Utils.isNotEmpty(e.getInnerip()) && e.getInnerip().contains(keyword)) {
            return true;
        }
        if (Utils.isNotEmpty(e.getOuterip()) && e.getOuterip().contains(keyword)) {
            return true;
        }
        return Utils.isNotEmpty(e.getName()) && e.getName().contains(keyword);
    }
}
