package com.tutorteam.server;

import java.io.IOException;
import java.net.ServerSocket;

final public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void up() throws IOException {
        ServerSocket server = new ServerSocket(port);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(server);
        connectionReceiver.start();
    }
}
