package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class ConnectionReceiver extends Thread {

    private ServerSocket serverSocket;

    public ConnectionReceiver(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket newConnection = serverSocket.accept();
                Room notFullRoom = Server.getNotFullRoom();
                User user = new User(newConnection, notFullRoom);
                user.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
