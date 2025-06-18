package service

import (
	"emulator/domain"
	"emulator/gps"
	"emulator/network/sender"
	"emulator/util"
	"fmt"
	"strconv"
	"strings"
	"time"
)

func PostCycleEvent(lines []string, token string, ticker *time.Ticker, geofenceInfo domain.GeofenceControlInfoResponse, lastGpsData domain.GpsData) {
	preGeoEvent := 1
	linesCount := len(lines)
	var gpsDataList []domain.GpsData
	var totalDistance int64 = 1000
	var totalDistanceList []int64 = []int64{}

	for i := 0; i < len(lines); i++ {
		<-ticker.C
		linesCount--
		line := lines[i]
		fields := strings.Fields(line)
		if len(fields) < 6 || i == 0 {
			continue // skip header or malformed line
		}

		dateStr := fields[1] + " " + fields[2]
		parsedTime, err := time.Parse("2006-01-02 15:04:05.00", dateStr)
		if err != nil {
			fmt.Println("시간 파싱 오류:", err)
			continue
		}

		lat, _ := strconv.ParseFloat(fields[4], 64)
		lon, _ := strconv.ParseFloat(fields[5], 64)

		gpsData := domain.GpsData{
			Type:      fields[0],
			DateTime:  parsedTime,
			Timestamp: fields[3],
			Lat:       lat,
			Lon:       lon,
		}

		gpsDataList = append(gpsDataList, gpsData)

		for j := 0; j < len(gpsDataList)-1; j++ {
			d := util.CalculateDistanceHaversine(
				gpsDataList[j].Lat, gpsDataList[j].Lon,
				gpsDataList[j+1].Lat, gpsDataList[j+1].Lon,
			)
			totalDistance += int64(d)
			totalDistanceList = append(totalDistanceList, totalDistance)
		}

		fmt.Printf("Parsed GPS - Type: %s, DateTime: %s, Timestamp: %s, Lat: %.6f, Lon: %.6f\n", gpsData.Type, gpsData.DateTime.Format("2006-01-02 15:04:05"), gpsData.Timestamp, gpsData.Lat, gpsData.Lon)

		var batch []domain.CycleInfo

		isInside := util.IsInsideGeofenceHaversine(gpsData.Lat, gpsData.Lon, geofenceInfo.Latitude, geofenceInfo.Longitude, float64(geofenceInfo.GeofenceRange))
		fmt.Printf("🔍 Geofence check - Lat: %.6f, Lon: %.6f, Inside: %v\n", gpsData.Lat, gpsData.Lon, isInside)

		var curGeoEvent int

		if isInside {
			curGeoEvent = 1
		} else {
			curGeoEvent = 2
		}

		if preGeoEvent != curGeoEvent {
			PostGeofenceEvent(token, gpsData, curGeoEvent, totalDistance)
			preGeoEvent = curGeoEvent
		}

		// Calculate total distance before sending event if we have 10 points

		if len(gpsDataList) == util.CycleCount {
			cycleInfos := gps.ConvertGpsToCycleInfo(gpsDataList, totalDistanceList)

			batch = append(batch, cycleInfos...)

			event := domain.CycleEvent{
				Mdn:                        1,
				TerminalId:                 "A001",
				MakerId:                    6,
				PacketVersion:              5,
				DeviceId:                   1,
				GpsCondition:               "A",
				Latitude:                   fmt.Sprintf("%.6f", batch[len(batch)-1].Lat),
				Longitude:                  fmt.Sprintf("%.6f", batch[len(batch)-1].Lon),
				Angle:                      int(batch[len(batch)-1].Ang),
				Speed:                      int(batch[len(batch)-1].Spd),
				OTime:                      time.Now().UTC().Truncate(time.Microsecond),
				CurrentAccumulatedDistance: totalDistance,
				CycleCount:                 int(len(batch)), // Ensure CycleCount is set as int(len(batch))
				Clist:                      batch,
			}

			err := sender.SendCycleInfoEvent(event, token)
			if err != nil {
				fmt.Println("주기정보 전송 실패:", err)
			} else {
				fmt.Println("주기정보 전송 성공")
			}

			gpsDataList = []domain.GpsData{}
			totalDistanceList = []int64{}
		}
	}

	if linesCount == 0 {
		PostOnOffEvent(token, util.EventTypeOff, lastGpsData, totalDistance)
	}
}

var savedOnTime string // package-level variable to store OnTime

func PostOnOffEvent(token string, eventType util.EventType, gpsData domain.GpsData, totalDistance int64) {
	var onTime string
	var offTime *string

	switch eventType {
	case util.EventTypeOn:
		onTime = time.Now().UTC().Format("20060102150405")
		savedOnTime = onTime // save for later use
		offTime = nil
	case util.EventTypeOff:
		onTime = savedOnTime
		t := time.Now().UTC().Format("20060102150405")
		offTime = &t
	default:
		fmt.Println("알 수 없는 이벤트 타입:", eventType)
		return
	}

	event := domain.OnOffEvent{
		Mdn:                        1,
		TerminalId:                 "A001",
		MakerId:                    6,
		PacketVersion:              5,
		DeviceId:                   1,
		OnTime:                     onTime,
		OffTime:                    offTime,
		GpsCondition:               "A",
		Latitude:                   37.5665,
		Longitude:                  126.978,
		Angle:                      0,
		Speed:                      0,
		CurrentAccumulatedDistance: totalDistance,
	}

	err := sender.SendOnOffEvent(event, token, eventType)
	if err != nil {
		fmt.Println("On/Off 이벤트 전송 실패:", err)
	} else {
		fmt.Println("On/Off 이벤트 전송 성공")
	}
}

func PostGeofenceEvent(token string, gpsData domain.GpsData, eventVal int, totalDistance int64) {

	geofenceEventRequest := domain.GeofenceEventRequest{
		Mdn:             1,
		TerminalId:      "A001",
		MakerId:         6,
		PacketVersion:   5,
		DeviceId:        1,
		OTime:           time.Now().UTC().Format("20060102150405"),
		GeofenceGroupId: 1,
		GeofencePointId: 1,
		EventValue:      eventVal,
		GpsCondition:    "A",
		Latitude:        gpsData.Lat,
		Longitude:       gpsData.Lon,
		Angle:           0,
		Speed:           0,
		Sum:             totalDistance,
	}

	sender.SendGeofenceEvent(token, geofenceEventRequest)
}
