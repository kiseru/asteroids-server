package ru.kiseru.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;
import ru.kiseru.server.room.Room;

public final class ConnectionReceiver {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private final ServerSocket connectionReceiver;

    ConnectionReceiver(ServerSocket connectionReceiver) {
        this.connectionReceiver = connectionReceiver;
    }

    public void handle() {
        while (true) {
            try {
                Socket newConnection = connectionReceiver.accept();
                Room notFullRoom = Server.Companion.getNotFullRoom();
                var reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
                var writer = new PrintWriter(newConnection.getOutputStream(), true);
                var userHandler = new UserHandler(ID_GENERATOR.incrementAndGet(), reader, writer, notFullRoom);
                new Thread(userHandler::run).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
