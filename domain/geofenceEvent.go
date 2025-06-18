package domain

type GeofenceEventRequest struct {
	Mdn             int64   `json:"mdn"`
	TerminalId      string  `json:"tid"`
	MakerId         int     `json:"mid"`
	PacketVersion   int     `json:"pv"`
	DeviceId        int     `json:"did"`
	OTime           string  `json:"oTime"` // yyyyMMddHHmmss
	GeofenceGroupId int     `json:"geoGrpId"`
	GeofencePointId int     `json:"geoPId"`
	EventValue      int     `json:"evtVal"`
	GpsCondition    string  `json:"gcd"`
	Latitude        float64 `json:"lat"`
	Longitude       float64 `json:"lon"`
	Angle           int     `json:"ang"`
	Speed           int     `json:"spd"`
	Sum             int64   `json:"sum"`
}
