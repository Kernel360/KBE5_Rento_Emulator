package request;

import common.BaseDeviceRequest;

public class OnEventRequest extends BaseDeviceRequest {
    String onTime;
    String offTime;
    Character gcd;
    Double lat;
    Double lon;
    String ang;
    String spd;
    Long sum;

    public OnEventRequest() {
        super();
    }

    public OnEventRequest(String onTime, String offTime, Character gcd, Double lat, Double lon, String ang, String spd, Long sum) {
        super();
        this.onTime = onTime;
        this.offTime = offTime;
        this.gcd = gcd;
        this.lat = lat;
        this.lon = lon;
        this.ang = ang;
        this.spd = spd;
        this.sum = sum;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getAng() {
        return ang;
    }

    public void setAng(String ang) {
        this.ang = ang;
    }

    public String getSpd() {
        return spd;
    }

    public void setSpd(String spd) {
        this.spd = spd;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public Character getGcd() {
        return gcd;
    }

    public void setGcd(Character gcd) {
        this.gcd = gcd;
    }
}
