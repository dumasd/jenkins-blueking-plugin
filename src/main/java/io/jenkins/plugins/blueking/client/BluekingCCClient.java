package io.jenkins.plugins.blueking.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.util.Secret;
import io.jenkins.plugins.blueking.model.dto.BkBizSetModule;
import io.jenkins.plugins.blueking.model.dto.BkCloud;
import io.jenkins.plugins.blueking.model.dto.BkHost;
import io.jenkins.plugins.blueking.model.dto.BkInstObj;
import io.jenkins.plugins.blueking.model.dto.Page;
import io.jenkins.plugins.blueking.model.dto.PageData;
import io.jenkins.plugins.blueking.model.req.BaseRequest;
import io.jenkins.plugins.blueking.model.req.ListBizHostsRequest;
import io.jenkins.plugins.blueking.model.req.SearchBizInstTopoRequest;
import io.jenkins.plugins.blueking.model.req.SearchBusinessRequest;
import io.jenkins.plugins.blueking.model.req.SearchCloudAreaRequest;
import io.jenkins.plugins.blueking.model.req.SearchModuleRequest;
import io.jenkins.plugins.blueking.model.req.SearchSetRequest;
import io.jenkins.plugins.blueking.model.resp.BaseResponse;
import io.jenkins.plugins.blueking.utils.BluekingException;
import io.jenkins.plugins.dumasd.common.HttpClientUtil;
import java.util.List;
import java.util.Objects;

/**
 * BlueKing cmdb api client
 *
 * @author Bruce.Wu
 * @date 2024-12-14
 */
public class BluekingCCClient {

    private final String baseUrl;
    private final String bkAppCode;
    private String bkAppSecret;
    private Secret secret;
    private final String bkUsername;

    public BluekingCCClient(String baseUrl, String bkAppCode, String bkAppSecret, String bkUsername) {
        this.baseUrl = baseUrl;
        this.bkAppCode = bkAppCode;
        this.bkAppSecret = bkAppSecret;
        this.bkUsername = bkUsername;
    }

    public BluekingCCClient(String baseUrl, StandardUsernamePasswordCredentials credentials, String bkUsername) {
        this.baseUrl = baseUrl;
        this.bkAppCode = credentials.getUsername();
        this.secret = credentials.getPassword();
        this.bkUsername = bkUsername;
    }

    private void handleCredentials(BaseRequest request) {
        request.setBkAppCode(bkAppCode);
        if (Objects.nonNull(secret)) {
            request.setBkAppSecret(Secret.toString(secret));
        } else {
            request.setBkAppSecret(bkAppSecret);
        }
        request.setBkUsername(bkUsername);
    }

    public PageData<BkBizSetModule> searchBusiness(SearchBusinessRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_business";
        String searchBizRespJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkBizSetModule>> response = JSON.parseObject(searchBizRespJson, new TypeReference<>() {});
        return checkResponse(url, response);
    }

    public PageData<BkBizSetModule> searchSet(SearchSetRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_set";
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkBizSetModule>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(url, response);
    }

    public PageData<BkBizSetModule> searchModule(SearchModuleRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_module";
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkBizSetModule>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(url, response);
    }

    public PageData<BkHost> listBizHosts(ListBizHostsRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/list_biz_hosts";
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkHost>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(url, response);
    }

    public List<BkInstObj> searchBizInstTopo(SearchBizInstTopoRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_biz_inst_topo";
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<List<BkInstObj>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(url, response);
    }

    public BkBizSetModule findBiz(String bizIdOrName) {
        SearchBusinessRequest searchBusinessRequest = new SearchBusinessRequest();
        Page page = new Page();
        page.setLimit(200);
        searchBusinessRequest.setPage(page);
        PageData<BkBizSetModule> searchBusinessData = searchBusiness(searchBusinessRequest);
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

    public PageData<BkCloud> searchCloudArea(SearchCloudAreaRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_cloud_area";
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkCloud>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(url, response);
    }

    private <T> T checkResponse(String url, BaseResponse<T> response) {
        if (!response.isSuccess()) {
            String s = String.format(
                    "Invoke %s error. code: %s, message:%s", url, response.getStrCode(), response.getMessage());
            throw new BluekingException(s);
        }
        return response.getData();
    }
}
