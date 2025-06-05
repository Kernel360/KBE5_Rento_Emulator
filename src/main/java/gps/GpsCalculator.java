package gps;

public class GpsCalculator {

    // 지구 반지름 (km)
    private static final double EARTH_RADIUS = 6371.0;

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 위도, 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 위도, 경도 차이
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine 공식
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 (km -> m로 변환)
        return EARTH_RADIUS * c * 1000;
    }

    public static int calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        // 위도, 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);

        // 방향 계산
        double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);

        double bearingRad = Math.atan2(y, x);

        // 라디안을 도(degree)로 변환하고 0-360 범위로 정규화
        double bearingDeg = Math.toDegrees(bearingRad);
        return (int) ((bearingDeg + 360) % 360);
    }

    public static int calculateSpeed(double distanceMeters, double timeSeconds) {
        if (timeSeconds <= 0) return 0;

        // m/s -> km/h 변환 (3.6을 곱함)
        double speedKmh = (distanceMeters / timeSeconds) * 3.6;
        return Math.min((int) Math.round(speedKmh), 255); // 최대 255km/h로 제한
    }
}