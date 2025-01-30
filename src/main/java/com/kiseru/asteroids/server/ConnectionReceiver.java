package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.room.Room;

import com.kiseru.asteroids.server.service.RoomService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiver implements Runnable {

    private final ServerSocket connectionReceiver;
    private final RoomService roomService;

    public ConnectionReceiver(
            ServerSocket connectionReceiver,
            RoomService roomService
    ) {
        this.connectionReceiver = connectionReceiver;
        this.roomService = roomService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket newConnection = connectionReceiver.accept();
                Room notFullRoom = roomService.getNotFullRoom();
                User user = new User(newConnection, notFullRoom, roomService);
                new Thread(user).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
