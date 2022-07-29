package com.kiseru.asteroids.server;

import java.io.IOException;

final public class ApplicationRunner {

    public static void main(String[] args) throws IOException {
        Server server = new Server(6501);
        server.up();
    }
}
