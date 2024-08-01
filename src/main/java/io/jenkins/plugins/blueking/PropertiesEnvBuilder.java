package io.jenkins.plugins.blueking;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.blueking.config.PropertiesEnvConfig;
import io.jenkins.plugins.blueking.utils.Logger;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.configfiles.ConfigFiles;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * 从properties文件读取主机信息
 *
 * @author Bruce.Wu
 * @date 2024-07-07
 */
@Getter
@Setter
public class PropertiesEnvBuilder extends Builder implements SimpleBuildStep {
    /**
     * 文件ID
     */
    private String fileId;
    /**
     * 文件路径。文件ID优先
     */
    private String filePath;

    private List<PropertiesEnvConfig> configs;

    @DataBoundConstructor
    public PropertiesEnvBuilder(List<PropertiesEnvConfig> configs) {
        this.configs = configs;
    }

    @DataBoundSetter
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @DataBoundSetter
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Descriptor<PropertiesEnvConfig> getConfigDescriptor() {
        return Jenkins.get().getDescriptorByType(PropertiesEnvConfig.PropertiesEnvConfigDescriptor.class);
    }

    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        Logger logger = new Logger("PropertiesEnvBuilder", listener);

        Properties properties = new Properties();
        if (StringUtils.isNotBlank(fileId)) {
            String fileIdEx = env.expand(fileId);
            logger.log("Read properties from fileId. fileId=%s", fileIdEx);
            Config config = ConfigFiles.getByIdOrNull(run, fileIdEx);
            if (Objects.isNull(config)) {
                throw new RuntimeException("Config File:[" + fileIdEx + "] not found");
            }
            String fileContent = config.content;
            properties.load(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));
        } else if (StringUtils.isNotBlank(filePath)) {
            String filePathEx = env.expand(filePath);
            logger.log("Read properties from filePath. filePath=%s", filePathEx);
            try (FileInputStream fis = new FileInputStream(filePathEx)) {
                properties.load(fis);
            }
        } else {
            throw new RuntimeException("FileId and FilePath all blank!!");
        }

        EnvVars envVars = new EnvVars();
        envVars.overrideAll(env);
        envVars.overrideAll(EnvVars.masterEnvVars);
        for (PropertiesEnvConfig config : configs) {
            String keyEx = env.expand(config.getKey());
            String envEx = env.expand(config.getEnv());
            if (StringUtils.isBlank(keyEx)) {
                throw new RuntimeException("key is blank.");
            }
            if (StringUtils.isBlank(envEx)) {
                throw new RuntimeException("env is blank.");
            }
            String value = properties.getProperty(keyEx);
            String valueEx = env.expand(value);
            if (StringUtils.isNotEmpty(valueEx)) {
                logger.log("Get environment from properties file. key=%s, env=%s, value=%s", keyEx, envEx, valueEx);
                envVars.put(envEx, valueEx);
            } else if (StringUtils.isNotEmpty(config.getDefaultValue())) {
                envVars.put(envEx, config.getDefaultValue());
                logger.log(
                        "Get environment from default value. key=%s, env=%s, value=%s",
                        keyEx, envEx, config.getDefaultValue());
            } else {
                logger.log("Get environment. key=%s, env=%s, value is empty!", keyEx, envEx);
            }
        }
        run.addAction(new EnvInjectAction(envVars));
    }

    @Symbol("propertiesEnv")
    @Extension
    public static final class PropertiesEnvDescriptor extends BuildStepDescriptor<Builder> {

        @POST
        public FormValidation doCheckKey(@QueryParameter("key") String key) throws IOException, ServletException {
            if (StringUtils.isBlank(key)) {
                return FormValidation.error("Please set key");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckConfigs(@QueryParameter("configs") List<PropertiesEnvConfig> configs)
                throws IOException, ServletException {
            if (CollectionUtils.isEmpty(configs)) {
                return FormValidation.error("Please add key-env config");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Get environments from properties file";
        }
    }

    public static class EnvInjectAction implements EnvironmentContributingAction {

        private final EnvVars envVars;

        public EnvInjectAction(EnvVars envVars) {
            this.envVars = envVars;
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return null;
        }

        @Override
        public void buildEnvironment(Run<?, ?> run, EnvVars env) {
            env.overrideAll(envVars);
        }
    }
}
