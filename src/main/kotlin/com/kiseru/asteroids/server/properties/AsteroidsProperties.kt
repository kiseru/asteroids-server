package com.kiseru.asteroids.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("asteroids")
data class AsteroidsProperties(
    var numberOfAsteroidCells: Int,
    var numberOfGarbageCells: Int,
    var server: ServerProperties,
    var screen: ScreenProperties,
) {

    data class ServerProperties(
        var port: Int,
    )

    data class ScreenProperties(
        var height: Int,
        var width: Int,
    )
}