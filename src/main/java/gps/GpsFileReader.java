package gps;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GpsFileReader {
    public static List<GpsData> readGpsData(String resourcePath) throws IOException {
        System.out.println("GPS 읽기 시작 (리소스): " + resourcePath);

        List<GpsData> gpsDatas = new ArrayList<>();

        try (
                InputStream is = GpsFileReader.class.getResourceAsStream(resourcePath)
        ) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split("\\s+");
                    if (parts.length >= 6) {
                        try {
                            String category = parts[0];
                            String date = parts[1];
                            String datetime = parts[2];
                            String vehicleType = parts[3];
                            double latitude = Double.parseDouble(parts[4]);
                            double longitude = Double.parseDouble(parts[5]);

                            GpsData gpsData = new GpsData(category, date, datetime, vehicleType, latitude, longitude);
                            gpsDatas.add(gpsData);
                        } catch (NumberFormatException e) {
                            System.err.println("데이터 파싱 오류 (건너뜀): " + line);
                        }
                    } else {
                        System.err.println("형식이 올바르지 않은 라인 (건너뜀): " + line);
                    }
                }
            }
        } catch (NullPointerException e) {
            throw new FileNotFoundException("리소스를 찾을 수 없습니다: " + resourcePath);
        }

        System.out.println("총 " + gpsDatas.size() + " 개의 GPS 로드 완료");
        return gpsDatas;
    }
}