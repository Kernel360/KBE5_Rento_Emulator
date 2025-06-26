package util

import (
	"os"
	"path/filepath"
	"strings"

	_ "embed"
)

//go:embed data/99_course_trip.txt
var CourseTripText string

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
