package util

import (
	"emulator/domain"
	"encoding/json"
	"fmt"
)

func ParseControlInfoResponse(body []byte) (*domain.ControlInfoResponse, error) {
	var response domain.ControlInfoResponse
	if err := json.Unmarshal(body, &response); err != nil {
		return nil, fmt.Errorf("failed to parse ControlInfoResponse: %w", err)
	}
	return &response, nil
}
