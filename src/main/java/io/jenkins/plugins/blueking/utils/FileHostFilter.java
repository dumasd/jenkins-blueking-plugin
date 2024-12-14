package io.jenkins.plugins.blueking.utils;

import io.jenkins.plugins.blueking.model.dto.FileHost;
import java.util.function.Predicate;

public class FileHostFilter implements Predicate<FileHost> {

    private final String keyword;

    public FileHostFilter(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean test(FileHost fileHost) {
        if (Utils.isNullOrEmpty(keyword)) {
            return true;
        }

        if (Utils.isNotEmpty(fileHost.getHostInnerip())
                && fileHost.getHostInnerip().contains(keyword)) {
            return true;
        }
        if (Utils.isNotEmpty(fileHost.getHostOuterip())
                && fileHost.getHostOuterip().contains(keyword)) {
            return true;
        }
        if (Utils.isNotEmpty(fileHost.getHostName()) && fileHost.getHostName().contains(keyword)) {
            return true;
        }
        return Utils.isNotEmpty(fileHost.getModule()) && fileHost.getModule().contains(keyword);
    }
}
