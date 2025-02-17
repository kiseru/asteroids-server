package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.CourseChecker;
import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.room.RoomStatus;
import java.util.function.Consumer;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Spaceship extends Point {
    private final User owner;
    private final CourseChecker courseChecker;
    private Direction direction;

    public Spaceship(Coordinates coordinates, User owner, CourseChecker courseChecker) {
        super(coordinates);
        this.owner = owner;
        this.courseChecker = courseChecker;
    }

    @Override
    public String view() {
        if (owner == null) {
            return "";
        }

        return String.valueOf(owner.getId());
    }

    /**
     * делает шаг в текущем направлении
     */
    public void go() {
        switch (direction) {
            case UP:
                coordinates = new Coordinates(this.getX(), this.getY() - 1);
                break;
            case RIGHT:
                coordinates = new Coordinates(this.getX() + 1, this.getY());
                break;
            case DOWN:
                coordinates = new Coordinates(this.getX(), this.getY() + 1);
                break;
            case LEFT:
                coordinates = new Coordinates(this.getX() - 1, this.getY());
        }
    }

    public void crash(Game game, Type type, RoomStatus roomStatus, Consumer<RoomStatus> onRoomStatusUpdate) {
        if (type == Type.ASTEROID) {
            if (roomStatus == RoomStatus.GAMING) {
                owner.subtractScore();
            }
        } else if (type == Type.GARBAGE) {
            if (roomStatus == RoomStatus.GAMING) {
                owner.addScore();
            }
            checkCollectedGarbage(game, onRoomStatusUpdate);
        } else if (type == Type.WALL) {
            // возвращаемся назад, чтобы не находится на стене
            rollbackLastStep();
            if (roomStatus == RoomStatus.GAMING) {
                owner.subtractScore();
            }
        }
        if (!owner.isAlive()) {
            this.destroy();
        }
    }

    private static void checkCollectedGarbage(Game game, Consumer<RoomStatus> onRoomStatusUpdate) {
        int collected = game.incrementCollectedGarbageCount();
        if (collected >= game.getGarbageNumber()) {
            onRoomStatusUpdate.accept(RoomStatus.FINISHED);
        }
    }

    private void rollbackLastStep() {
        switch (direction) {
            case UP:
                coordinates = new Coordinates(this.getX(), this.getY() + 1);
                break;
            case RIGHT:
                coordinates = new Coordinates(this.getX() - 1, this.getY());
                break;
            case DOWN:
                coordinates = new Coordinates(this.getX(), this.getY() - 1);
                break;
            case LEFT:
                coordinates = new Coordinates(this.getX() + 1, this.getY());
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Type getType() {
        return Type.SPACESHIP;
    }

    public CourseChecker getCourseChecker() {
        return courseChecker;
    }

    public User getOwner() {
        return owner;
    }
}
