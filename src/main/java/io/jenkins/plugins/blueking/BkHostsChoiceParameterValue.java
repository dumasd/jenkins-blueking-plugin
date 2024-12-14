package io.jenkins.plugins.blueking;

import hudson.EnvVars;
import hudson.model.ParameterValue;
import hudson.model.Run;
import io.jenkins.plugins.blueking.utils.Utils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BkHostsChoiceParameterValue extends ParameterValue {
    private static final long serialVersionUID = 1759781287322880602L;

    private final String outerEnv;
    private final String innerEnv;
    private final Set<String> outerIps;
    private final Set<String> innerIps;

    public BkHostsChoiceParameterValue(
            String name, String outerEnv, String innerEnv, Set<String> outerIps, Set<String> innerIps) {
        super(name);
        this.outerEnv = outerEnv;
        this.innerEnv = innerEnv;
        this.outerIps = outerIps;
        this.innerIps = innerIps;
    }

    @Override
    public void buildEnvironment(Run<?, ?> build, EnvVars env) {
        String outerKey = outerEnv;
        if (Utils.isNullOrEmpty(outerKey)) {
            outerKey = getName() + "_OUTER";
        }
        String innerKey = innerEnv;
        if (Utils.isNullOrEmpty(innerEnv)) {
            innerKey = getName() + "_INNER";
        }
        env.put(outerKey, String.join(",", outerIps));
        env.put(innerKey, String.join(",", innerIps));
    }

    @Override
    public Object getValue() {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        result.put("inner", innerIps);
        result.put("outer", outerIps);
        return result;
    }
}
