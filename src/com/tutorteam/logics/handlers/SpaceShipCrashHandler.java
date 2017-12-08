package com.tutorteam.logics.handlers;

import com.tutorteam.logics.Game;
import com.tutorteam.logics.models.SpaceShip;

/**
 * @author Bulat Giniyatullin
 * 08 Декабрь 2017
 */

public class SpaceShipCrashHandler {
    private SpaceShip spaceShip;
    private Game game;

    public SpaceShipCrashHandler(Game game, SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
        this.game = game;
    }

    public void check() {
        // TODO crash handling
    }
}
