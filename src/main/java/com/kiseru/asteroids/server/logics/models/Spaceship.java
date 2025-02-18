package com.kiseru.asteroids.server.logics.models;

import com.kiseru.asteroids.server.logics.CourseChecker;
import com.kiseru.asteroids.server.logics.auxiliary.Coordinates;
import com.kiseru.asteroids.server.logics.auxiliary.Direction;
import com.kiseru.asteroids.server.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Spaceship extends Point {
    private final int ownerId;
    private final CourseChecker courseChecker;
    private Direction direction;
    private int steps = 0;
    private int score = 0;
    private boolean isAlive = true;

    public Spaceship(Coordinates coordinates, int ownerId, CourseChecker courseChecker) {
        super(coordinates);
        this.ownerId = ownerId;
        this.courseChecker = courseChecker;
    }

    @Override
    public String view() {
        return String.valueOf(ownerId);
    }

    public void addScore() {
        score += 10;
    }

    public void subtractScore() {
        score -= 50;
        isAlive = score >= 0;
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

    public int getScore() {
        return score;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
}
