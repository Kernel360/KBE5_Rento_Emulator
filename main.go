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

// list of data files to process in round‑robin
var dataFiles = []string{"busan.txt", "sejong.txt", "yeongdeungpo.txt", "goyang.txt"}

func main() {
	util.StopSignal = make(chan struct{})
	guiApp := app.NewWithID("rento.emulator")

	statusLabel := widget.NewLabel("")
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

	maxReadValueEntry := widget.NewEntry()
	maxReadValueEntry.SetText("1000")
	maxReadValueEntry.SetPlaceHolder("주행 거리 (1–10000)")
	maxReadValueEntry.OnChanged = func(s string) {
		if val, err := strconv.Atoi(s); err != nil || val < 1 {
			maxReadValueEntry.SetText("1")
		} else if val > 10000 {
			maxReadValueEntry.SetText("10000")
		}
	}

	urlToggle := widget.NewRadioGroup([]string{"Local", "Deploy"}, func(value string) {
		if value == "Deploy" {
			sender.UseDeployURL = true
		} else {
			sender.UseDeployURL = false
		}
	})
	urlToggle.Horizontal = true
	urlToggle.SetSelected("Local")

	runButton := widget.NewButton("Start Emulator", func() {
		statusLabel.SetText("● 실행 중")
		statusLabel.TextStyle = fyne.TextStyle{Bold: true}
		statusLabel.Refresh()

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

		maxReadValue, err3 := strconv.Atoi(maxReadValueEntry.Text)
		if err3 != nil || maxReadValue < 1 {
			maxReadValue = 1
		} else if maxReadValue > 10000 {
			maxReadValue = 10000
		}
		util.MaxReadValue = maxReadValue

		util.ThreadCount = threadCount
		util.CycleCount = cycleCount

		go runEmulator()
	})

	stopButton := widget.NewButton("Stop Emulator", func() {
		close(util.StopSignal)
		util.StopSignal = make(chan struct{})
		threadCountEntry.Enable()
		cycleCountEntry.Enable()

		statusLabel.SetText("● 중지됨")
		statusLabel.TextStyle = fyne.TextStyle{Bold: true}
		statusLabel.Refresh()
	})

	win.SetContent(container.NewVBox(
		widget.NewLabel("에뮬레이터 설정"),
		container.NewVBox(
			widget.NewLabel("차량 대수: (1–3000)"),
			container.NewMax(threadCountEntry),
			widget.NewLabel("주행 개수: (1–60)"),
			container.NewMax(cycleCountEntry),
			widget.NewLabel("주행 거리: (1–10000)"),
			container.NewMax(maxReadValueEntry),
		),
		urlToggle,
		runButton,
		stopButton,
		statusLabel,
	))

	win.ShowAndRun()
}

func runEmulator() {
	stopSignal := util.StopSignal
	var token string
	var wg sync.WaitGroup

	fmt.Println("에뮬레이터 시작 - 차량 대수:", util.ThreadCount, "주행 개수:", util.CycleCount)
	var tokenList []string = make([]string, 0, util.ThreadCount)
	for i := 0; i < util.ThreadCount; i++ {
		wg.Add(1)
		go func(index int) {
			// pick which file to read this goroutine
			filename := dataFiles[index%len(dataFiles)]
			if err := util.SetCourseTripText(filename); err != nil {
				fmt.Printf("[#%d] 파일 선택 오류: %v\n", index+1, err)
				wg.Done()
				return
			}

			defer wg.Done()

			var err error
			token, err = sender.GetToken(i+1, "v1.0.0")
			tokenList = append(tokenList, token)
			if err != nil {
				msg := fmt.Sprintf("토큰:", tokenList[index])
				fmt.Print(msg)
				return
			}
			msg := fmt.Sprintf("[#%d] 토큰: %s\n", index+1, tokenList[index])
			fmt.Print(msg)

			lines, err := util.ReadFileLines(filename)
			if err != nil {
				msg := fmt.Sprintf("[#%d] 파일 읽기 오류: %v\n", index+1, err)
				fmt.Print(msg)
				return
			}

			lastGpsData, err := gps.ParseLineToGpsData(lines[len(lines)-1])
			if err != nil {
				msg := fmt.Sprintf("[#%d] 마지막 GPS 데이터 파싱 오류: %v\n", index+1, err)
				fmt.Print(msg)
				return
			}

			contrlInfoResponse, err := sender.GetControlInfo(tokenList[index])
			if err != nil {
				msg := fmt.Sprintf("[#%d] 제어 정보 요청 실패: %v\n", index+1, err)
				fmt.Print(msg)
				return
			}
			msg = fmt.Sprintf("[#%d] 제어 정보: %+v\n", index+1, contrlInfoResponse)
			fmt.Print(msg)

			geofenceInfo := contrlInfoResponse.GeofenceControlInfoResponseList[0]

			gposData, err := gps.ParseLineToGpsData(lines[1])
			if err != nil {
				msg := fmt.Sprintf("[#%d] GPS 데이터 파싱 오류: %v\n", index+1, err)
				fmt.Print(msg)
				return
			}

			ticker := time.NewTicker(1 * time.Second)
			go func() {
				<-stopSignal
				ticker.Stop()
				service.PostOnOffEvent(tokenList[index], util.EventTypeOff, lastGpsData, 0, int64(index+1))
			}()
			defer ticker.Stop()

			service.PostOnOffEvent(tokenList[index], util.EventTypeOn, gposData, 0, int64(index+1))

			service.PostCycleEvent(lines, tokenList[index], ticker, geofenceInfo, lastGpsData, int64(index+1))
		}(i)
	}

	wg.Wait()
}
