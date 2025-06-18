package util

type EventType string

const (
	EventTypeOn        EventType = "ON"
	EventTypeOff       EventType = "OFF"
	EventTypeOnOff     EventType = "ON_OFF"
	EventTypeGeofence  EventType = "GEOFENCE"
	EventTypeCycleInfo EventType = "CYCLE_INFO"
)
