package com.kiseru.asteroids.server.service.impl;

import com.kiseru.asteroids.server.room.Room;
import com.kiseru.asteroids.server.service.RoomService;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RoomServiceImpl implements RoomService {

    private final List<Room> rooms = new ArrayList<>();

    private Room notFullRoom = new Room(this);

    @Override
    public synchronized void writeRatings(OutputStream outputStream) {
        for (Room room : rooms) {
            writeRating(room, outputStream);
        }
    }

    private void writeRating(Room room, OutputStream outputStream) {
        try {
            var rating = room.getRating();
            outputStream.write((rating + "\n").getBytes());
        } catch (IOException e) {
            System.out.println("Failed to write the room's rating");
        }
    }

    @Override
    public synchronized void writeGameFields(OutputStream outputStream) {
        for (Room room : rooms) {
            writeGameField(room, outputStream);
        }
    }

    private void writeGameField(Room room, OutputStream outputStream) {
        try {
            var game = room.getGame();
            var screen = game.getScreen();
            var gameField = screen.display();
            outputStream.write((gameField + "\n").getBytes());
        } catch (IOException e) {
            System.out.println("Failed to write the room's game field");
        }
    }

    @Override
    public synchronized Room getNotFullRoom() {
        if (!notFullRoom.isFull()) {
            return notFullRoom;
        }

        rooms.add(notFullRoom);
        notFullRoom = new Room(this);
        return notFullRoom;
    }
}
