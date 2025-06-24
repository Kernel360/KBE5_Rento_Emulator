package main

import (
	"emulator/gps"
	"emulator/network/sender"
	"emulator/service"
	"emulator/util"
	"fmt"
	"sync"
	"time"
)

func main() {
	var token string
	var wg sync.WaitGroup

	for i := 0; i < util.ThreadCount; i++ {
		wg.Add(1)
		go func(index int) {
			defer wg.Done()

			var err error
			token, err = sender.GetToken(1, "v1.0.0")
			if err != nil {
				fmt.Printf("[#%d] 토큰 요청 실패: %v\n", index, err)
				return
			}
			fmt.Printf("[#%d] 토큰: %s\n", index, token)

			lines, err := util.ReadFileLines("99_course_trip.txt")
			if err != nil {
				fmt.Printf("[#%d] 파일 읽기 오류: %v\n", index, err)
				return
			}

			lastGpsData, err := gps.ParseLineToGpsData(lines[len(lines)-1])
			if err != nil {
				fmt.Printf("[#%d] 마지막 GPS 데이터 파싱 오류: %v\n", index, err)
				return
			}

			contrlInfoResponse, err := sender.GetControlInfo(token)
			if err != nil {
				fmt.Printf("[#%d] 제어 정보 요청 실패: %v\n", index, err)
				return
			}
			fmt.Printf("[#%d] 제어 정보: %+v\n", index, contrlInfoResponse)

			geofenceInfo := contrlInfoResponse.GeofenceControlInfoResponseList[0]

			gposData, err := gps.ParseLineToGpsData(lines[1])
			if err != nil {
				fmt.Printf("[#%d] GPS 데이터 파싱 오류: %v\n", index, err)
				return
			}

			ticker := time.NewTicker(1 * time.Second)
			defer ticker.Stop()

			service.PostOnOffEvent(token, util.EventTypeOn, gposData, 0)

			service.PostCycleEvent(lines, token, ticker, geofenceInfo, lastGpsData)
		}(i)
	}

	wg.Wait()
}
