package common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import gps.GPSEmulator;
import gps.GpsData;
import request.GeofenceEventRequest;
import request.OffEventRequest;
import request.OnEventRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventSender {

    private static EventSender instance;

    private DeviceService service;
    private EmulatorConfig config;
    private Supplier<List<GpsData>> gpsDataSupplier;
    private Supplier<GPSEmulator> emulatorSupplier;
    private Supplier<String> onEventTimeSupplier;
    private Consumer<String> onEventTimeSetter;
    private Consumer<String> oTimeSetter;

    private EventSender() {
        // private 생성자
    }

    public static EventSender getInstance() {
        if (instance == null) {
            instance = new EventSender();
        }
        return instance;
    }

    public void init(DeviceService service,
                     EmulatorConfig config,
                     Supplier<List<GpsData>> gpsDataSupplier,
                     Supplier<GPSEmulator> emulatorSupplier,
                     Supplier<String> onEventTimeSupplier,
                     Consumer<String> onEventTimeSetter,
                     Consumer<String> oTimeSetter) {
        this.service = service;
        this.config = config;
        this.gpsDataSupplier = gpsDataSupplier;
        this.emulatorSupplier = emulatorSupplier;
        this.onEventTimeSupplier = onEventTimeSupplier;
        this.onEventTimeSetter = onEventTimeSetter;
        this.oTimeSetter = oTimeSetter;
    }

    public boolean sendOnEventRequest() {
        if (!service.hasValidToken()) return false;

        List<GpsData> gpsDataList = gpsDataSupplier.get();
        if (gpsDataList.isEmpty()) return false;

        String onEventTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        onEventTimeSetter.accept(onEventTime);

        GpsData first = gpsDataList.get(0);
        OnEventRequest request = new OnEventRequest(
                onEventTime, null, 'A',
                first.getLatitude(), first.getLongitude(), "0", "0", 0L
        );

        BaseDeviceRequest.setDefaults(request, config.getDeviceMdn());
        return send("OnEvent", service.sendOnEventRequest(request));
    }

    public void sendOffEventRequest() {
        if (!service.hasValidToken()) return;

        List<GpsData> gpsDataList = gpsDataSupplier.get();
        if (gpsDataList.isEmpty()) return;

        String offTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String onTime = onEventTimeSupplier.get();
        GpsData last = gpsDataList.get(gpsDataList.size() - 1);

        OffEventRequest request = new OffEventRequest(
                onTime, offTime, 'A',
                last.getLatitude(), last.getLongitude(), "0", "0",
                emulatorSupplier.get().getTotalDistanceMeters()
        );

        BaseDeviceRequest.setDefaults(request, config.getDeviceMdn());
        send("OffEvent", service.sendOffEventRequest(request));
    }

    public void sendGeofenceRequest(int eventVal) {
        if (!service.hasValidToken()) return;

        List<GpsData> gpsDataList = gpsDataSupplier.get();
        if (gpsDataList.isEmpty()) return;

        String oTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        onEventTimeSetter.accept(oTime);

        GpsData first = gpsDataList.get(0);
        GeofenceEventRequest request = new GeofenceEventRequest(
                oTime, 1, 1, eventVal, 'A',
                first.getLatitude(), first.getLongitude(), "0", "0", 0L
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("보내는 JSON: " + json);

        BaseDeviceRequest.setDefaults(request, config.getDeviceMdn());
        send("Geofence", service.sendGeofenceRequest(request));
    }

    private boolean send(String name, HttpClientManager.ApiResponse<String> response) {
        if (response.isSuccess()) {
            System.out.printf("%s 전송 성공%n", name);
            return true;
        } else {
            System.err.printf("%s 전송 실패: %s (응답코드: %s)%n", name, response.getMessage(), response.getCode());
            return false;
        }
    }
}
