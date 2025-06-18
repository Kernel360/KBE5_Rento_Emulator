package domain

type ControlInfo struct {
	Mdn                   int64  `json:"mdn"`    // 차량 번호
	TerminalId            string `json:"tid"`    // A001 고정
	MakerId               int    `json:"mid"`    // 6 고정
	PacketVersion         int    `json:"pv"`     // 5 고정
	DeviceId              int    `json:"did"`    // 단말 ID
	OnTime                string `json:"onTime"` // yyyyMMddHHmmss 형식
	DeviceFirmwareVersion string `json:"dFWVer"` // LTE 1.2 고정
}
