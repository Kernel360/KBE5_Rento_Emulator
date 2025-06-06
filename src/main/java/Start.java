import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.DeviceService;
import common.EmulatorConfig;
import common.HttpClientManager;
import gps.GPSEmulator;

import java.io.IOException;

public class Start {

    private static DeviceService service;
    private static GPSEmulator gpsEmulator;
    private static EmulatorConfig emulatorConfig;
    private static Integer ctrCnt, geoCnt;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        System.out.println("START THE CAR");

        emulatorConfig = new EmulatorConfig.Builder()
                .serverUrl("http://localhost:8080")
                .deviceMdn(1L)
                .firmwareVersion("LTE 1.2")
                .batchSize(85878)
                .dataGenerationInterval(1)
                .sendInterval(10000)
                .build();

        service = new DeviceService(
                emulatorConfig.getServerUrl(),
                emulatorConfig.getDeviceMdn(),
                emulatorConfig.getFirmwareVersion()
        );

        String token = extractValueFromJson(service.sendTokenRequest(), "token");
        service.setToken(token);

        sendGetSetInfoRequest();
        sendCheckInfoRequest();
//        startEmulator(token);
        startEmulatorBatchSend(token); // <--- 이렇게만 바꿔주면 됨!

    }

    private static void startEmulator(String token) {
        if (gpsEmulator != null && gpsEmulator.isRunning()) {
            System.out.println("에뮬레이터가 이미 실행 중입니다.");
            return;
        }

        String filePath = "/Users/sean/Downloads/99_course_trip.txt";

        try {
            gpsEmulator = new GPSEmulator(emulatorConfig, token);
            gpsEmulator.start(filePath);

        } catch (Exception e) {
            System.err.println("에뮬레이터 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendGetSetInfoRequest() {
        if (!service.hasValidToken()) {
            System.out.println("유효한 토큰이 없습니다. 토큰을 먼저 발급받으세요");
            return;
        }

        try {
            HttpClientManager.ApiResponse<String> response = service.sendGetSetInfoRequest();
            JsonNode rootNode = objectMapper.readTree(response.getData());

            ctrCnt = rootNode.get("ctrCnt").asInt();
            geoCnt = rootNode.get("geoCnt").asInt();

            System.out.println("설정 정보 성공 여부: " + response.isSuccess());
        } catch (Exception e) {
            System.err.println("GetSetInfo 요청 중 예외 발생: " + e.getMessage());
        }
    }

    private static void sendCheckInfoRequest() {
        if (!service.hasValidToken()) {
            System.out.println("유효한 토큰이 없습니다. 토큰을 먼저 발급받으세요");
            return;
        }

        try {
            HttpClientManager.ApiResponse<String> response = service.sendCheckInfoRequest(ctrCnt, geoCnt);
            System.out.println("설정 정보 확인 성공 여부: " + response.isSuccess());
        } catch (Exception e) {
            System.err.println("CheckInfo 요청 중 예외 발생: " + e.getMessage());
        }
    }

    private static String extractValueFromJson(HttpClientManager.ApiResponse<String> response, String fieldName) throws IOException {
        JsonNode rootNode = objectMapper.readTree(response.getData());
        return rootNode.get(fieldName).asText();
    }

    private static void startEmulatorBatchSend(String token) {
        if (gpsEmulator != null && gpsEmulator.isRunning()) {
            System.out.println("에뮬레이터가 이미 실행 중입니다.");
            return;
        }

        String filePath = "/Users/sean/Downloads/99_course_trip.txt";

        try {
            gpsEmulator = new GPSEmulator(emulatorConfig, token);
            gpsEmulator.startBatchSendMode(filePath);

        } catch (Exception e) {
            System.err.println("에뮬레이터 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
