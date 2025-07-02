package util

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"

	_ "embed"
)

var CourseTripText string

//go:embed data/busan.txt
var busanText string

//go:embed data/sejong.txt
var sejongText string

//go:embed data/dangsan.txt
var dangsanText string

//go:embed data/goyang.txt
var goyangText string

// round‑robin state
var (
	dataFiles   = []string{busanText, sejongText, dangsanText, goyangText}
	currentFile = 0
)

func ReadFileLines(filename string) ([]string, error) {
	var texts string
	switch filename {
	case "busan.txt":
		texts = busanText
	case "sejong.txt":
		texts = sejongText
	case "dangsan.txt":
		texts = dangsanText
	case "goyang.txt":
		texts = goyangText
	default:
		return nil, fmt.Errorf("unknown file: %s", filename)
	}
	lines := strings.Split(texts, "\n")
	if len(lines) > MaxReadValue {
		return lines[:MaxReadValue], nil
	}
	return lines, nil
}

// SetCourseTripText selects which embedded data to read by filename
func SetCourseTripText(filename string) error {
	switch filename {
	case "busan.txt":
		CourseTripText = busanText
	case "sejong.txt":
		CourseTripText = sejongText
	case "dangsan.txt":
		CourseTripText = dangsanText
	case "goyang.txt":
		CourseTripText = goyangText
	default:
		return fmt.Errorf("unknown file: %s", filename)
	}
	return nil
}

func GetProjectRelativePath(relPath string) string {
	exePath, _ := os.Executable()
	dir := filepath.Dir(exePath)
	return filepath.Join(dir, relPath)
}

func GetCourseTripLines() []string {
	return strings.Split(CourseTripText, "\n")
}
