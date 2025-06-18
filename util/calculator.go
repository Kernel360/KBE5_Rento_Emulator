package util

import (
	"math"
)

func IsInsideGeofenceHaversine(lat1, lon1, lat2, lon2, radiusKm float64) bool {
	const EarthRadiusKm = 6371.0

	latDistance := toRadians(lat2 - lat1)
	lonDistance := toRadians(lon2 - lon1)

	a := math.Sin(latDistance/2)*math.Sin(latDistance/2) +
		math.Cos(toRadians(lat1))*math.Cos(toRadians(lat2))*
			math.Sin(lonDistance/2)*math.Sin(lonDistance/2)

	c := 2 * math.Atan2(math.Sqrt(a), math.Sqrt(1-a))
	distance := EarthRadiusKm * c

	return distance <= radiusKm
}

func toRadians(deg float64) float64 {
	return deg * math.Pi / 180
}

func CalculateDistanceHaversine(lat1, lon1, lat2, lon2 float64) int {
	const EarthRadiusKm = 6371.0

	latDistance := toRadians(lat2 - lat1)
	lonDistance := toRadians(lon2 - lon1)

	a := math.Sin(latDistance/2)*math.Sin(latDistance/2) +
		math.Cos(toRadians(lat1))*math.Cos(toRadians(lat2))*
			math.Sin(lonDistance/2)*math.Sin(lonDistance/2)

	c := 2 * math.Atan2(math.Sqrt(a), math.Sqrt(1-a))
	distance := EarthRadiusKm * c

	return int(distance * 1000)
}
