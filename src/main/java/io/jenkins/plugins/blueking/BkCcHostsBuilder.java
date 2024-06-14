package io.jenkins.plugins.blueking;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.blueking.client.BluekingCCClient;
import io.jenkins.plugins.blueking.model.dto.BkBizSetModule;
import io.jenkins.plugins.blueking.model.dto.BkHost;
import io.jenkins.plugins.blueking.model.dto.Page;
import io.jenkins.plugins.blueking.model.dto.PageData;
import io.jenkins.plugins.blueking.model.req.ListBizHostsRequest;
import io.jenkins.plugins.blueking.model.req.SearchBusinessRequest;
import io.jenkins.plugins.blueking.model.req.SearchSetRequest;
import io.jenkins.plugins.blueking.utils.Logger;
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
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

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
            String baseUrl,
            String bkAppCode,
            String bkAppSecret,
            String bkUsername,
            String bkBiz,
            String bkSet,
            String bkModules) {
        this.baseUrl = baseUrl;
        this.bkAppCode = bkAppCode;
        this.bkAppSecret = bkAppSecret;
        this.bkUsername = bkUsername;
        this.bkBiz = bkBiz;
        this.bkSet = bkSet;
        this.bkModules = bkModules;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBkAppCode() {
        return bkAppCode;
    }

    public String getBkAppSecret() {
        return bkAppSecret;
    }

    public String getBkUsername() {
        return bkUsername;
    }

    public String getBkBiz() {
        return bkBiz;
    }

    public String getBkSet() {
        return bkSet;
    }

    public String getBkModules() {
        return bkModules;
    }

    @DataBoundSetter
    public void setOuterIpVariable(String outerIpVariable) {
        this.outerIpVariable = outerIpVariable;
    }

    @DataBoundSetter
    public void setInnerIpVariable(String innerIpVariable) {
        this.innerIpVariable = innerIpVariable;
    }

    public String getOuterIpVariable() {
        return outerIpVariable;
    }

    public String getInnerIpVariable() {
        return innerIpVariable;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        Logger logger = new Logger(listener.getLogger());
        logger.log("Start........");
        BluekingCCClient client = new BluekingCCClient(logger, baseUrl, bkAppCode, bkAppSecret, bkUsername);
        BkBizSetModule biz = findBiz(client);
        if (Objects.isNull(biz)) {
            logger.log("Not found business, please check your parameter");
            run.setResult(Result.FAILURE);
            return;
        }

        SearchSetRequest searchSetRequest = new SearchSetRequest();
        searchSetRequest.setBkBizId(biz.getBkBizId());
        searchSetRequest.addIdOrNameCondition(bkSet);
        PageData<BkBizSetModule> searchSetData = client.searchSet(searchSetRequest);
        if (searchSetData.getCount() <= 0) {
            logger.log("Not found set, please check your parameter");
            run.setResult(Result.FAILURE);
            return;
        }

        if (searchSetData.getCount() > 1) {
            logger.log("Found set more than 1, please check your blueking");
            run.setResult(Result.FAILURE);
            return;
        }
        BkBizSetModule set = searchSetData.getInfo().get(0);

        // biz module ids
        List<Integer> moduleIds =
                Arrays.stream(bkModules.split(",")).map(Integer::parseInt).collect(Collectors.toList());

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

    private BkBizSetModule findBiz(BluekingCCClient client) {
        SearchBusinessRequest searchBusinessRequest = new SearchBusinessRequest();
        Page page = new Page();
        page.setLimit(200);
        searchBusinessRequest.setPage(page);
        PageData<BkBizSetModule> searchBusinessData = client.searchBusiness(searchBusinessRequest);
        if (searchBusinessData.getCount() <= 0) {
            return null;
        }
        BkBizSetModule biz = null;
        for (BkBizSetModule bsm : searchBusinessData.getInfo()) {
            if (Objects.equals(bsm.getBkBizId().toString(), bkBiz) || Objects.equals(bsm.getBkBizName(), bkBiz)) {
                biz = bsm;
                break;
            }
        }
        return biz;
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

    public static class EnvInjectAction implements EnvironmentContributingAction {

        private EnvVars envVars;

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
        public void buildEnvironment(@NonNull Run<?, ?> run, @NonNull EnvVars env) {
            env.overrideAll(envVars);
        }
    }
}
