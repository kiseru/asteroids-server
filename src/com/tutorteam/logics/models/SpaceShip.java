package com.tutorteam.logics.models;

import com.tutorteam.server.User;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class SpaceShip extends Point implements Model{
    private User owner;
    private Direction direction;

    @Override
    public void render() {
        if (isVisible) {
            // TODO render
        }
    }

    public void crash() {
        // TODO process crashing
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
}
