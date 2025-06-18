package network

import (
	"net/http"
	"sync"
	"time"
)

type NetworkManager struct {
	client *http.Client
}

var instance *NetworkManager
var once sync.Once

func GetNetworkManager() *NetworkManager {
	once.Do(func() {
		instance = &NetworkManager{
			client: &http.Client{
				Timeout: time.Second * 10,
			},
		}
	})
	return instance
}

func (nm *NetworkManager) Get(url string) (*http.Response, error) {
	return nm.client.Get(url)
}