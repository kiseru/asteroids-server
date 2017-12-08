package com.tutorteam.logics.models;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class Garbage extends Point implements Model{
    public void crash() {
        this.isVisible = false;
    }

    @Override
    public void render() {
        if (isVisible) {
            // TODO render
        }
    }

    @Override
    public Type getType() {
        return Type.GARBAGE;
    }
}
