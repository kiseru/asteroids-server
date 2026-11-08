package ru.kiseru.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import ru.kiseru.server.room.Room;

final public class ConnectionReceiver extends Thread {

    private final ServerSocket connectionReceiver;

    ConnectionReceiver(ServerSocket connectionReceiver) {
        this.connectionReceiver = connectionReceiver;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket newConnection = connectionReceiver.accept();
                Room notFullRoom = Server.getNotFullRoom();
                var reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
                var writer = new PrintWriter(newConnection.getOutputStream(), true);
                User user = new User(reader, writer, notFullRoom);
                user.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
