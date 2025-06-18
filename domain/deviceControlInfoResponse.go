package domain

type DeviceControlInfoResponse struct {
	ControlId    int64  `json:"controlId"`
	ControlCode  string `json:"controlCode"`
	ControlValue string `json:"controlValue"`
}
