package io.jenkins.plugins.dumasd.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.extra.template.engine.velocity.VelocityEngine;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import io.jenkins.plugins.dumasd.common.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import jenkins.MasterToSlaveFileCallable;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

public class TemplateRenderExecution extends SynchronousNonBlockingStepExecution<Map<String, String>> {

    private static final long serialVersionUID = 3007320632464334141L;

    private final String templateFile;

    private final String outputFile;

    private final Map<String, Object> vars;

    public TemplateRenderExecution(
            @NonNull StepContext context, String templateFile, String outputFile, Map<String, Object> vars) {
        super(context);
        this.templateFile = templateFile;
        this.outputFile = outputFile;
        this.vars = vars;
    }

    @Override
    protected Map<String, String> run() throws Exception {
        FilePath workspace = getContext().get(FilePath.class);
        TaskListener taskListener = getContext().get(TaskListener.class);
        Logger logger = new Logger("FreemarkerContentReplace", taskListener);
        FilePath templateFilePath = workspace.child(templateFile);
        FilePath outputFilePath = workspace.child(outputFile);
        if (templateFilePath.exists() || !templateFilePath.isDirectory()) {
            templateFilePath.act(new RemoteCallable(outputFilePath.getRemote(), vars));
        } else {
            logger.log("error: TemplateFile %s is not file", templateFilePath.getRemote());
            throw new FileNotFoundException("Template file " + templateFilePath.getRemote() + " not found");
        }
        return Collections.emptyMap();
    }

    private static class RemoteCallable extends MasterToSlaveFileCallable<Boolean> {
        private static final long serialVersionUID = 3524691324325868577L;
        private final String outputFile;
        private final Map<String, Object> vars;

        private RemoteCallable(String outputFile, Map<String, Object> vars) {
            this.outputFile = outputFile;
            this.vars = vars;
        }

        @Override
        public Boolean invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
            TemplateConfig templateConfig = new TemplateConfig();
            templateConfig.setCharset(StandardCharsets.UTF_8);
            templateConfig.setResourceMode(TemplateConfig.ResourceMode.STRING);
            templateConfig.setCustomEngine(VelocityEngine.class);
            TemplateEngine templateEngine = TemplateUtil.createEngine(templateConfig);
            String templateContent = FileUtil.readString(f, StandardCharsets.UTF_8);
            Template template = templateEngine.getTemplate(templateContent);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                template.render(vars, fos);
                fos.flush();
            }
            return Boolean.TRUE;
        }
    }
}
