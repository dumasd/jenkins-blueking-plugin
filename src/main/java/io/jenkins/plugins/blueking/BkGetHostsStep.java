package io.jenkins.plugins.blueking;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import io.jenkins.plugins.blueking.client.BluekingCCClient;
import io.jenkins.plugins.blueking.model.dto.BkBizSetModule;
import io.jenkins.plugins.blueking.model.dto.BkHost;
import io.jenkins.plugins.blueking.model.dto.Page;
import io.jenkins.plugins.blueking.model.dto.PageData;
import io.jenkins.plugins.blueking.model.req.ListBizHostsRequest;
import io.jenkins.plugins.blueking.model.req.SearchBusinessRequest;
import io.jenkins.plugins.blueking.model.req.SearchSetRequest;
import io.jenkins.plugins.blueking.utils.BluekingException;
import io.jenkins.plugins.blueking.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import jenkins.MasterToSlaveFileCallable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Getter
@Setter
@ToString
@Log
public class BkGetHostsStep extends Step implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Blueking API base url
     */
    private final String baseUrl;

    private final String appCode;

    private final String appSecret;

    private final String username;
    /**
     * 业务名称或集群ID
     */
    private final String biz;
    /**
     * 集群名称或集群ID
     */
    private final String set;
    /**
     * 模块ID列表，逗号分隔
     */
    private final String modules;
    /**
     * 外网IP列表，逗号分隔
     */
    private String outerIpVariable = "BK_OUTER_IPS";
    /**
     * 内网IP列表，逗号分隔
     */
    private String innerIpVariable = "BK_INNER_IPS";

    private boolean runOnAgent;

    @DataBoundConstructor
    public BkGetHostsStep(
            @NonNull String baseUrl,
            @NonNull String appCode,
            @NonNull String appSecret,
            @NonNull String username,
            @NonNull String biz,
            @NonNull String set,
            @NonNull String modules) {
        this.baseUrl = baseUrl;
        this.appCode = appCode;
        this.appSecret = appSecret;
        this.username = username;
        this.biz = biz;
        this.set = set;
        this.modules = modules;
    }

    @DataBoundSetter
    public void setInnerIpVariable(String innerIpVariable) {
        this.innerIpVariable = innerIpVariable;
    }

    @DataBoundSetter
    public void setOuterIpVariable(String outerIpVariable) {
        this.outerIpVariable = outerIpVariable;
    }

    @DataBoundSetter
    public void setRunOnAgent(boolean runOnAgent) {
        this.runOnAgent = runOnAgent;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new StepExecutionImpl(context, this);
    }

    public static class StepExecutionImpl extends SynchronousNonBlockingStepExecution<Map<String, Object>> {
        private static final long serialVersionUID = -1872031188937161306L;

        private final BkGetHostsStep step;

        public StepExecutionImpl(@NonNull StepContext context, BkGetHostsStep step) {
            super(context);
            this.step = step;
        }

        @Override
        protected Map<String, Object> run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            if (Objects.isNull(taskListener)) {
                throw new NullPointerException("Step task listener is null");
            }
            Logger logger = new Logger("BkGetHosts", taskListener);
            Run<?, ?> run = getContext().get(Run.class);
            if (Objects.isNull(run)) {
                throw new NullPointerException("Step run is null");
            }

            logger.log(
                    "Get hosts. baseUrl:%s, biz:%s, set:%s, modules:%s",
                    step.getBaseUrl(), step.getBiz(), step.getSet(), step.getModules());

            BkGetHostsCallable callable = new BkGetHostsCallable(
                    step.getBaseUrl(),
                    step.getAppCode(),
                    step.getAppSecret(),
                    step.getUsername(),
                    step.getBiz(),
                    step.getSet(),
                    step.getModules());
            Map<String, Object> result;
            if (step.isRunOnAgent()) {
                FilePath workspace = getContext().get(FilePath.class);
                if (Objects.isNull(workspace)) {
                    throw new NullPointerException("Step workspace is null");
                }
                result = workspace.act(callable);
            } else {
                result = callable.invoke(null, null);
            }

            String innerIps =
                    Objects.requireNonNullElse(result.get("innerIps"), "").toString();

            String outerIps =
                    Objects.requireNonNullElse(result.get("outerIps"), "").toString();

            logger.log("Found inner ips. [" + innerIps + "]");
            logger.log("Found outer ips. [" + outerIps + "]");

            // 放入环境变量
            EnvVars env = run.getEnvironment(taskListener);
            EnvVars envVars = new EnvVars();
            envVars.overrideAll(env);
            envVars.overrideAll(EnvVars.masterEnvVars);
            envVars.put(step.getInnerIpVariable(), innerIps);
            envVars.put(step.getOuterIpVariable(), outerIps);
            run.addAction(new EnvInjectAction(envVars));

            return result;
        }
    }

    public static class BkGetHostsCallable extends MasterToSlaveFileCallable<Map<String, Object>> {

        /**
         * Blueking API base url
         */
        private final String baseUrl;

        private final String appCode;
        private final String appSecret;

        private final String username;
        /**
         * 业务名称或集群ID
         */
        private final String biz;
        /**
         * 集群名称或集群ID
         */
        private final String set;
        /**
         * 模块ID列表，逗号分隔
         */
        private final String modules;

        public BkGetHostsCallable(
                String baseUrl,
                String appCode,
                String appSecret,
                String username,
                String biz,
                String set,
                String modules) {
            this.baseUrl = baseUrl;
            this.appCode = appCode;
            this.appSecret = appSecret;
            this.username = username;
            this.biz = biz;
            this.set = set;
            this.modules = modules;
        }

        @Override
        public Map<String, Object> invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
            BluekingCCClient client = new BluekingCCClient(baseUrl, appCode, appSecret, username);
            BkBizSetModule bkBiz = findBiz(client, biz);
            if (Objects.isNull(bkBiz)) {
                throw new BluekingException("CMDB Business not found, please check your parameter");
            }

            SearchSetRequest searchSetRequest = new SearchSetRequest();
            searchSetRequest.setBkBizId(bkBiz.getBkBizId());
            searchSetRequest.addIdOrNameCondition(set);
            PageData<BkBizSetModule> searchSetData = client.searchSet(searchSetRequest);
            if (searchSetData.getCount() <= 0) {
                throw new BluekingException("CMDB set not found, please check your parameter");
            }
            if (searchSetData.getCount() > 1) {
                throw new BluekingException("Found CMDB set more than 1, please check your blueking");
            }

            BkBizSetModule set = searchSetData.getInfo().get(0);
            // biz module ids
            List<Integer> moduleIds =
                    Arrays.stream(modules.split(",")).map(Integer::parseInt).collect(Collectors.toList());

            // 搜索host,拼接IP
            ListBizHostsRequest listBizHostsRequest = new ListBizHostsRequest();
            listBizHostsRequest.setBkBizId(bkBiz.getBkBizId());
            listBizHostsRequest.setBkSetIds(Collections.singletonList(set.getBkSetId()));
            listBizHostsRequest.setBkModuleIds(moduleIds);
            listBizHostsRequest.getPage().setLimit(499);
            PageData<BkHost> listBizHostsData = client.listBizHosts(listBizHostsRequest);
            if (listBizHostsData.getCount() <= 0) {
                throw new BluekingException("Not found host, please check your parameter");
            }
            String innerIps = listBizHostsData.getInfo().stream()
                    .map(BkHost::getBkHostInnerip)
                    .distinct()
                    .collect(Collectors.joining(","));
            String outerIps = listBizHostsData.getInfo().stream()
                    .map(BkHost::getBkHostOuterip)
                    .distinct()
                    .collect(Collectors.joining(","));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("innerIps", innerIps);
            result.put("outerIps", outerIps);
            return result;
        }

        private BkBizSetModule findBiz(BluekingCCClient client, String bizIdOrName) {
            SearchBusinessRequest searchBusinessRequest = new SearchBusinessRequest();
            Page page = new Page();
            page.setLimit(200);
            searchBusinessRequest.setPage(page);
            PageData<BkBizSetModule> searchBusinessData = client.searchBusiness(searchBusinessRequest);
            if (searchBusinessData.getCount() <= 0) {
                return null;
            }
            BkBizSetModule bkBiz = null;
            for (BkBizSetModule bsm : searchBusinessData.getInfo()) {
                if (Objects.equals(bsm.getBkBizId().toString(), bizIdOrName)
                        || Objects.equals(bsm.getBkBizName(), bizIdOrName)) {
                    bkBiz = bsm;
                    break;
                }
            }
            return bkBiz;
        }
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

        @NonNull
        @Override
        public String getDisplayName() {
            return "Blueking Get Hosts Step";
        }

        @Override
        public String getFunctionName() {
            return "bkGetHosts";
        }
    }
}
