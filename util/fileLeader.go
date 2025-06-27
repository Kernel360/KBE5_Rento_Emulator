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

func ReadFileLines() ([]string, error) {
	lines := strings.Split(CourseTripText, "\n")
	if len(lines) > MaxReadValue {
		return lines[:MaxReadValue], nil
	}
	return lines, nil
}

func GetProjectRelativePath(relPath string) string {
	exePath, _ := os.Executable()
	dir := filepath.Dir(exePath)
	return filepath.Join(dir, relPath)
}

func GetCourseTripLines() []string {
	return strings.Split(CourseTripText, "\n")
}
