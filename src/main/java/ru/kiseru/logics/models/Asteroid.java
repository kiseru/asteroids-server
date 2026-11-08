package ru.kiseru.logics.models;

import ru.kiseru.logics.Screen;
import ru.kiseru.logics.auxiliary.Coordinates;
import ru.kiseru.logics.auxiliary.Type;

public class Asteroid extends Point implements Model, Crashable {

    public Asteroid(Coordinates coordinates) {
        super(coordinates);
    }

    public void crash() {
        this.isVisible = false;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(coordinates, "A");
        }
    }

    @Override
    public Type getType() {
        return Type.ASTEROID;
    }
}
