package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.logics.CourseChecker;
import com.kiseru.asteroids.server.model.Coordinates;
import com.kiseru.asteroids.server.model.Direction;
import com.kiseru.asteroids.server.model.Point;
import com.kiseru.asteroids.server.model.Type;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class SpaceShip extends Point {

    private final Lock lock = new ReentrantLock();

    private User owner;
    private Direction direction;
    private CourseChecker courseChecker;

    public SpaceShip(Coordinates coordinates, User owner) {
        super(coordinates);
        this.owner = owner;
    }

    @Override
    public String getSymbolToShow() {
        return String.valueOf(owner.getId());
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    /**
     * делает шаг в текущем направлении
     */
    public void go() {
        switch (direction) {
            case UP:
                setCoordinates(new Coordinates(this.getX(), this.getY() - 1));
                break;
            case RIGHT:
                setCoordinates(new Coordinates(this.getX() + 1, this.getY()));
                break;
            case DOWN:
                setCoordinates(new Coordinates(this.getX(), this.getY() + 1));
                break;
            case LEFT:
                setCoordinates(new Coordinates(this.getX() - 1, this.getY()));
        }
    }

    /**
     * Вызывается при выявлении столкновения корабля с чем-либо
     * @param type: тип объекта, с которым произошло столкновение
     */
    public void crash(Type type) {
        lock.lock();
        try {
            if (type == Type.ASTEROID) {
                owner.substractScore();
            } else if (type == Type.GARBAGE) {
                owner.addScore();
                int collected = owner.getRoom().getGame().incrementCollectedGarbageCount();
                checkCollectedGarbage(collected);
            } else if (type == Type.WALL) {
                // возвращаемся назад, чтобы не находится на стене
                rollbackLastStep();
                owner.substractScore();
            }
            if (!owner.isAlive()) {
                this.destroy();
            }
        } finally {
            lock.unlock();
        }
    }

    private void checkCollectedGarbage(int collected) {
        owner.checkCollectedGarbage(collected);
    }

    private void rollbackLastStep() {
        switch (direction) {
            case UP:
                setCoordinates(new Coordinates(this.getX(), this.getY() + 1));
                break;
            case RIGHT:
                setCoordinates(new Coordinates(this.getX() - 1, this.getY()));
                break;
            case DOWN:
                setCoordinates(new Coordinates(this.getX(), this.getY() - 1));
                break;
            case LEFT:
                setCoordinates(new Coordinates(this.getX() + 1, this.getY()));
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

    public void setCourseChecker(CourseChecker courseChecker) {
        this.courseChecker = courseChecker;
    }

    public boolean isOwnerAlive() {
        return owner.isAlive();
    }
}
