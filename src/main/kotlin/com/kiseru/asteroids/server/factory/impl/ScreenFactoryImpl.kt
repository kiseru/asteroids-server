package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.ScreenFactory
import com.kiseru.asteroids.server.model.Screen
import com.kiseru.asteroids.server.properties.AsteroidsProperties
import org.springframework.stereotype.Component

@Component
class ScreenFactoryImpl(
    private val asteroidsProperties: AsteroidsProperties,
) : ScreenFactory {

    override fun createScreen(): Screen = Screen(asteroidsProperties.screen.width, asteroidsProperties.screen.height)
}