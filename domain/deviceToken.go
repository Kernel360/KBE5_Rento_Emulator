package domain

type DeviceTokenRequest struct {
	Mdn         int    `json:"mdn"`
	Tid         string `json:"tid"`
	Mid         int    `json:"mid"`
	Pv          int    `json:"pv"`
	Did         int    `json:"did"`
	DFWVer      string `json:"dFWVer"`
	CompanyCode string `json:"companyCode"`
}
