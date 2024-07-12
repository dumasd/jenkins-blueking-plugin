package io.jenkins.plugins.blueking.config;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

import java.io.Serializable;

import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

@Setter
@Getter
@NoArgsConstructor
public class PropertiesEnvConfig implements Serializable, Describable<PropertiesEnvConfig> {
    /**
     * properties 文件中的key
     */
    private String key;

    /**
     * properties value写入的环境变量名称
     */
    private String env;

    /**
     * properties value
     */
    private String defaultValue;


    @DataBoundConstructor
    public PropertiesEnvConfig(String key, String env) {
        this.key = key;
        this.env = env;
    }

    @DataBoundSetter
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Descriptor<PropertiesEnvConfig> getDescriptor() {
        return Jenkins.get().getDescriptorByType(PropertiesEnvConfigDescriptor.class);
    }

    @Extension
    public static class PropertiesEnvConfigDescriptor extends Descriptor<PropertiesEnvConfig> {

        public FormValidation doTest(@QueryParameter("key") String key, @QueryParameter("env") String env) {
            if (StringUtils.isBlank(key)) {
                return FormValidation.error("Please input key");
            }
            if (StringUtils.isBlank(env)) {
                return FormValidation.error("Please input env name");
            }
            return FormValidation.ok("Validate Success!");
        }
    }
}
