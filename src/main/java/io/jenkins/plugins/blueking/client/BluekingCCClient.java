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
import io.jenkins.plugins.blueking.utils.HttpClientUtil;
import io.jenkins.plugins.blueking.utils.Logger;

public class BluekingCCClient {

    private final Logger logger;
    private final String baseUrl;
    private final String bkAppCode;
    private final String bkAppSecret;
    private final String bkUsername;

    public BluekingCCClient(Logger logger, String baseUrl, String bkAppCode, String bkAppSecret, String bkUsername) {
        this.logger = logger;
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
        logger.log("Start request. url=" + url);
        String searchBizRespJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkBizSetModule>> response = JSON.parseObject(searchBizRespJson, new TypeReference<>() {});
        return checkResponse(response);
    }

    public PageData<BkBizSetModule> searchSet(SearchSetRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_set";
        logger.log("Start request. url=" + url);
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkBizSetModule>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(response);
    }

    public PageData<BkBizSetModule> searchModule(SearchModuleRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/search_module";
        logger.log("Start request. url=" + url);
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkBizSetModule>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(response);
    }

    public PageData<BkHost> listBizHosts(ListBizHostsRequest request) {
        handleCredentials(request);
        String url = baseUrl + "/api/c/compapi/v2/cc/list_biz_hosts";
        logger.log("Start request. url=" + url);
        String respJson = HttpClientUtil.post(url, request.toJsonString());
        BaseResponse<PageData<BkHost>> response = JSON.parseObject(respJson, new TypeReference<>() {});
        return checkResponse(response);
    }

    private <T> T checkResponse(BaseResponse<T> response) {
        if (!response.isSuccess()) {
            logger.log("Request unsuccessful. code=" + response.getCode() + ", message=" + response.getMessage());
            throw new RuntimeException("Invoke api error");
        }
        return response.getData();
    }
}
