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
                Socket newConection = connectionReceiver.accept();
                User user = new User(newConection);
                user.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
