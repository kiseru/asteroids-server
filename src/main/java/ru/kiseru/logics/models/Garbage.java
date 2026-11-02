package ru.kiseru.logics.models;

import ru.kiseru.logics.Screen;
import ru.kiseru.logics.auxiliary.Coordinates;
import ru.kiseru.logics.auxiliary.Type;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Garbage extends Point implements Model, Crashable{
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
