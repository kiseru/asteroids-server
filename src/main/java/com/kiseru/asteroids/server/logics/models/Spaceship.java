package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.User;
import com.kiseru.asteroids.server.logics.CourseChecker;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.auxiliary.Type;

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
