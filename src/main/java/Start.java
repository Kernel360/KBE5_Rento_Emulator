import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import common.*;
import gps.*;
import request.GeofenceEventRequest;
import request.OffEventRequest;
import request.OnEventRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Start {

    private static DeviceService service;
    private static GPSEmulator gpsEmulator;
    private static EmulatorConfig emulatorConfig;
    private static Integer ctrCnt, geoCnt;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static List<GpsData> gpsDataList;
    private static String onEventTime;
    private static String oTime;
    private static final String GPS_FILE_PATH = "/gps/99_course_trip.txt";
    public static List<GeofenceResponseDto> geoList = new ArrayList<>();

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

        String token = extractValueFromJson(service.sendTokenRequest());
        service.setToken(token);

        EventSender.getInstance().init(
                service,
                emulatorConfig,
                () -> gpsDataList,
                () -> gpsEmulator,
                () -> onEventTime,
                (val) -> onEventTime = val,
                (val) -> oTime = val
        );

        CompletableFuture<Void> future = sendGetSetInfoRequestAsync() // 설정 정보를 받고 prepareGpsAndEmulator 실행
                .thenRun(() -> {
                    if (!prepareGpsAndEmulator(token))
                        return;

                    if (EventSender.getInstance().sendOnEventRequest()) {
                        waitForUserToQuit();
                    }

                    EventSender.getInstance().sendOffEventRequest();
                });

        // 🔽 main 스레드가 종료되지 않도록 대기
        future.join();
    }

    private static boolean prepareGpsAndEmulator(String token) {
        try{
            gpsDataList = GpsFileReader.readGpsData(GPS_FILE_PATH);

            if(gpsDataList.isEmpty()) {
                System.out.println("GPS 데이터가 없습니다");
                return false;
            }

            gpsEmulator = new GPSEmulator(emulatorConfig, token);
            gpsEmulator.start(GPS_FILE_PATH, geoList);

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


    private static void startEmulator(String token) {
        if (gpsEmulator != null && gpsEmulator.isRunning()) {
            System.out.println("에뮬레이터가 이미 실행 중입니다.");
            return;
        }

        String filePath = "/Users/soyun/workspace/Rento/rento-be/emulator/src/main/java/99_course_trip.txt";

        try {
            gpsEmulator = new GPSEmulator(emulatorConfig, token);
            gpsEmulator.start(filePath, geoList);

        } catch (Exception e) {
            System.err.println("에뮬레이터 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static CompletableFuture<Void> sendGetSetInfoRequestAsync() {
        return CompletableFuture.runAsync(() -> {
            if (!service.hasValidToken()) {
                System.out.println("유효한 토큰이 없습니다. 토큰을 먼저 발급받으세요");
                return;
            }

            try {
                HttpClientManager.ApiResponse<String> response = service.sendGetSetInfoRequest();
                JsonNode rootNode = objectMapper.readTree(response.getData());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                GeofenceListWrapperDto result = objectMapper.readValue(response.getData(), GeofenceListWrapperDto.class);

                geoList = result.getGeoList();

                ctrCnt = rootNode.get("ctrCnt").asInt();
                geoCnt = rootNode.get("geoCnt").asInt();

                System.out.println("설정 정보 성공 여부: " + response.isSuccess());
            } catch (Exception e) {
                System.err.println("GetSetInfo 요청 중 예외 발생: " + e.getMessage());
            }
        });
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

    private static String extractValueFromJson(HttpClientManager.ApiResponse<String> response) throws IOException {
        JsonNode rootNode = objectMapper.readTree(response.getData());
        return rootNode.get("token").asText();
    }
}