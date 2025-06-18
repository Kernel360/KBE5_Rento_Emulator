package domain

type GeofenceControlInfoResponse struct {
	GeoControlId    int64   `json:"geoControlId"`
	UpdateValue     int     `json:"updateValue"`
	GeofenceGroupId int     `json:"geofenceGroupId"`
	GeoEventType    int16   `json:"geoEventType"`
	GeofenceRange   int     `json:"geofenceRange"`
	Latitude        float64 `json:"latitude"`
	Longitude       float64 `json:"longitude"`
	OnTime          string  `json:"onTime"`  // formatted as yyyyMMddHHmmss
	OffTime         string  `json:"offTime"` // formatted as yyyyMMddHHmmss
	StoreType       int16   `json:"storeType"`
}
