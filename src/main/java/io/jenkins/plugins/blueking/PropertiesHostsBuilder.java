package io.jenkins.plugins.blueking;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.blueking.utils.Logger;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import javax.servlet.ServletException;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;
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
public class PropertiesHostsBuilder extends Builder implements SimpleBuildStep {
    /**
     * 文件ID
     */
    private String fileId;
    /**
     * 文件路径。文件ID优先
     */
    private String filePath;
    /**
     * key
     */
    private String key;
    /**
     * 读取到的IP变量名称
     */
    private String ipVariable = "BK_IPS";

    @DataBoundConstructor
    public PropertiesHostsBuilder(String key) {
        this.key = key;
    }

    @DataBoundSetter
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @DataBoundSetter
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @DataBoundSetter
    public void setIpVariable(String ipVariable) {
        this.ipVariable = ipVariable;
    }

    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        Logger logger = new Logger(listener);

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
        String keyEx = env.expand(key);
        String value = properties.getProperty(keyEx);
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("ip value is blank. key=" + keyEx);
        }
        logger.log("Get ip. key=%s, ips=%s", keyEx, value);
        EnvVars envVars = new EnvVars();
        envVars.overrideAll(env);
        envVars.overrideAll(EnvVars.masterEnvVars);
        envVars.put(ipVariable, value);
        run.addAction(new EnvInjectAction(envVars));
    }

    @Symbol("propertiesCC")
    @Extension
    public static final class PropertiesHostsDescriptor extends BuildStepDescriptor<Builder> {

        @POST
        public FormValidation doCheckKey(@QueryParameter("key") String key) throws IOException, ServletException {
            if (StringUtils.isBlank(key)) {
                return FormValidation.error("Please set key");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckFile(
                @QueryParameter("fileId") String fileId, @QueryParameter("filePath") String filePath)
                throws IOException, ServletException {
            if (StringUtils.isBlank(fileId) && StringUtils.isBlank(filePath)) {
                return FormValidation.error("Please set fileId or filePath");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckIpVariable(@QueryParameter("ipVariable") String ipVariable)
                throws IOException, ServletException {
            if (StringUtils.isBlank(ipVariable)) {
                return FormValidation.error("Please set ipVariable");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Get Host from properties file";
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
