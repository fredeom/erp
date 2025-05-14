package tcp_server

import (
	"fmt"
	"log"
	"net"
	"time"

	"github.com/fredeom/erp_server/storage"
)

type TCPServer struct {
	host string
	port string
	r    storage.Repository
}

type TCPClient struct {
	conn net.Conn
	r    storage.Repository
}

type Config struct {
	Host string
	Port string
}

func NewTCPServer(config *Config, r storage.Repository) *TCPServer {
	return &TCPServer{
		host: config.Host,
		port: config.Port,
		r:    r,
	}
}

func (server *TCPServer) Run() {
	listener, err := net.Listen("tcp", fmt.Sprintf("%s:%s", server.host, server.port))
	if err != nil {
		log.Fatal(err)
	}
	defer listener.Close()

	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Fatal(err)
		}
		client := &TCPClient{
			conn: conn,
			r:    server.r,
		}
		go client.handleRequest()
	}
}

func (client *TCPClient) handleRequest() {
	storedTm := ""
	for {
		tm, tmErr := client.r.MC.Get("notification_time")
		if tmErr == nil && (string(tm) != storedTm) {
			client.conn.Write([]byte("{\"time\": \"" + string(tm) + "\"}"))
			storedTm = string(tm)
		}
		time.Sleep(10 * time.Second)
	}
}
