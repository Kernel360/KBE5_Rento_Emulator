package util

import (
	"bufio"
	"os"
	"path/filepath"
)

func ReadFileLines(filename string) ([]string, error) {
	path := filepath.Join("resources", filename)
	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	var lines []string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
		if len(lines) > MaxReadValue {
			return lines, nil // Limit to 200 lines
		}

	}

	if err := scanner.Err(); err != nil {
		return nil, err
	}

	return lines, nil
}
