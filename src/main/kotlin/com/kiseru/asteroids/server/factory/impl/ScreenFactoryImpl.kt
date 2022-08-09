package com.kiseru.asteroids.server.factory.impl

import com.kiseru.asteroids.server.factory.ScreenFactory
import com.kiseru.asteroids.server.model.Screen
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ScreenFactoryImpl(
    @Value("\${asteroids.screen.height}") private val height: Int,
    @Value("\${asteroids.screen.width}") private val width: Int,
) : ScreenFactory {

    override fun createScreen(): Screen = Screen(width, height)
}