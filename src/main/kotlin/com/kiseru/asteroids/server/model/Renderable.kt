package com.kiseru.asteroids.server.model

import com.kiseru.asteroids.server.logics.Screen

interface Renderable {
    
    fun render(screen: Screen)
}