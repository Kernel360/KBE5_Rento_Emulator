package domain

import "time"

type GpsData struct {
	Type      string
	DateTime  time.Time
	Timestamp string
	Lat       float64
	Lon       float64
}
