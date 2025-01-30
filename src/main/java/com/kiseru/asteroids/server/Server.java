package com.kiseru.asteroids.server;

import com.kiseru.asteroids.server.room.Room;

import com.kiseru.asteroids.server.service.RoomService;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

final public class Server {

    @Deprecated
    private static final List<Room> rooms = new LinkedList<>();

    private final RoomService roomService;
    private final int port;

    public Server(RoomService roomService, int port) {
        this.roomService = roomService;
        this.port = port;
    }

    public void up() throws IOException {
        ServerSocket server = new ServerSocket(port);
        ConnectionReceiver connectionReceiver = new ConnectionReceiver(server, roomService);
        new Thread(connectionReceiver).start();
        Scanner sc = new Scanner(System.in);
        while (true) {
            String command = sc.nextLine();
            if (command.equals("rating")) {
                roomService.writeRatings(System.out);
            } else if (command.equals("gamefield")) {
                roomService.writeGameFields(System.out);
            }
        }
    }
}
