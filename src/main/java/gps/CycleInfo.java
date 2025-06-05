package gps;

public class CycleInfo {
    private String sec; //발생시간 '초', 'ss'
    private String gcd; //gps 상태, ‘A’ : 정상, ‘V’ : 비정상 ‘0’ : 미장착
    private Double lat; //GPS 위도, 위도X1000000한값(소수점6자리)
    private Double lon; //GPS 경도, 위도X1000000한값(소수점6자리)
    private String ang; //방향, 범위 : 0 ~ 365
    private String spd; //속도, 범위 : 0 ~ 255(단위: km/h)
    private Long sum; //누적 주행 거리, 범위 : 0 ~ 9999999(단위 : m)
    private String bat; //배터리 전압, 범위 : 0 ~ 9999(실제 값X10, 단위: V)

    public String getBat() {
        return bat;
    }

    public void setBat(String bat) {
        this.bat = bat;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public String getSpd() {
        return spd;
    }

    public void setSpd(String spd) {
        this.spd = spd;
    }

    public String getAng() {
        return ang;
    }

    public void setAng(String ang) {
        this.ang = ang;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getGcd() {
        return gcd;
    }

    public void setGcd(String gcd) {
        this.gcd = gcd;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }
}