package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientManager {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final int connectTimeout;
    private final int readTimeout;

    public HttpClientManager() {
        this(3000, 5000);
    } //3초동안 연결 안되면 실패, 연결은 되었는데 5초 안에 응답 없으면 실패

    public HttpClientManager(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public <T> ApiResponse<String> sendPostRequest(String url, T requestBody){
        return sendPostRequest(url, requestBody, null);
    }

    public <T> ApiResponse<String> sendPostRequest(String url, T requestBody, String token){
        try{
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest.Builder request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofMillis(readTimeout))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if(token != null && !token.trim().isEmpty()){
                request.header( "X-Device-Token",token );
            }

            HttpRequest requestObj = request.build();
            HttpResponse<String> response = httpClient.send(requestObj, HttpResponse.BodyHandlers.ofString());

            return new ApiResponse<>(
                    response.statusCode() == 200,
                    response.statusCode(),
                    response.body(),
                    null
            );
        } catch (Exception e){
            return new ApiResponse<>(false, -1, null, e.getMessage());
        }
    }

    public static class ApiResponse<T> {
        private final boolean success;
        private final int code;
        private final T data;
        private final String message;

        public ApiResponse(boolean success, int code, T data, String message) {
            this.success = success;
            this.code = code;
            this.data = data;
            this.message = message;
        }

        public boolean isSuccess() {return success;}
        public int getCode() {return code;}
        public T getData() {return data;}
        public String getMessage() {return message;}
    }
}