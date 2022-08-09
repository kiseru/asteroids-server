package com.kiseru.asteroids.server.factory

import com.kiseru.asteroids.server.model.Screen

interface ScreenFactory {

    fun createScreen(): Screen
}