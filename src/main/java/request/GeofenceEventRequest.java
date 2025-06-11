package request;

import common.BaseDeviceRequest;

import java.time.LocalDateTime;

public class GeofenceEventRequest extends BaseDeviceRequest {
    String oTime;
    int geoGrpId;
    int geoPid;
    int evtVal;
    Character gcd;
    Double lat;
    Double lon;
    String ang;
    String spd;
    Long sum;

    public GeofenceEventRequest() {
        super();
    }

    public GeofenceEventRequest(String oTime, int geoGrpId, int geoPid, int evtVal, Character gcd, Double lat,
                                Double lon, String ang, String spd, Long sum) {
        super();
        this.oTime = oTime;
        this.geoGrpId = geoGrpId;
        this.geoPid = geoPid;
        this.evtVal = evtVal;
        this.gcd = gcd;
        this.lat = lat;
        this.lon = lon;
        this.ang = ang;
        this.spd = spd;
        this.sum = sum;
    }

    public String getoTime() {
        return oTime;
    }

    public void setoTime(String oTime) {
        this.oTime = oTime;
    }

    public int getGeoGrpId() {
        return geoGrpId;
    }

    public void setGeoGrpId(int geoGrpId) {
        this.geoGrpId = geoGrpId;
    }

    public int getGeoPid() {
        return geoPid;
    }

    public void setGeoPid(int geoPid) {
        this.geoPid = geoPid;
    }

    public int getEvtVal() {
        return evtVal;
    }

    public void setEvtVal(int evtVal) {
        this.evtVal = evtVal;
    }

    public Character getGcd() {
        return gcd;
    }

    public void setGcd(Character gcd) {
        this.gcd = gcd;
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
}
