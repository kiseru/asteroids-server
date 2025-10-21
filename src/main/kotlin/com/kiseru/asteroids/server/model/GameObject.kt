package com.kiseru.asteroids.server.model

sealed class GameObject(var x: Int, var y: Int) {
    class Asteroid(x: Int, y: Int) : GameObject(x, y)
    class Garbage(x: Int, y: Int) : GameObject(x, y)
    class Spaceship(x: Int, y: Int, val user: User) : GameObject(x, y)
}
