package com.tutorteam;

import com.tutorteam.server.ConnectionReceiver;

import java.io.IOException;
import java.net.ServerSocket;

final public class ApplicationRunner {

    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(6501);
            ConnectionReceiver connectionReceiver = new ConnectionReceiver(server);
            connectionReceiver.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
