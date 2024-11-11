package io.jenkins.plugins.blueking;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.dumasd.common.Logger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.configfiles.ConfigFiles;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Setter
@Getter
public class ReadPropertiesStep extends Step {
    /**
     * 文件ID
     */
    private String fileId;
    /**
     * 文件路径。文件ID优先
     */
    private String filePath;

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new ReadPropertiesStepExecution(context, fileId, filePath);
    }

    @DataBoundConstructor
    public ReadPropertiesStep() {}

    @DataBoundSetter
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @DataBoundSetter
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> classes = new HashSet<>();
            classes.add(Run.class);
            classes.add(TaskListener.class);
            classes.add(FilePath.class);
            return classes;
        }

        @Override
        public String getFunctionName() {
            return "readProperties";
        }
    }

    public static class ReadPropertiesStepExecution extends SynchronousNonBlockingStepExecution<Map<String, String>> {

        private final String fileId;
        private final String filePath;

        public ReadPropertiesStepExecution(@NonNull StepContext context, String fileId, String filePath) {
            super(context);
            this.fileId = fileId;
            this.filePath = filePath;
        }

        @Override
        protected Map<String, String> run() throws Exception {
            Run run = this.getContext().get(Run.class);
            FilePath workspace = this.getContext().get(FilePath.class);
            TaskListener taskListener = this.getContext().get(TaskListener.class);
            Logger logger = new Logger("ReadProperties", taskListener);
            Properties properties = new Properties();
            if (StringUtils.isNotBlank(fileId)) {
                logger.log("Read properties from fileId. fileId=%s", fileId);
                Config config = ConfigFiles.getByIdOrNull(run, fileId);
                if (Objects.isNull(config)) {
                    logger.log("warning: FileId %s does not exist, omitting from properties gathering", fileId);
                } else {
                    String fileContent = config.content;
                    properties.load(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));
                }
            } else if (StringUtils.isNotBlank(filePath)) {
                logger.log("Read properties from filePath. filePath=%s", filePath);
                FilePath f = workspace.child(filePath);
                if (f.exists() && !f.isDirectory()) {
                    try (InputStream is = f.read()) {
                        properties.load(is);
                    }
                } else if (f.isDirectory()) {
                    logger.log(
                            "warning: FilePath %s is a directory, omitting from properties gathering", f.getRemote());
                } else if (!f.exists()) {
                    logger.log(
                            "warning: FilePath %s does not exist, omitting from properties gathering", f.getRemote());
                }
            } else {
                throw new IllegalArgumentException("FileId and FilePath all blank!!");
            }
            Map<String, String> result = new LinkedHashMap<>();
            properties.forEach((k, v) -> result.put(k.toString(), v.toString()));
            return result;
        }
    }
}
