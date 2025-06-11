package common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeofenceListWrapperDto {
    private List<GeofenceResponseDto> geoList;

    public List<GeofenceResponseDto> getGeoList() {
        return geoList;
    }

    public void setGeoList(List<GeofenceResponseDto> geoList) {
        this.geoList = geoList;
    }
}
