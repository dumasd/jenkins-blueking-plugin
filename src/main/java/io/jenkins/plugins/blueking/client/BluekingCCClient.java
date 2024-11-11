package io.jenkins.plugins.blueking.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import io.jenkins.plugins.blueking.model.dto.BkBizSetModule;
import io.jenkins.plugins.blueking.model.dto.BkHost;
import io.jenkins.plugins.blueking.model.dto.PageData;
import io.jenkins.plugins.blueking.model.req.BaseRequest;
import io.jenkins.plugins.blueking.model.req.ListBizHostsRequest;
import io.jenkins.plugins.blueking.model.req.SearchBusinessRequest;
import io.jenkins.plugins.blueking.model.req.SearchModuleRequest;
import io.jenkins.plugins.blueking.model.req.SearchSetRequest;
import io.jenkins.plugins.blueking.model.resp.BaseResponse;
import io.jenkins.plugins.blueking.utils.BluekingException;
import io.jenkins.plugins.dumasd.common.HttpClientUtil;

public class BluekingCCClient {

    private final String baseUrl;
    private final String bkAppCode;
    private final String bkAppSecret;
    private final String bkUsername;

    public BluekingCCClient(String baseUrl, String bkAppCode, String bkAppSecret, String bkUsername) {
        this.baseUrl = baseUrl;
        this.bkAppCode = bkAppCode;
        this.bkAppSecret = bkAppSecret;
        this.bkUsername = bkUsername;
    }

    private void handleCredentials(BaseRequest request) {
        request.setBkAppCode(bkAppCode);
        request.setBkAppSecret(bkAppSecret);
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

    private <T> T checkResponse(String url, BaseResponse<T> response) {
        if (!response.isSuccess()) {
            String s = String.format(
                    "Invoke %s error. code: %s, message:%s", url, response.getStrCode(), response.getMessage());
            throw new BluekingException(s);
        }
        return response.getData();
    }
}
