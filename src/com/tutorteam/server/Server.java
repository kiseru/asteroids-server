package com.tutorteam.server;

import com.tutorteam.server.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

final public class Server {

    private int port;
    private static final Set<Room> rooms = new HashSet<>();
    private static Room notFullRoom = new Room();

    public Server(int port) {
        this.port = port;
    }

    public void up() throws IOException {
        ServerSocket server = new ServerSocket(port);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(server);
        connectionReceiver.start();
    }

    public static Room getNotFullRoom() {
        if (!notFullRoom.isFull()) return notFullRoom;

        rooms.add(notFullRoom);
        notFullRoom = new Room();
        return notFullRoom;
    }
}