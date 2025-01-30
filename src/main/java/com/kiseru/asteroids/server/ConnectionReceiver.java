package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiver implements Runnable {

    private final ServerSocket connectionReceiver;

    public ConnectionReceiver(ServerSocket connectionReceiver) {
        this.connectionReceiver = connectionReceiver;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket newConnection = connectionReceiver.accept();
                Room notFullRoom = Server.getNotFullRoom();
                User user = new User(newConnection, notFullRoom);
                new Thread(user).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
