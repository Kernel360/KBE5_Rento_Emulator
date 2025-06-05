package gps;

public class GpsData {
    private String category; //구분
    private String date; //일자
    private String datetime; //일시
    private String vehicleType; //차량구분
    private double latitude; //위도
    private double longitude; //경도

    public GpsData(String category, String date, String datetime, String vehicleType, double latitude, double longitude) {
        this.category = category;
        this.date = date;
        this.datetime = datetime;
        this.vehicleType = vehicleType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Getter
    public String getCategory() {
        return category;
    }
    public String getDate(){
        return date;
    }
    public String getDatetime(){
        return datetime;
    }
    public String getVehicleType(){
        return vehicleType;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }

    @Override
    public String toString() {
        return String.format("GPS.gps.GpsData{category = '%s', date = '%s', datetime = '%s', vehicleType = '%s', latitude = '%.6f', longitude = '%.6f'", category, date, datetime, vehicleType, latitude, longitude);
    }
}