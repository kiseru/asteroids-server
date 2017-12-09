package com.tutorteam.logics.models;

import com.tutorteam.logics.Screen;
import com.tutorteam.logics.auxiliary.Coordinates;
import com.tutorteam.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Garbage extends Point implements Model{
    public Garbage(Coordinates coordinates) {
        super(coordinates);
    }

    public void crash() {
        this.isVisible = false;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(coordinates, "G");
        }
    }

    @Override
    public Type getType() {
        return Type.GARBAGE;
    }
}
