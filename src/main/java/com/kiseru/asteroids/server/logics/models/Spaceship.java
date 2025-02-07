package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.CourseChecker;
import com.kiseru.asteroids.server.logics.Game;
import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.room.Room;
import com.kiseru.asteroids.server.room.RoomStatus;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Spaceship extends Point implements Model{
    private final User owner;
    private final Lock lock;
    private final Condition condition;
    private Direction direction;
    private CourseChecker courseChecker;

    public Spaceship(Coordinates coordinates, User owner, Lock lock, Condition condition) {
        super(coordinates);
        this.owner = owner;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(coordinates, String.valueOf(owner.getId()));
        }
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

    /**
     * Вызывается при выявлении столкновения корабля с чем-либо
     * @param type: тип объекта, с которым произошло столкновение
     */
    public void crash(Room room, Type type) {
        var game = room.getGame();
        synchronized (game) {
            if (type == Type.ASTEROID) {
                owner.subtractScore(room);
            } else if (type == Type.GARBAGE) {
                owner.addScore(room);
                lock.lock();
                checkCollectedGarbage(room, game);
                condition.signalAll();
                lock.unlock();
            } else if (type == Type.WALL) {
                // возвращаемся назад, чтобы не находится на стене
                rollbackLastStep();
                owner.subtractScore(room);
            }
            if (!owner.isAlive()) {
                this.destroy();
            }
        }
    }

    private static void checkCollectedGarbage(Room room, Game game) {
        int collected = game.incrementCollectedGarbageCount();
        if (collected >= game.getGarbageNumber()) {
            room.setStatus(RoomStatus.FINISHED);
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

    /**
     * разрушение корабля - прекращение его отображения
     */
    private void destroy() {
        this.isVisible = false;
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

    public void setCourseChecker(CourseChecker courseChecker) {
        this.courseChecker = courseChecker;
    }

    public User getOwner() {
        return owner;
    }
}
