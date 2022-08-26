package com.kiseru.asteroids.server.model

enum class Direction {
    UP {
        override fun go(point: Point) {
            point.y -= 1
        }

        override fun rollback(point: Point) {
            point.y += 1
        }

        override fun isWall(point: Point, screen: Screen): Boolean = point.y == 1

        override fun checkContaining(point: Point, coordinates: List<Point>): Boolean =
            coordinates.any { it.x == point.x && it.y == point.y - 1 }
    },
    DOWN {
        override fun go(point: Point) {
            point.y += 1
        }

        override fun rollback(point: Point) {
            point.y -= 1
        }

        override fun isWall(point: Point, screen: Screen): Boolean = point.y == screen.height

        override fun checkContaining(point: Point, coordinates: List<Point>): Boolean =
            coordinates.any { it.x == point.x && it.y == point.y + 1 }
    },
    LEFT {
        override fun go(point: Point) {
            point.x -= 1
        }

        override fun rollback(point: Point) {
            point.x += 1
        }

        override fun isWall(point: Point, screen: Screen): Boolean = point.x == 1

        override fun checkContaining(point: Point, coordinates: List<Point>): Boolean =
            coordinates.any { it.x == point.x - 1 && it.y == point.y }
    },
    RIGHT {
        override fun go(point: Point) {
            point.x += 1
        }

        override fun rollback(point: Point) {
            point.x -= 1
        }

        override fun isWall(point: Point, screen: Screen): Boolean = point.x == screen.width

        override fun checkContaining(point: Point, coordinates: List<Point>): Boolean =
            coordinates.any { it.x == point.x + 1 && it.y == point.y }
    };

    abstract fun go(point: Point)

    abstract fun rollback(point: Point)

    abstract fun isWall(point: Point, screen: Screen): Boolean

    abstract fun checkContaining(point: Point, coordinates: List<Point>): Boolean
}