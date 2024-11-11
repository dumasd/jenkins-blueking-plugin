package io.jenkins.plugins.dumasd.tools;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Setter
@Getter
@ToString
public class TemplateRenderStep extends Step implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateFile;

    private String outputFile;

    private Map<String, Serializable> vars;

    @DataBoundConstructor
    public TemplateRenderStep(String templateFile, String outputFile) {
        this.templateFile = templateFile;
        this.outputFile = outputFile;
    }

    @DataBoundSetter
    public void setVars(Map<String, Serializable> vars) {
        this.vars = vars;
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
    }
}
