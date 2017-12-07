package com.tutorteam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final public class ConnectionReceiver extends Thread {

    private ServerSocket connectionReceiver;

    public ConnectionReceiver(ServerSocket connectionReceiver) {
        this.connectionReceiver = connectionReceiver;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket newConnection = connectionReceiver.accept();
                User user = new User(newConnection);
                user.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
