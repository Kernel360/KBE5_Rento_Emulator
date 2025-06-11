package common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GeofenceResponseDto {
    private int geoControlId;
    private int updateValue;
    private int geofenceGroupId;
    private int geoEventType;
    private BigDecimal geofenceRange;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime onTime;
    private LocalDateTime offTime;
    private int storeType;

    public GeofenceResponseDto() {
    }

    public int getGeoControlId() {
        return geoControlId;
    }

    public void setGeoControlId(int geoControlId) {
        this.geoControlId = geoControlId;
    }

    public int getUpdateValue() {
        return updateValue;
    }

    public void setUpdateValue(int updateValue) {
        this.updateValue = updateValue;
    }

    public int getGeofenceGroupId() {
        return geofenceGroupId;
    }

    public void setGeofenceGroupId(int geofenceGroupId) {
        this.geofenceGroupId = geofenceGroupId;
    }

    public int getGeoEventType() {
        return geoEventType;
    }

    public void setGeoEventType(int geoEventType) {
        this.geoEventType = geoEventType;
    }

    public BigDecimal getGeofenceRange() {
        return geofenceRange;
    }

    public void setGeofenceRange(BigDecimal geofenceRange) {
        this.geofenceRange = geofenceRange;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getOnTime() {
        return onTime;
    }

    public void setOnTime(LocalDateTime onTime) {
        this.onTime = onTime;
    }

    public LocalDateTime getOffTime() {
        return offTime;
    }

    public void setOffTime(LocalDateTime offTime) {
        this.offTime = offTime;
    }

    public int getStoreType() {
        return storeType;
    }

    public void setStoreType(int storeType) {
        this.storeType = storeType;
    }

    @Override
    public String toString() {
        return "GeofenceResponseDto{" +
                "geoControlId=" + geoControlId +
                ", updateValue=" + updateValue +
                ", geofenceGroupId=" + geofenceGroupId +
                ", geoEventType=" + geoEventType +
                ", geofenceRange=" + geofenceRange +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", onTime=" + onTime +
                ", offTime=" + offTime +
                ", storeType=" + storeType +
                '}';
    }
}
