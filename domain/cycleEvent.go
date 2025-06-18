package domain

import "time"

type CycleEvent struct {
	Id                         int64       `json:"id"`             // ID
	DeviceUniqueId             int64       `json:"deviceUniqueId"` // device unique id
	Mdn                        int64       `json:"mdn"`            // 차량 식별 key
	TerminalId                 string      `json:"tid"`            // terminalId
	MakerId                    int         `json:"mid"`            // makerId
	PacketVersion              int         `json:"pv"`             // packetVersion
	DeviceId                   int         `json:"did"`            // deviceId
	GpsCondition               string      `json:"gcd"`            // gps condition (as string for enum)
	Latitude                   string      `json:"lat"`            // 위도 (string to match Java BigDecimal with scale)
	Longitude                  string      `json:"lon"`            // 경도 (string to match Java BigDecimal with scale)
	Angle                      int         `json:"ang"`            // 각도
	Speed                      int         `json:"spd"`            // 속도
	CurrentAccumulatedDistance int64       `json:"sum"`            // 누적 거리
	EventType                  string      `json:"evtVal"`         // event type (as string for enum)
	OTime                      time.Time   `json:"oTime"`          // 발생 시간 (as string to match JSON date string)
	CycleCount                 int         `json:"cCnt"`           // cycle count
	Clist                      []CycleInfo `json:"cList"`          // cycle info list
}
