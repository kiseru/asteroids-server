package com.kiseru.asteroids.server.model

enum class Direction {
    UP {
        override fun go(point: Point) {
            point.y -= 1
        }

        override fun rollbackLastStep(point: Point) {
            point.y += 1
        }
    },
    DOWN {
        override fun go(point: Point) {
            point.y += 1
        }

        override fun rollbackLastStep(point: Point) {
            point.y -= 1
        }
    },
    LEFT {
        override fun go(point: Point) {
            point.x -= 1
        }

        override fun rollbackLastStep(point: Point) {
            point.x += 1
        }
    },
    RIGHT {
        override fun go(point: Point) {
            point.x += 1
        }

        override fun rollbackLastStep(point: Point) {
            point.x -= 1
        }
    };

    abstract fun go(point: Point)

    abstract fun rollbackLastStep(point: Point)
}