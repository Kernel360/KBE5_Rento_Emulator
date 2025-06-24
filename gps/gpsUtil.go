package gps

import (
	"emulator/domain"
	"fmt"
	"math"
	"strconv"
	"strings"
	"time"
)

// Haversine calculates the distance between two geographic points.
func Haversine(lat1, lon1, lat2, lon2 float64) float64 {
	const R = 6371000
	lat1Rad := lat1 * math.Pi / 180
	lon1Rad := lon1 * math.Pi / 180
	lat2Rad := lat2 * math.Pi / 180
	lon2Rad := lon2 * math.Pi / 180

	dLat := lat2Rad - lat1Rad
	dLon := lon2Rad - lon1Rad

	a := math.Sin(dLat/2)*math.Sin(dLat/2) +
		math.Cos(lat1Rad)*math.Cos(lat2Rad)*math.Sin(dLon/2)*math.Sin(dLon/2)
	c := 2 * math.Atan2(math.Sqrt(a), math.Sqrt(1-a))
	return R * c
}

// bearing calculates the bearing between two points in degrees.
func bearing(lat1, lon1, lat2, lon2 float64) float64 {
	lat1Rad := lat1 * math.Pi / 180
	lat2Rad := lat2 * math.Pi / 180
	dLon := (lon2 - lon1) * math.Pi / 180

	y := math.Sin(dLon) * math.Cos(lat2Rad)
	x := math.Cos(lat1Rad)*math.Sin(lat2Rad) -
		math.Sin(lat1Rad)*math.Cos(lat2Rad)*math.Cos(dLon)
	angle := math.Atan2(y, x) * 180 / math.Pi
	if angle < 0 {
		angle += 360
	}
	return angle
}

// ConvertGpsToCycleInfo generates CycleInfo entries with computed ang, spd, sum fields.
func ConvertGpsToCycleInfo(data []domain.GpsData, totalDistanceList []int64) []domain.CycleInfo {
	var result []domain.CycleInfo

	for i := 0; i < len(data); i++ {
		var dist, speed, angle float64
		if i > 0 {
			prev := data[i-1]
			curr := data[i]

			dist = Haversine(prev.Lat, prev.Lon, curr.Lat, curr.Lon)
			duration := curr.DateTime.Sub(prev.DateTime).Seconds()
			if duration > 0 {
				speed = dist / duration * 3.6
			}
			angle = bearing(prev.Lat, prev.Lon, curr.Lat, curr.Lon)
		}

		curr := data[i]
		info := domain.CycleInfo{
			Sec: func() int {
				base := time.Now()
				t := base.Add(time.Duration(i) * time.Second)
				return t.Second()
			}(),
			Gcd: "A",
			Lat: math.Round(curr.Lat*1e6) / 1e6,
			Lon: math.Round(curr.Lon*1e6) / 1e6,
			Ang: int(angle),
			Spd: int(speed),
			Sum: func() int64 {
				if i == 0 {
					return 0
				}
				return int64(totalDistanceList[i-1])
			}(),
			Bat: 9999,
		}

		result = append(result, info)
	}

	return result
}

// ConvertGpsToGeofenceEvent calculates bearing, speed, and distance between prev and curr, and returns a GeofenceEventRequest.
func ConvertGpsToGeofenceEvent(prev, curr domain.GpsData) domain.GeofenceEventRequest {
	dist := Haversine(prev.Lat, prev.Lon, curr.Lat, curr.Lon)
	duration := curr.DateTime.Sub(prev.DateTime).Seconds()
	speed := 0.0
	if duration > 0 {
		speed = dist / duration * 3.6 // m/s to km/h
	}
	angle := bearing(prev.Lat, prev.Lon, curr.Lat, curr.Lon)

	event := domain.GeofenceEventRequest{
		Mdn:             1,
		TerminalId:      "A001",
		MakerId:         6,
		PacketVersion:   5,
		DeviceId:        1,
		OTime:           curr.DateTime.Format("20060102150405"),
		GeofenceGroupId: 1,
		GeofencePointId: 1,
		EventValue:      1,
		GpsCondition:    "A",
		Latitude:        curr.Lat,
		Longitude:       curr.Lon,
		Angle:           int(angle),
		Speed:           int(speed),
		Sum:             int64(dist),
	}
	return event
}

// ParseLineToGpsData converts a single line of GPS log string into a domain.GpsData struct.
func ParseLineToGpsData(line string) (domain.GpsData, error) {
	fields := strings.Fields(line)
	if len(fields) < 6 {
		return domain.GpsData{}, fmt.Errorf("invalid line format")
	}

	dateTimeStr := fields[1] + " " + fields[2]
	parsedTime, err := time.Parse("2006-01-02 15:04:05.00", dateTimeStr)
	if err != nil {
		return domain.GpsData{}, fmt.Errorf("failed to parse date/time: %v", err)
	}

	lat, err := strconv.ParseFloat(fields[4], 64)
	if err != nil {
		return domain.GpsData{}, fmt.Errorf("invalid latitude: %v", err)
	}

	lon, err := strconv.ParseFloat(fields[5], 64)
	if err != nil {
		return domain.GpsData{}, fmt.Errorf("invalid longitude: %v", err)
	}

	return domain.GpsData{
		Type:      fields[0],
		DateTime:  parsedTime,
		Timestamp: fields[3],
		Lat:       lat,
		Lon:       lon,
	}, nil
}
