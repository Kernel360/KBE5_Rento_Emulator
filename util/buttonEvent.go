package util

import "sync"

var (
	StopSignal = make(chan struct{})
	StopOnce   sync.Once
)

var LogUpdateHandler func(string)

// AppendLog appends a log message to the UI
func AppendLog(msg string) {
	if LogUpdateHandler != nil {
		LogUpdateHandler(msg)
	}
}

// ReportError sends an error message to the UI log
func ReportError(err error) {
	AppendLog("❌ 에러 발생: " + err.Error())
}

// StopEmulator safely closes the StopSignal channel once
func StopEmulator() {
	StopOnce.Do(func() {
		close(StopSignal)
	})
}

// SetThreadCount sets the number of concurrent threads
func SetThreadCount(n int) {
	if n >= 1 && n <= 3000 {
		ThreadCount = n
	}
}

// SetCycleCount sets the number of cycles per thread
func SetCycleCount(n int) {
	if n >= 5 && n <= 60 {
		CycleCount = n
	}
}
