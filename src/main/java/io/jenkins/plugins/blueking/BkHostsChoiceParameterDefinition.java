package io.jenkins.plugins.blueking;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.blueking.client.BluekingCCClient;
import io.jenkins.plugins.blueking.model.dto.BkBizSetModule;
import io.jenkins.plugins.blueking.model.dto.BkHost;
import io.jenkins.plugins.blueking.model.dto.BkInstObj;
import io.jenkins.plugins.blueking.model.dto.FileHost;
import io.jenkins.plugins.blueking.model.dto.PageData;
import io.jenkins.plugins.blueking.model.dto.PropertyFilter;
import io.jenkins.plugins.blueking.model.req.ListBizHostsRequest;
import io.jenkins.plugins.blueking.model.req.SearchBizInstTopoRequest;
import io.jenkins.plugins.blueking.utils.BluekingException;
import io.jenkins.plugins.blueking.utils.Constants;
import io.jenkins.plugins.blueking.utils.FileHostFilter;
import io.jenkins.plugins.blueking.utils.Tuple;
import io.jenkins.plugins.blueking.utils.Utils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.plaincredentials.FileCredentials;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * @author Bruce.Wu
 * @date 2024-12-09
 */
@Log
@Getter
@ToString
public class BkHostsChoiceParameterDefinition extends ParameterDefinition {

    private static final long serialVersionUID = 160662149243038267L;
    /**
     * bk baseurl
     */
    private String baseUrl;
    /**
     * credentials ID
     */
    private String credentialsId;
    /**
     * bk username
     */
    private String username;
    /**
     * 业务名称或集群ID
     */
    private String biz;
    /**
     * excel表格
     */
    private String extraFileId;
    /**
     * 外网IP列表，逗号分隔
     */
    private String outerEnv = "OUTER_IPS";
    /**
     * 内网IP列表，逗号分隔
     */
    private String innerEnv = "INNER_IPS";

    @DataBoundConstructor
    public BkHostsChoiceParameterDefinition(String name) {
        super(Util.fixNull(Util.fixEmpty(name), "DEPLOY_IPS"));
    }

    @DataBoundSetter
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @DataBoundSetter
    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    @DataBoundSetter
    public void setUsername(String username) {
        this.username = username;
    }

    @DataBoundSetter
    public void setBiz(String biz) {
        this.biz = biz;
    }

    @DataBoundSetter
    public void setExtraFileId(String extraFileId) {
        this.extraFileId = extraFileId;
    }

    @DataBoundSetter
    public void setOuterEnv(String outerEnv) {
        this.outerEnv = outerEnv;
    }

    @DataBoundSetter
    public void setInnerEnv(String innerEnv) {
        this.innerEnv = innerEnv;
    }

    public List<Map<String, String>> getTabs() {
        List<Map<String, String>> tabs = new ArrayList<>();

        if (StringUtils.isNotBlank(baseUrl)
                && StringUtils.isNotBlank(username)
                && StringUtils.isNotBlank(biz)
                && StringUtils.isNotBlank(credentialsId)) {
            Map<String, String> tabIp = new LinkedHashMap<>();
            tabIp.put("name", "BK IP Choice");
            tabIp.put("id", "bkIpChoice");
            tabs.add(tabIp);
        }

        if (StringUtils.isNotBlank(extraFileId)) {
            Map<String, String> tabFile = new LinkedHashMap<>();
            tabFile.put("name", "File Choice");
            tabFile.put("id", "fileChoice");
            tabs.add(tabFile);
        }
        return tabs;
    }

    /**
     * 获取拓扑
     *
     * @return
     */
    public List<BkInstObj> getBkInstTopo() {
        StandardUsernamePasswordCredentials standardCredentials =
                Utils.findCredential(credentialsId, StandardUsernamePasswordCredentials.class);
        BluekingCCClient client = new BluekingCCClient(baseUrl, Objects.requireNonNull(standardCredentials), username);
        BkBizSetModule bkBiz = client.findBiz(biz);
        SearchBizInstTopoRequest request = new SearchBizInstTopoRequest();
        request.setLevel(-1);
        request.setBkBizId(bkBiz.getBkBizId());
        List<BkInstObj> topo = client.searchBizInstTopo(request);
        return topo;
    }

    public String getParams() {
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(baseUrl)
                && StringUtils.isNotBlank(username)
                && StringUtils.isNotBlank(biz)
                && StringUtils.isNotBlank(credentialsId)) {
            StandardUsernamePasswordCredentials standardCredentials =
                    Utils.findCredential(credentialsId, StandardUsernamePasswordCredentials.class);
            BluekingCCClient client =
                    new BluekingCCClient(baseUrl, Objects.requireNonNull(standardCredentials), username);
            BkBizSetModule bkBiz = client.findBiz(biz);
            obj.put("baseUrl", baseUrl);
            obj.put("credentialsId", credentialsId);
            obj.put("username", username);
            obj.put("bizId", bkBiz.getBkBizId());
        }
        obj.put("extraFileId", extraFileId);
        return obj.toString();
    }

    private Tuple<Set<String>, Set<String>> getIps(String json) {
        Set<String> innerIps = new LinkedHashSet<>();
        Set<String> outerIps = new LinkedHashSet<>();
        JSONObject ob = JSONObject.fromObject(json);
        if (ob.has("data")) {
            String hostsJson = ob.getString("data");
            JSONArray arr = JSONArray.fromObject(hostsJson);
            for (int i = 0; i < arr.size(); i++) {
                JSONArray host = arr.getJSONArray(i);
                innerIps.add(host.getString(0));
                outerIps.add(host.getString(1));
            }
        }
        return new Tuple<>(innerIps, outerIps);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        String name = jo.getString("name");
        JSONObject value = jo.getJSONObject("value");
        String selectedBkHostsJson = value.getString("selectedBkHosts");
        String selectedFileHostsJson = value.getString("selectedFileHosts");

        Tuple<Set<String>, Set<String>> bkTuple = getIps(selectedBkHostsJson);
        Tuple<Set<String>, Set<String>> fileTuple = getIps(selectedFileHostsJson);

        Set<String> allInnerIps = new LinkedHashSet<>(bkTuple.getLeft());
        allInnerIps.addAll(fileTuple.getLeft());

        Set<String> allOuterIps = new LinkedHashSet<>(bkTuple.getRight());
        allOuterIps.addAll(fileTuple.getRight());

        return new BkHostsChoiceParameterValue(name, outerEnv, innerEnv, allOuterIps, allInnerIps);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        try {
            return createValue(req, req.getSubmittedForm());
        } catch (Exception e) {
            throw new BluekingException(e);
        }
    }

    @Log
    @Extension
    @Symbol("bkHostsChoice")
    public static class DescriptorImpl extends ParameterDescriptor {

        public DescriptorImpl() {
            super(BkHostsChoiceParameterDefinition.class);
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Blueking Hosts Choice Parameter";
        }

        public ListBoxModel doFillCredentialsIdItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Select a credential", "");
            for (StandardUsernameCredentials c : CredentialsProvider.lookupCredentialsInItemGroup(
                    StandardUsernameCredentials.class, Jenkins.get(), null, Collections.emptyList())) {
                items.add(c.getId(), c.getId());
            }
            return items;
        }

        public ListBoxModel doFillExtraFileIdItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Select a file", "");
            for (FileCredentials c : CredentialsProvider.lookupCredentialsInItemGroup(
                    FileCredentials.class, Jenkins.get(), null, Collections.emptyList())) {
                items.add(c.getId(), c.getId());
            }
            return items;
        }

        @JavaScriptMethod(name = "searchBkHosts")
        public PageData<BkHost> doSearchBkHosts(
                @QueryParameter("selectedHosts") String selectedHosts,
                @QueryParameter("bkHostsChoiceParams") String bkHostsChoiceParams,
                @QueryParameter("bkObjId") String bkObjId,
                @QueryParameter("bkInstId") String bkInstId,
                @QueryParameter("keyword") String keyword,
                @QueryParameter("page") String page,
                @QueryParameter("limit") String limit)
                throws Exception {

            JSONObject params = JSONObject.fromObject(bkHostsChoiceParams);
            String baseUrl = params.getString("baseUrl");
            String credentialsId = params.getString("credentialsId");
            String username = params.getString("username");
            String bizId = params.getString("bizId");
            StandardUsernamePasswordCredentials standardCredentials =
                    Utils.findCredential(credentialsId, StandardUsernamePasswordCredentials.class);
            BluekingCCClient client =
                    new BluekingCCClient(baseUrl, Objects.requireNonNull(standardCredentials), username);
            ListBizHostsRequest listBizHostsRequest = new ListBizHostsRequest();
            listBizHostsRequest.getFields().add("bk_cloud_id");
            listBizHostsRequest.getFields().add("bk_cloud_name");
            listBizHostsRequest.getFields().add("host_name");
            listBizHostsRequest.getFields().add("bk_state");
            listBizHostsRequest.getFields().add("bk_asset_id");
            listBizHostsRequest.setBkBizId(Integer.parseInt(bizId));

            if (Utils.isNotEmpty(keyword)) {
                PropertyFilter fieldInnerIp = PropertyFilter.createField("bk_host_innerip", "equal", keyword);
                PropertyFilter fieldOuterIp = PropertyFilter.createField("bk_host_outerip", "equal", keyword);
                List<PropertyFilter> rules = new ArrayList<>(2);
                rules.add(fieldInnerIp);
                rules.add(fieldOuterIp);
                PropertyFilter hostPropertyFilter = PropertyFilter.createCondition(Constants.OR, rules);
                listBizHostsRequest.setHostPropertyFilter(hostPropertyFilter);
            }
            int pageNum = NumberUtil.parseInt(page);
            int limitNum = NumberUtil.parseInt(limit);

            listBizHostsRequest.getPage().setStart(pageNum * limitNum);
            listBizHostsRequest.getPage().setLimit(limitNum);

            // biz,BusinessId,set,module
            if ("set".equals(bkObjId)) {
                listBizHostsRequest.setBkSetIds(Collections.singletonList(Integer.parseInt(bkInstId)));
            } else if ("module".equals(bkObjId)) {
                listBizHostsRequest.setBkModuleIds(Collections.singletonList(Integer.parseInt(bkInstId)));
            }

            PageData<BkHost> pageData = client.listBizHosts(listBizHostsRequest);
            if (CollectionUtils.isNotEmpty(pageData.getInfo())) {
                Set<String> selectedHostIds = getSelectedHostIds(selectedHosts);
                pageData.getInfo().forEach(e -> e.setSelected(selectedHostIds.contains(e.getBkHostId())));
            }

            return pageData;
        }

        @JavaScriptMethod(name = "searchFileHosts")
        public PageData<FileHost> doSearchFileHosts(
                @QueryParameter("bkHostsChoiceParams") String bkHostsChoiceParams,
                @QueryParameter("selectedHosts") String selectedHosts,
                @QueryParameter("keyword") String keyword,
                @QueryParameter("page") String page,
                @QueryParameter("limit") String limit)
                throws IOException, BluekingException {
            JSONObject params = JSONObject.fromObject(bkHostsChoiceParams);
            String extraFileId = params.getString("extraFileId");
            FileCredentials fileCredentials = Utils.findCredential(extraFileId, FileCredentials.class);
            CsvReader reader = CsvUtil.getReader();
            CsvData csvData = reader.read(new InputStreamReader(
                    Objects.requireNonNull(fileCredentials).getContent(), StandardCharsets.UTF_8));
            PageData<FileHost> pageData = new PageData<>();
            if (csvData.getRowCount() < 2) {
                pageData.setCount(0);
                pageData.setInfo(Collections.emptyList());
                return pageData;
            } else {
                int pageNum = NumberUtil.parseInt(page);
                int limitNum = NumberUtil.parseInt(limit);
                FileHostFilter fileHostFilter = new FileHostFilter(keyword);
                List<FileHost> fileHosts = new ArrayList<>(csvData.getRowCount() - 1);
                for (int i = 1; i < csvData.getRowCount(); i++) {
                    CsvRow csvRow = csvData.getRow(i);
                    String innerIp = csvRow.get(0);
                    String outerIp = csvRow.get(1);
                    String hostName = csvRow.get(2);
                    String module = csvRow.get(3);
                    if (Utils.isNotEmpty(innerIp) && !ReUtil.isMatch(RegexPool.IPV4, innerIp)) {
                        throw new BluekingException(String.format(
                                "Found invalid inner ip. fileId: %s, ip: %s, row: %d", extraFileId, innerIp, i));
                    }
                    if (Utils.isNotEmpty(outerIp) && !ReUtil.isMatch(RegexPool.IPV4, outerIp)) {
                        throw new BluekingException(String.format(
                                "Found invalid outer ip. fileId: %s, ip: %s, row: %d", extraFileId, outerIp, i));
                    }
                    FileHost fileHost = new FileHost();
                    fileHost.setHostId(String.valueOf(i));
                    fileHost.setHostInnerip(innerIp);
                    fileHost.setHostOuterip(outerIp);
                    fileHost.setHostName(hostName);
                    fileHost.setModule(module);
                    if (fileHostFilter.test(fileHost)) {
                        fileHosts.add(fileHost);
                    }
                }
                pageData.setCount(fileHosts.size());
                if (pageNum * limitNum >= fileHosts.size()) {
                    pageData.setInfo(Collections.emptyList());
                } else {
                    int endIdx = Math.min(fileHosts.size(), (pageNum + 1) * limitNum);
                    pageData.setInfo(fileHosts.subList(pageNum * limitNum, endIdx));
                }
                pageData.setInfo(fileHosts);
            }
            Set<String> selectedHostIds = getSelectedHostIds(selectedHosts);
            pageData.getInfo().forEach(e -> e.setSelected(selectedHostIds.contains(e.getHostId())));
            return pageData;
        }

        private Set<String> getSelectedHostIds(String selectedHosts) {
            Set<String> selectedHostIds = new HashSet<>();
            JSONObject selectedHostsObj = JSONObject.fromObject(selectedHosts);
            if (selectedHostsObj.has("data")) {
                String hostsJson = selectedHostsObj.getString("data");
                JSONArray arr = JSONArray.fromObject(hostsJson);
                for (int i = 0; i < arr.size(); i++) {
                    selectedHostIds.add(arr.getJSONArray(i).getString(3));
                }
            }
            return selectedHostIds;
        }
    }
}
