package io.jenkins.plugins.blueking;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.blueking.client.BluekingCCClient;
import io.jenkins.plugins.blueking.model.dto.BkBizSetModule;
import io.jenkins.plugins.blueking.model.dto.BkHost;
import io.jenkins.plugins.blueking.model.dto.PageData;
import io.jenkins.plugins.blueking.model.req.ListBizHostsRequest;
import io.jenkins.plugins.blueking.model.req.SearchSetRequest;
import io.jenkins.plugins.dumasd.common.Logger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

@Setter
@Getter
public class BkCcHostsBuilder extends Builder implements SimpleBuildStep {

    /**
     * Blueking API base url
     */
    private final String baseUrl;

    private final String bkAppCode;
    private final String bkAppSecret;

    private final String bkUsername;
    /**
     * 业务名称或集群ID
     */
    private final String bkBiz;
    /**
     * 集群名称或集群ID
     */
    private final String bkSet;
    /**
     * 模块ID列表，逗号分隔
     */
    private final String bkModules;
    /**
     * 外网IP列表，逗号分隔
     */
    private String outerIpVariable = "BK_OUTER_IPS";
    /**
     * 内网IP列表，逗号分隔
     */
    private String innerIpVariable = "BK_INNER_IPS";

    @DataBoundConstructor
    public BkCcHostsBuilder(
            @NonNull String baseUrl,
            @NonNull String bkAppCode,
            @NonNull String bkAppSecret,
            @NonNull String bkUsername,
            @NonNull String bkBiz,
            @NonNull String bkSet,
            @NonNull String bkModules) {
        this.baseUrl = baseUrl;
        this.bkAppCode = bkAppCode;
        this.bkAppSecret = bkAppSecret;
        this.bkUsername = bkUsername;
        this.bkBiz = bkBiz;
        this.bkSet = bkSet;
        this.bkModules = bkModules;
    }

    @DataBoundSetter
    public void setOuterIpVariable(String outerIpVariable) {
        this.outerIpVariable = outerIpVariable;
    }

    @DataBoundSetter
    public void setInnerIpVariable(String innerIpVariable) {
        this.innerIpVariable = innerIpVariable;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        Logger logger = new Logger(listener);
        String baseUrlEx = env.expand(baseUrl);
        String bkAppCodeEx = env.expand(bkAppCode);
        String bkAppSecretEx = env.expand(bkAppSecret);
        String bkUsernameEx = env.expand(bkUsername);
        String bkBizEx = env.expand(bkBiz);
        String bkSetEx = env.expand(bkSet);
        String bkModulesEx = env.expand(bkModules);
        logger.log(
                "Start fetch host. baseUrl:%s, biz:%s, set:%s, modules:%s", baseUrlEx, bkBizEx, bkSetEx, bkModulesEx);
        BluekingCCClient client = new BluekingCCClient(baseUrlEx, bkAppCodeEx, bkAppSecretEx, bkUsernameEx);
        BkBizSetModule biz = client.findBiz(bkBizEx);
        if (Objects.isNull(biz)) {
            logger.log("CMDB Business not found, please check your parameter");
            run.setResult(Result.FAILURE);
            return;
        }

        SearchSetRequest searchSetRequest = new SearchSetRequest();
        searchSetRequest.setBkBizId(biz.getBkBizId());
        searchSetRequest.addIdOrNameCondition(bkSetEx);
        PageData<BkBizSetModule> searchSetData = client.searchSet(searchSetRequest);
        if (searchSetData.getCount() <= 0) {
            logger.log("CMDB set not found, please check your parameter");
            run.setResult(Result.FAILURE);
            return;
        }

        if (searchSetData.getCount() > 1) {
            logger.log("Found CMDB set more than 1, please check your blueking");
            run.setResult(Result.FAILURE);
            return;
        }
        BkBizSetModule set = searchSetData.getInfo().get(0);

        // biz module ids
        List<Integer> moduleIds =
                Arrays.stream(bkModulesEx.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        // 搜索host,拼接IP
        ListBizHostsRequest listBizHostsRequest = new ListBizHostsRequest();
        listBizHostsRequest.setBkBizId(biz.getBkBizId());
        listBizHostsRequest.setBkSetIds(Collections.singletonList(set.getBkSetId()));
        listBizHostsRequest.setBkModuleIds(moduleIds);
        listBizHostsRequest.getPage().setLimit(499);
        PageData<BkHost> listBizHostsData = client.listBizHosts(listBizHostsRequest);
        if (listBizHostsData.getCount() <= 0) {
            logger.log("Not found host, please check your parameter");
            run.setResult(Result.FAILURE);
            return;
        }
        String innerIps = listBizHostsData.getInfo().stream()
                .map(BkHost::getBkHostInnerip)
                .distinct()
                .collect(Collectors.joining(","));
        String outerIps = listBizHostsData.getInfo().stream()
                .map(BkHost::getBkHostOuterip)
                .distinct()
                .collect(Collectors.joining(","));
        logger.log("Found inner ips. [" + innerIps + "]");
        logger.log("Found outer ips. [" + outerIps + "]");

        EnvVars envVars = new EnvVars();
        envVars.overrideAll(env);
        envVars.overrideAll(EnvVars.masterEnvVars);
        envVars.put(innerIpVariable, innerIps);
        envVars.put(outerIpVariable, outerIps);
        run.addAction(new EnvInjectAction(envVars));
    }

    @Symbol("bkCC")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @POST
        public FormValidation doCheckBaseUrl(@QueryParameter String baseUrl) throws IOException, ServletException {
            try {
                new URL(baseUrl);
            } catch (MalformedURLException e) {
                return FormValidation.error("Invalid URL");
            }
            if (baseUrl.endsWith("/")) {
                return FormValidation.error("The URL cannot have a trailing slash");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckBkAppCode(@QueryParameter String bkAppCode) throws IOException, ServletException {
            if (bkAppCode.isEmpty()) {
                return FormValidation.error("Please set a app code");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckBkUsername(@QueryParameter String bkUsername)
                throws IOException, ServletException {
            if (bkUsername.isEmpty()) {
                return FormValidation.error("Please set a username");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckBkBiz(@QueryParameter String bkBiz) throws IOException, ServletException {
            if (bkBiz.isEmpty()) {
                return FormValidation.error("Please set a cmdb business id or name");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckBkSet(@QueryParameter String bkSet) throws IOException, ServletException {
            if (bkSet.isEmpty()) {
                return FormValidation.error("Please set a cmdb set id or name");
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckBkModules(@QueryParameter String bkModules) throws IOException, ServletException {
            if (bkModules.isEmpty()) {
                return FormValidation.error("Please set a cmdb module id list, split with ','");
            }
            String[] ss = bkModules.split(",");
            for (String s : ss) {
                try {
                    Integer.parseInt(s);
                } catch (Exception e) {
                    return FormValidation.error("Illegal cmdb module id. [" + s + "]");
                }
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.BkCcHostsBuilder_DescriptorImpl_DisplayName();
        }
    }
}
