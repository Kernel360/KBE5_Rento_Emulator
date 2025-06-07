package common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import request.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DeviceService {
    private final HttpClientManager httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final Long deviceMdn;
    private final String firmwareVersion;
    private String currentToken;

    public DeviceService(String baseUrl, Long deviceMdn, String firmwareVersion) {
        this.httpClient = new HttpClientManager();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
        this.deviceMdn = deviceMdn;
        this.firmwareVersion = firmwareVersion;
        this.currentToken = null;
    }

    public HttpClientManager.ApiResponse<String> sendTokenRequest() {
        TokenRequest request = new TokenRequest(deviceMdn, firmwareVersion);

        HttpClientManager.ApiResponse<String> response = httpClient.sendPostRequest(baseUrl + "/api/devices/token", request, null);

        if(response.isSuccess()) {
            try{
                JsonNode rootNode = objectMapper.readTree(response.getData());

                if(rootNode.has("token")) {
                    this.currentToken = rootNode.get("token").asText();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return response;
    }

    public HttpClientManager.ApiResponse<String> sendGetSetInfoRequest() {
        GetSetInfoRequest request = new GetSetInfoRequest(deviceMdn, firmwareVersion);
        return httpClient.sendPostRequest(baseUrl + "/api/devices/getSetInfo", request, currentToken);
    }

    public HttpClientManager.ApiResponse<String> sendCheckInfoRequest(Integer ctrCnt, Integer getCnt) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CheckInfoRequest request = new CheckInfoRequest(deviceMdn, currentTime, ctrCnt, getCnt);

        return httpClient.sendPostRequest(baseUrl + "/api/devices/checkInfo", request, currentToken);
    }

    public HttpClientManager.ApiResponse<String> sendGpsData(CycleInfoSendRequest request) {
        return httpClient.sendPostRequest(baseUrl + "/api/events/cycle-info", request, currentToken);
    }

    public HttpClientManager.ApiResponse<String> sendOnEventRequest(OnEventRequest request) {
        return httpClient.sendPostRequest(baseUrl + "/api/events/on-off/on", request, currentToken);
    }

    public HttpClientManager.ApiResponse<String> sendOffEventRequest(OffEventRequest request) {
        return httpClient.sendPostRequest(baseUrl + "/api/events/on-off/off", request, currentToken);

    }

    public void setToken(String token) {
        this.currentToken = token;
    }

    public boolean hasValidToken() {
        return currentToken != null && !currentToken.trim().isEmpty();
    }

    public String getCurrentToken() {
        return currentToken;
    }


}