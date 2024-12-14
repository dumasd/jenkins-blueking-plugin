package io.jenkins.plugins.blueking.config;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Getter
public class BkBizInstTopoConfig extends AbstractDescribableImpl<BkBizInstTopoConfig> implements Serializable {
    private static final long serialVersionUID = -5082500295909846095L;

    private String bkInstId;

    private String bkInstName;

    private String bkObjId;

    private String bkObjName;

    private List<BkBizInstTopoConfig> child;

    @DataBoundConstructor
    public BkBizInstTopoConfig(String bkInstId, String bkInstName, String bkObjId, String bkObjName) {
        this.bkInstId = bkInstId;
        this.bkInstName = bkInstName;
        this.bkObjId = bkObjId;
        this.bkObjName = bkObjName;
    }

    @DataBoundSetter
    public void setChild(List<BkBizInstTopoConfig> child) {
        this.child = child;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<BkBizInstTopoConfig> {}
}
