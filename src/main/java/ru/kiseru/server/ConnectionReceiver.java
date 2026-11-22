package ru.kiseru.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

import ru.kiseru.logics.User;
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
                writer.println("Welcome To Asteroids Server");
                writer.println("Please, introduce yourself!");
                var user = new User(ID_GENERATOR.incrementAndGet(), reader.readLine());
                var userHandler = new UserHandler(reader, writer, notFullRoom, user);
                new Thread(userHandler::run).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
