package com.tutorteam.server;

import com.tutorteam.server.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final public class ConnectionReceiver extends Thread {

    private ServerSocket connectionReceiver;

    ConnectionReceiver(ServerSocket connectionReceiver) {
        this.connectionReceiver = connectionReceiver;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket newConnection = connectionReceiver.accept();
                Room notFullRoom = Server.getNotFullRoom();
                User user = new User(newConnection, notFullRoom);
                user.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
