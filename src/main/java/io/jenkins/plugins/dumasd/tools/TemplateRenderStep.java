package io.jenkins.plugins.dumasd.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.verb.POST;

@Setter
@Getter
@ToString
public class TemplateRenderStep extends Step implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateFile;

    private String outputFile;

    private Map<String, Object> vars;

    @DataBoundConstructor
    public TemplateRenderStep(String templateFile, String outputFile) {
        this.templateFile = templateFile;
        this.outputFile = outputFile;
    }

    @DataBoundSetter
    public void setVars(Map<String, Object> vars) {
        this.vars = vars;
    }

    public void setVars(String vars) {
        this.vars = JSON.parseObject(vars);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new TemplateRenderExecution(context, templateFile, outputFile, vars);
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
            return "templateRender";
        }

        @POST
        public FormValidation doCheckTemplateFile(@QueryParameter("templateFile") String templateFile) {
            if (StringUtils.isBlank(templateFile)) {
                return FormValidation.error("templateFile is required");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckOutputFile(@QueryParameter("outputFile") String outputFile) {
            if (StringUtils.isBlank(outputFile)) {
                return FormValidation.error("outputFile is required");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckVars(@QueryParameter("vars") String vars) {
            if (StringUtils.isNotBlank(vars)) {
                try {
                    JSON.parse(vars);
                } catch (JSONException e) {
                    return FormValidation.error("Illegal json format");
                }
            }
            return FormValidation.ok();
        }

        @Override
        public Step newInstance(@Nullable StaplerRequest req, @NonNull JSONObject formData) throws FormException {
            String templateFile = formData.getString("templateFile");
            String outputFile = formData.getString("outputFile");
            Object vars = formData.getOrDefault("vars", null);
            TemplateRenderStep step = new TemplateRenderStep(templateFile, outputFile);
            if (Objects.nonNull(vars) && StringUtils.isNotBlank(vars.toString())) {
                step.setVars(vars.toString());
            }
            return step;
        }
    }
}
