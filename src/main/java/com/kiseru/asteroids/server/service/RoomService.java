package com.kiseru.asteroids.server.service;

import com.kiseru.asteroids.server.room.Room;
import java.io.OutputStream;

public interface RoomService {

    void writeRatings(OutputStream outputStream);

    void writeGameFields(OutputStream outputStream);

    Room getNotFullRoom();
}
