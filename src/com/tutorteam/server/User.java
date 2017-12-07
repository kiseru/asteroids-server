package com.tutorteam.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

final public class User extends Thread {

    private BufferedReader reader;
    private PrintWriter writer;
    private String userName;

    public User(Socket newConnection) throws IOException {
        reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
        writer = new PrintWriter(newConnection.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            writer.println("Welcome To Asteroids Server");
            writer.println("Please, introduce yourself!");
            userName = reader.readLine();
            writer.println("You need to keep a space garbage.");
            writer.println("You need to keep a space garbage.");
            writer.println("Good luck, Commander!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
