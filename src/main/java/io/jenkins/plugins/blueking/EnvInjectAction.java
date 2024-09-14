package io.jenkins.plugins.blueking;

import hudson.EnvVars;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Run;

/**
 * @author Bruce.Wu
 * @date 2024-09-14
 */
public class EnvInjectAction implements EnvironmentContributingAction {

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
