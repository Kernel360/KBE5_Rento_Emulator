package domain

type OnOffEvent struct {
	Mdn                        int64   `json:"mdn"` // 차량 번호
	TerminalId                 string  `json:"tid"` // A001로 고정
	MakerId                    int     `json:"mid"` // 6으로 고정
	PacketVersion              int     `json:"pv"`  // 5로 고정
	DeviceId                   int     `json:"did"` // 1로 고정
	OnTime                     string  `json:"onTime"`
	OffTime                    *string `json:"offTime,omitempty"`
	GpsCondition               string  `json:"gcd"` // GPS 상태
	Latitude                   float64 `json:"lat"` // 위도, 소수점 6자리
	Longitude                  float64 `json:"lon"` // 경도, 소수점 6자리
	Angle                      int     `json:"ang"` // 방향
	Speed                      int     `json:"spd"` // 속도
	CurrentAccumulatedDistance int64   `json:"sum"` // 누적 주행 거리
}
