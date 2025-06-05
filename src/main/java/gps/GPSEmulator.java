package gps;

import common.DeviceService;
import common.EmulatorConfig;
import request.CycleInfoSendRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GPSEmulator {
    // 설정 상수
    private static final int DEFAULT_BATCH_SIZE = 60;
    private static final int DEFAULT_DATA_GENERATION_INTERVAL = 1; // 초
    private static final String DEFAULT_FIRMWARE_VERSION = "LTE 1.2";

    // 서비스 및 프로세서
    private final DeviceService gpsService;   // 8080으로 GPS 데이터 전송
    private final GpsDataProcessor dataProcessor;
    private final EmulatorConfig config;
    private final String token; // 토큰을 멤버 변수로 저장

    // 데이터 관리
    private final List<GpsData> dataBuffer = new ArrayList<>();
    private List<GpsData> allGpsData = new ArrayList<>();
    private int currentIndex = 0;

    // 스케줄러
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;

    // 토큰을 받는 생성자로 변경
    public GPSEmulator(EmulatorConfig config, String token) {
        this.config = config;
        this.token = token;

        // 8080으로 GPS 데이터 전송하는 서비스
        this.gpsService = new DeviceService(
                config.getServerUrl(), // GPS 데이터 서버 (8080)
                config.getDeviceMdn(),
                config.getFirmwareVersion()
        );

        // 토큰 설정
        this.gpsService.setToken(token);
        this.dataProcessor = new GpsDataProcessor();
    }

    // 기존 생성자도 유지 (호환성을 위해)
    public GPSEmulator(EmulatorConfig config) {
        this(config, null);
    }

    public void start(String filePath) throws Exception {
        if (isRunning) {
            shutdown();
        }

        loadGPSData(filePath);

        this.scheduler = Executors.newScheduledThreadPool(2);

        // 초기화
        dataProcessor.reset();

        startEmulation();
        isRunning = true;
    }

    private void loadGPSData(String fileName) throws Exception {
        allGpsData = GpsFileReader.readGpsData(fileName);
        if (allGpsData.isEmpty()) {
            throw new RuntimeException("로드된 GPS 데이터가 없습니다.");
        }
    }

    private void startEmulation() {
        scheduler.scheduleAtFixedRate(
                this::generateGPSData,
                0,
                config.getDataGenerationInterval(),
                TimeUnit.SECONDS
        );

        scheduler.scheduleAtFixedRate(
                this::sendDataToServer,
                config.getSendInterval() / 1000,
                config.getSendInterval() / 1000,
                TimeUnit.SECONDS
        );
    }

    private void generateGPSData() {
        GpsData currentData = allGpsData.get(currentIndex % allGpsData.size());
        currentIndex++;

        String currentTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.00"));

        GpsData newData = new GpsData(
                currentData.getCategory(),
                currentTime.split(" ")[0],
                currentTime,
                currentData.getVehicleType(),
                currentData.getLatitude(),
                currentData.getLongitude()
        );

        synchronized (dataBuffer) {
            dataBuffer.add(newData);
            System.out.printf("GPS 데이터 생성: %d/%d %d %s%n", dataBuffer.size(), config.getBatchSize(), dataBuffer.size(), newData);
        }
    }

    private void sendDataToServer() {
        List<GpsData> dataToSend;

        synchronized (dataBuffer) {
            if (dataBuffer.isEmpty()) {
                return;
            }

            int sendCount = Math.min(dataBuffer.size(), config.getBatchSize());
            dataToSend = new ArrayList<>(dataBuffer.subList(0, sendCount));
            dataBuffer.subList(0, sendCount).clear();
        }

        try {
            CycleInfoSendRequest request = createCycleInfoRequest(dataToSend);

            var response = gpsService.sendGpsData(request);

            if (response.isSuccess()) {
                System.out.println("✓ GPS 서버 전송 성공: " + dataToSend.size() + "개 데이터");
                System.out.printf("현재 총 누적거리: %dm (%.2fkm)%n",
                        dataProcessor.getTotalDistanceMeters(),
                        dataProcessor.getTotalDistanceMeters() / 1000.0);
            } else {
                System.err.println("✗ GPS 서버 전송 실패: " + response.getMessage());
                System.err.println("응답 코드: " + response.getCode());

                // 실패한 데이터를 버퍼에 다시 추가
                synchronized (dataBuffer) {
                    dataBuffer.addAll(0, dataToSend);
                }
            }
        } catch (Exception e) {
            System.err.println("전송 중 오류: " + e.getMessage());
            e.printStackTrace();
            synchronized (dataBuffer) {
                dataBuffer.addAll(0, dataToSend);
            }
        }
    }

    private CycleInfoSendRequest createCycleInfoRequest(List<GpsData> dataList) {
        List<CycleInfo> cList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String oTime = LocalDateTime.now().format(formatter);

        for (int i = 0; i < dataList.size(); i++) {
            GpsData gpsData = dataList.get(i);
            double timeInterval = config.getDataGenerationInterval();

            GpsDataProcessor.ProcessedGpsData processed = dataProcessor.processGpsData(gpsData, timeInterval);
            CycleInfo cycleInfo = createCycleInfo(processed, i);
            cList.add(cycleInfo);
        }

        CycleInfoSendRequest request = new CycleInfoSendRequest();
        request.setMdn(config.getDeviceMdn());
        request.setTid("A001");
        request.setMid("6");
        request.setPv("5");
        request.setDid("1");
        request.setOTime(oTime);
        request.setCCnt(String.valueOf(cList.size()));
        request.setCList(cList);

        return request;
    }

    private CycleInfo createCycleInfo(GpsDataProcessor.ProcessedGpsData processed, int sec) {
        CycleInfo cycleInfo = new CycleInfo();
        GpsData gpsData = processed.getOriginalData();

        cycleInfo.setSec(String.format("%02d", sec));
        cycleInfo.setGcd("A"); // GPS 정상 상태
        cycleInfo.setLat((gpsData.getLatitude() * 1_000_000));
        cycleInfo.setLon((gpsData.getLongitude() * 1_000_000));
        cycleInfo.setAng(String.valueOf(processed.getBearing()));
        cycleInfo.setSpd(String.valueOf(processed.getSpeed()));
        cycleInfo.setSum(processed.getTotalDistance());
        cycleInfo.setBat("120"); // 12.0V를 10배한 값

        return cycleInfo;
    }

    public void stop() {
        isRunning = false;
        shutdown();
    }

    public void shutdown() {
        System.out.println("GPS 에뮬레이터 종료 중...");

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }

        isRunning = false;
        System.out.printf("총 주행거리: %dm (%.2fkm)%n",
                dataProcessor.getTotalDistanceMeters(),
                dataProcessor.getTotalDistanceMeters() / 1000.0);
        System.out.println("GPS 에뮬레이터 종료 완료");
    }

    public boolean isRunning() {
        return isRunning;
    }
}