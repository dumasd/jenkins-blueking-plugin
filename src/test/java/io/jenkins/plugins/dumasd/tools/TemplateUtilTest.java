package io.jenkins.plugins.dumasd.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.extra.template.engine.velocity.VelocityEngine;
import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.java.Log;

/**
 * @author Bruce.Wu
 * @date 2024-11-11
 */
@Log
public class TemplateUtilTest {

    // @Test
    public void test() {
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setCharset(StandardCharsets.UTF_8);
        templateConfig.setResourceMode(TemplateConfig.ResourceMode.STRING);
        templateConfig.setCustomEngine(VelocityEngine.class);
        TemplateEngine templateEngine = TemplateUtil.createEngine(templateConfig);

        File file = new File(
                "/Users/wukai/IdeaProjects/Opensource/jenkins-blueking-plugin/templates/k8s_deployment_template.yaml.vm");
        String templateStr = FileUtil.readString(file, StandardCharsets.UTF_8);
        Template template = templateEngine.getTemplate(templateStr);
        StringWriter stringWriter = new StringWriter();
        Map<String, Serializable> vars = new HashMap<>();
        vars.put("APP_NAME", "devops");
        vars.put("NAMESPACE", "default");
        vars.put("VERSION", "1.0.0");
        vars.put("RESTART_TIME", "2024-11-11 18:00:00");

        Map<String, Object> configmapVolume = new HashMap<>();
        configmapVolume.put("VOLUME_NAME", "config");
        configmapVolume.put("CONFIGMAP_NAME", "cm-cfg");
        configmapVolume.put("MOUNT_PATH", "/home/cfg");

        vars.put("CONFIGMAP_VOLUMES", new ArrayList<>(Collections.singletonList(configmapVolume)));

        template.render(vars, stringWriter);
        log.info(stringWriter.toString());
    }
}
