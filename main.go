package main

import (
	"emulator/gps"
	"emulator/network/sender"
	"emulator/service"
	"emulator/util"
	"fmt"
	"strconv"
	"sync"
	"time"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

func main() {
	guiApp := app.New()
	win := guiApp.NewWindow("Rento Emulator Controller")
	win.Resize(fyne.NewSize(300, 200))

	threadCountEntry := widget.NewEntry()
	threadCountEntry.SetText("1")
	threadCountEntry.SetPlaceHolder("차량 대수 (1–3000)")
	threadCountEntry.OnChanged = func(s string) {
		if val, err := strconv.Atoi(s); err != nil || val < 1 {
			threadCountEntry.SetText("1")
		} else if val > 3000 {
			threadCountEntry.SetText("3000")
		}
	}

	cycleCountEntry := widget.NewEntry()
	cycleCountEntry.SetText("5")
	cycleCountEntry.SetPlaceHolder("주행 개수 (5–60)")
	cycleCountEntry.OnChanged = func(s string) {
		if val, err := strconv.Atoi(s); err != nil || val < 5 {
			cycleCountEntry.SetText("5")
		} else if val > 60 {
			cycleCountEntry.SetText("60")
		}
	}

	runButton := widget.NewButton("Start Emulator", func() {
		threadCountEntry.Disable()
		cycleCountEntry.Disable()

		threadCount, err1 := strconv.Atoi(threadCountEntry.Text)
		if err1 != nil || threadCount < 1 {
			threadCount = 1
		} else if threadCount > 3000 {
			threadCount = 3000
		}

		cycleCount, err2 := strconv.Atoi(cycleCountEntry.Text)
		if err2 != nil || cycleCount < 5 {
			cycleCount = 5
		} else if cycleCount > 60 {
			cycleCount = 60
		}

		util.ThreadCount = threadCount
		util.CycleCount = cycleCount

		go runEmulator()
	})

	stopButton := widget.NewButton("Stop Emulator", func() {
		util.StopSignal <- struct{}{}
		threadCountEntry.Enable()
		cycleCountEntry.Enable()
	})

	win.SetContent(container.NewVBox(
		widget.NewLabel("에뮬레이터 설정"),
		container.NewVBox(
			widget.NewLabel("차량 대수: (1–3000)"),
			container.NewMax(threadCountEntry),
			widget.NewLabel("주행 개수: (1–60)"),
			container.NewMax(cycleCountEntry),
		),
		runButton,
		stopButton,
	))

	win.ShowAndRun()
}

func runEmulator() {
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

			lines, err := util.ReadFileLines()
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
			go func() {
				<-util.StopSignal
				ticker.Stop()
				service.PostOnOffEvent(token, util.EventTypeOff, lastGpsData, 0)
			}()
			defer ticker.Stop()

			service.PostOnOffEvent(token, util.EventTypeOn, gposData, 0)

			service.PostCycleEvent(lines, token, ticker, geofenceInfo, lastGpsData)
		}(i)
	}

	wg.Wait()
}
