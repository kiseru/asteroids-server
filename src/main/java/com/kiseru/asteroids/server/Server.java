package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public final class Server {

    private int port;
    private static final List<Room> rooms = new LinkedList<>();
    private static Room notFullRoom = new Room();

    public Server(int port) {
        this.port = port;
    }

    public void up() throws IOException {
        ServerSocket server = new ServerSocket(port);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(server);
        connectionReceiver.start();
        Scanner sc = new Scanner(System.in);
        while (true) {
            String command = sc.nextLine();
            if ("rating".equals(command)) {
                rooms.forEach(room -> System.out.println(room.getRating()));
            } else if ("gamefield".equals(command)) {
                rooms.forEach(room -> System.out.println(room.getGame().getScreen().display()));
            } else if ("exit".equals(command)) {
                break;
            }
        }
    }

    public static Room getNotFullRoom() {
        if (!notFullRoom.isFull()) return notFullRoom;

        rooms.add(notFullRoom);
        notFullRoom = new Room();
        return notFullRoom;
    }
}