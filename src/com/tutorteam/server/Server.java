package com.tutorteam.server;

import com.tutorteam.server.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

final public class Server {

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
            if (command.equals("rating")) {
                rooms.forEach(room -> System.out.println(room.getRating()));
            } else if (command.equals("gamefield")) {
                rooms.forEach(room -> room.getGame().getScreen().display());
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
