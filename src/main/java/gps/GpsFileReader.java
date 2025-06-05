package gps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GpsFileReader {
    public static List<GpsData> readGpsData(String fileName) throws IOException {
        System.out.println("GPS 읽기 시작: " + fileName);

        List<GpsData> gpsDatas = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        for(String line : lines) {
            line = line.trim();
            if(line.isEmpty()) continue;

            String[] parts = line.split("\\s+");

            if(parts.length >= 6) {
                try{
                    String category = parts[0];
                    String date = parts[1];
                    String datetime = parts[2];
                    String vehicleType = parts[3];
                    double latitude = Double.parseDouble(parts[4]);
                    double longitude = Double.parseDouble(parts[5]);

                    GpsData gpsData = new GpsData(category, date, datetime, vehicleType, latitude, longitude);

                    gpsDatas.add(gpsData);
                } catch (NumberFormatException e) {
                    System.err.println("데이터 파싱 오류 (건너뜀) : " + line);
                }
            } else {
                System.err.println("형식이 올바르지 않은 라인 (건너뜀) : " + line);
            }
        }
        System.out.println("총 " + gpsDatas.size() + " 개의 GPS 로드 완료");

        return gpsDatas;
    }
}