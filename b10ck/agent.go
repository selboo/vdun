package main

import (
	"net/url"
	"time"

	log "github.com/Sirupsen/logrus"
	"github.com/codegangsta/cli"
	"github.com/gorilla/websocket"
)

func block(message []byte) {
	// TODO: 处理 Master 端推送的 IP
}

func agent(c *cli.Context) {
	u := url.URL{Scheme: "ws", Host: c.String("master"), Path: "/ws"}
	log.Printf("Master is %s", u.String())

	for {
		conn, _, err := websocket.DefaultDialer.Dial(u.String(), nil)
		if err != nil {
			log.Println("Dial: ", err)
			time.Sleep(5 * time.Second)
			continue
		}
		for {
			_, message, err := conn.ReadMessage()
			if err != nil {
				log.Println(err)
				break
			}
			log.Println("Got: %s", message)
			go block(message)
		}
		conn.Close()
	}
}
