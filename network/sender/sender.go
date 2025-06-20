package sender

import (
	"bytes"
	"emulator/domain"
	"emulator/util"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"
)

var (
	baseURL = "https://api.rento.world"
	client  = &http.Client{Timeout: 10 * time.Second}
)

func GetToken(mdn int, firmware string) (string, error) {
	url := baseURL + "/api/devices/token"
	requestBody := domain.DeviceTokenRequest{
		Mdn:         mdn,
		Tid:         "A001",
		Mid:         6,
		Pv:          5,
		Did:         1,
		DFWVer:      firmware,
		CompanyCode: "C1",
	}

	jsonData, err := json.Marshal(requestBody)
	if err != nil {
		return "", fmt.Errorf("failed to marshal token request: %w", err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return "", fmt.Errorf("failed to create token request: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return "", fmt.Errorf("token request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		var result map[string]interface{}
		if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
			return "", fmt.Errorf("failed to parse token response: %w", err)
		}
		if token, ok := result["token"].(string); ok {
			fmt.Println("Token fetched successfully:", token)
			return token, nil
		}
		return "", fmt.Errorf("token field missing in response")
	}

	return "", fmt.Errorf("token request failed: %s", resp.Status)
}

func SendCycleInfoEvent(event domain.CycleEvent, token string) error {
	url := baseURL + "/api/events/cycle-info"

	jsonData, err := json.Marshal(event)
	if err != nil {
		return fmt.Errorf("failed to marshal request: %w", err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)
	req.Header.Set("X-Device-Token", token)

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		fmt.Println("Cycle info event sent successfully")
		return nil
	}

	return fmt.Errorf("server returned status: %s", resp.Status)
}

func SendOnOffEvent(event domain.OnOffEvent, token string, eventType util.EventType) error {
	url := baseURL + "/api/events/on-off"

	// Replace 'EventType' with the correct field name from domain.OnOffEvent, e.g., 'EventType'
	if eventType == util.EventTypeOn {
		url += "/on"
	} else {
		url += "/off"
	}

	jsonData, err := json.Marshal(event)
	if err != nil {
		return fmt.Errorf("failed to marshal request: %w", err)
	}
	fmt.Println("📦 Sending JSON:", string(jsonData))

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)
	req.Header.Set("X-Device-Token", token)

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		fmt.Println("Cycle info event sent successfully")
		return nil
	}

	return fmt.Errorf("server returned status: %s", resp.Status)
}

func GetControlInfo(token string) (*domain.ControlInfoResponse, error) {
	url := baseURL + "/api/devices/get-set-info"

	request := domain.ControlInfo{
		Mdn:                   1,
		TerminalId:            "A001",
		MakerId:               6,
		PacketVersion:         5,
		DeviceId:              1,
		OnTime:                time.Now().UTC().Format("20060102150405"),
		DeviceFirmwareVersion: "v1.0.0",
	}

	respBody, err := sendPostRequest(url, request, token)
	if err != nil {
		return nil, err
	}
	fmt.Println("📥 Response:", string(respBody))
	return util.ParseControlInfoResponse(respBody)
}

func SendGeofenceEvent(token string, request domain.GeofenceEventRequest) error {
	url := baseURL + "/api/events/geofences"

	respBody, err := sendPostRequest(url, request, token)
	if err != nil {
		return err
	}
	fmt.Println("📥 Response:", string(respBody))
	return nil
}

func sendPostRequest(url string, payload interface{}, token string) (respBody []byte, err error) {
	jsonData, err := json.Marshal(payload)
	if err != nil {
		return nil, fmt.Errorf("failed to marshal request: %w", err)
	}
	fmt.Println("📦 Sending JSON:", string(jsonData))

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)
	req.Header.Set("X-Device-Token", token)

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		fmt.Println("Request sent successfully")
		return io.ReadAll(resp.Body)
	}

	return nil, fmt.Errorf("server returned status: %s", resp.Status)
}
