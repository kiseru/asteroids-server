package com.tutorteam.logics.models;

import com.tutorteam.logics.Screen;
import com.tutorteam.logics.auxiliary.Coordinates;
import com.tutorteam.logics.auxiliary.Direction;
import com.tutorteam.logics.auxiliary.Type;
import com.tutorteam.server.User;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class SpaceShip extends Point implements Model{
    private User owner;
    private Direction direction;

    public SpaceShip(Coordinates coordinates, User owner) {
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
     * Вызывается при выявлении столкновения корабля с чем-либо
     * @param type: тип объекта, с которым произошло столкновение
     */
    public void crash(Type type) {
        if (type == Type.ASTEROID) {
            owner.substractScore();
        } else if (type == Type.GARBAGE) {
            owner.addScore();
        } else if (type == Type.WALL) {
            owner.substractScore();
        }
        if (! owner.isAlive()) {
            this.destroy();
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
}
