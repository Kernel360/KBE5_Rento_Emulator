import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.BaseDeviceRequest;
import common.DeviceService;
import common.EmulatorConfig;
import common.HttpClientManager;
import gps.*;
import request.OffEventRequest;
import request.OnEventRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Start {

    private static DeviceService service;
    private static GPSEmulator gpsEmulator;
    private static EmulatorConfig emulatorConfig;
    private static Integer ctrCnt, geoCnt;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static List<GpsData> gpsDataList;
    private static String onEventTime;
    private static final String GPS_FILE_PATH = "/Users/soyun/workspace/Rento/rento-be/emulator/src/main/java/99_course_trip.txt";


    public static void main(String[] args) throws IOException {
        System.out.println("START THE CAR");

        emulatorConfig = new EmulatorConfig.Builder()
                .serverUrl("http://localhost:8080")
                .deviceMdn(1L)
                .firmwareVersion("LTE 1.2")
                .batchSize(60)
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

        if(!prepareGpsAndEmulator(token))
            return;

        if(sendOnEventRequest()) {
            waitForUserToQuit();
        }

        sendOffEventRequest();
    }

    private static boolean prepareGpsAndEmulator(String token) {
        try{
            gpsDataList = GpsFileReader.readGpsData(GPS_FILE_PATH);

            if(gpsDataList.isEmpty()) {
                System.out.println("GPS 데이터가 없습니다");
                return false;
            }

            gpsEmulator = new GPSEmulator(emulatorConfig, token);
            gpsEmulator.start(GPS_FILE_PATH);

            return true;
        } catch (Exception e) {
            System.out.println("GPS 데이터 로드 또는 애뮬레이터 시작 실패: " + e.getMessage());
            return false;
        }
    }

    private static void waitForUserToQuit() {
        try {
            System.out.println("\n=== GPS 에뮬레이터가 실행 중입니다 ===");
            System.out.println("종료하려면 'q' 또는 'quit'를 입력하세요...");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine().trim().toLowerCase();
                if ("q".equals(input) || "quit".equals(input)) {
                    System.out.println("에뮬레이터를 종료합니다...");
                    break;
                }
                System.out.println("종료하려면 'q' 또는 'quit'를 입력하세요...");
            }

            if (gpsEmulator != null) {
                gpsEmulator.stop();
                // 에뮬레이터가 완전히 종료될 때까지 잠시 대기
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            System.err.println("에뮬레이터 실행 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendOffEventRequest() {
        if (!service.hasValidToken()) {
            System.out.println("유효한 토큰이 없습니다. 토큰을 먼저 발급받으세요");
            return;
        }

        if(gpsDataList.isEmpty()){
            System.out.println("GPS 데이터가 없습니다");
            return;
        }

        try{
            String offTime  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            GpsData firstData = gpsDataList.get(gpsDataList.size()-1);

            OffEventRequest request = new OffEventRequest(
                    onEventTime,
                    offTime,
                    'A',
                    firstData.getLatitude(),
                    firstData.getLongitude(),
                    "0",
                    "0",
                    gpsEmulator.getTotalDistanceMeters()
            );

            BaseDeviceRequest.setDefaults(request, emulatorConfig.getDeviceMdn());

            HttpClientManager.ApiResponse<String> response = service.sendOffEventRequest(request);

            if (response.isSuccess()) {
                System.out.println("OnEvent 전송 성공");
            } else {
                System.err.println("OnEvent 전송 실패: " + response.getMessage());
                System.err.println("응답 코드: " + response.getCode());
            }

        } catch (Exception e) {
            System.err.println("OnEvent 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static boolean sendOnEventRequest() {
        if (!service.hasValidToken()) {
            System.out.println("유효한 토큰이 없습니다. 토큰을 먼저 발급받으세요");
            return false;
        }

        if(gpsDataList.isEmpty()){
            System.out.println("GPS 데이터가 없습니다");
            return false;
        }

        try{
            onEventTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            GpsData firstData = gpsDataList.get(0);

            OnEventRequest request = new OnEventRequest(
                    onEventTime,
                    null,
                    'A',
                    firstData.getLatitude(),
                    firstData.getLongitude(),
                    "0",
                    "0",
                    //todo: 시작할때 누적 주행거리는 시동 off 일때의 값으로
                    0L
            );

            BaseDeviceRequest.setDefaults(request, emulatorConfig.getDeviceMdn());

            HttpClientManager.ApiResponse<String> response = service.sendOnEventRequest(request);

            if (response.isSuccess()) {
                System.out.println("OnEvent 전송 성공");
            } else {
                System.err.println("OnEvent 전송 실패: " + response.getMessage());
                System.err.println("응답 코드: " + response.getCode());
            }

        } catch (Exception e) {
            System.err.println("OnEvent 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private static void startEmulator(String token) {
        if (gpsEmulator != null && gpsEmulator.isRunning()) {
            System.out.println("에뮬레이터가 이미 실행 중입니다.");
            return;
        }

        String filePath = "/Users/soyun/workspace/Rento/rento-be/emulator/src/main/java/99_course_trip.txt";

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
}