package domain

type ControlInfoResponse struct {
	ResultCode                      string                        `json:"rstCd"`
	ResultMessage                   string                        `json:"rstMsg"`
	Mdn                             int64                         `json:"mdn"`
	OTime                           string                        `json:"oTime"` // formatted as yyyyMMddHHmmss
	ControlCount                    int                           `json:"ctrCnt"`
	GeofenceCount                   int                           `json:"geoCnt"`
	DeviceControlInfoResponseList   []DeviceControlInfoResponse   `json:"crtList"`
	GeofenceControlInfoResponseList []GeofenceControlInfoResponse `json:"geoList"`
}
