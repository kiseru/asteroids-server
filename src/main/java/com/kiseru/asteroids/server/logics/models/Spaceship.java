package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.CourseChecker;
import com.kiseru.asteroids.server.logics.Screen;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.auxiliary.Type;
import com.kiseru.asteroids.server.User;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Spaceship extends Point implements Model{
    private final User owner;
    private Direction direction;
    private CourseChecker courseChecker;

    public Spaceship(Coordinates coordinates, User owner) {
        super(coordinates);
        this.owner = owner;
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
    public void crash(Type type) {
        var room = owner.getRoom();
        var game = room.getGame();
        synchronized (game) {
            if (type == Type.ASTEROID) {
                owner.substractScore();
            } else if (type == Type.GARBAGE) {
                owner.addScore();
                room.checkCollectedGarbage(game);
            } else if (type == Type.WALL) {
                // возвращаемся назад, чтобы не находится на стене
                rollbackLastStep();
                owner.substractScore();
            }
            if (!owner.isAlive()) {
                this.destroy();
            }
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
}
