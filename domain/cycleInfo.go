package domain

type CycleInfo struct {
	Sec int     `json:"sec"` // 발생시간 '초', 0~59
	Gcd string  `json:"gcd"` // GPS 상태, ‘A’ : 정상, ‘V’ : 비정상, ‘0’ : 미장착
	Lat float64 `json:"lat"` // GPS 위도, 위도X1000000한 값 (소수점 6자리)
	Lon float64 `json:"lon"` // GPS 경도, 경도X1000000한 값 (소수점 6자리)
	Ang int     `json:"ang"` // 방향, 범위: 0 ~ 365
	Spd int     `json:"spd"` // 속도, 범위: 0 ~ 255 (단위: km/h)
	Sum int64   `json:"sum"` // 누적 주행 거리, 범위: 0 ~ 9999999 (단위: m)
	Bat int     `json:"bat"` // 배터리 전압, 범위: 0 ~ 9999 (단위: V)
}
