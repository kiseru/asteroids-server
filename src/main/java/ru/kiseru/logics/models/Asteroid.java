package ru.kiseru.logics.models;

import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.Type;

public class Asteroid extends Point implements Model, Crashable {

    public Asteroid(Position position) {
        super(position);
    }

    public void crash() {
        this.isVisible = false;
    }

    @Override
    public Type getType() {
        return Type.ASTEROID;
    }
}
