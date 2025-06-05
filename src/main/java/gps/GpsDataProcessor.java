package gps;

public class GpsDataProcessor {
    private GpsData previousGpsData;
    private long totalDistanceMeters;
    private long lastUpdateTime;

    public GpsDataProcessor() {
        this.totalDistanceMeters = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public ProcessedGpsData processGpsData(GpsData currentData, double timeIntervalSeconds) {
        ProcessedGpsData result = new ProcessedGpsData();
        result.setOriginalData(currentData);

        if (previousGpsData != null) {
            // 거리 계산
            double distance = GpsCalculator.calculateDistance(
                    previousGpsData.getLatitude(), previousGpsData.getLongitude(),
                    currentData.getLatitude(), currentData.getLongitude()
            );

            // 방향 계산
            int bearing = GpsCalculator.calculateBearing(
                    previousGpsData.getLatitude(), previousGpsData.getLongitude(),
                    currentData.getLatitude(), currentData.getLongitude()
            );

            // 속도 계산
            int speed = GpsCalculator.calculateSpeed(distance, timeIntervalSeconds);

            // 누적 거리 업데이트
            totalDistanceMeters += (long) distance;

            result.setBearing(bearing);
            result.setSpeed(speed);
            result.setDistance(distance);
            result.setTotalDistance(totalDistanceMeters);
        } else {
            // 첫 번째 데이터
            result.setBearing(0);
            result.setSpeed(0);
            result.setDistance(0);
            result.setTotalDistance(0);
        }

        previousGpsData = currentData;
        return result;
    }

    public static class ProcessedGpsData {
        private GpsData originalData;
        private int bearing;
        private int speed;
        private double distance;
        private long totalDistance;

        // Getters and Setters
        public GpsData getOriginalData() { return originalData; }
        public void setOriginalData(GpsData originalData) { this.originalData = originalData; }
        public int getBearing() { return bearing; }
        public void setBearing(int bearing) { this.bearing = bearing; }
        public int getSpeed() { return speed; }
        public void setSpeed(int speed) { this.speed = speed; }
        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }
        public long getTotalDistance() { return totalDistance; }
        public void setTotalDistance(long totalDistance) { this.totalDistance = totalDistance; }
    }

    public long getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void reset() {
        this.previousGpsData = null;
        this.totalDistanceMeters = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }
}