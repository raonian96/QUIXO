package com.raonstudio.quixo

enum class Direction {
    TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

    val opposite: Direction
        get() {
            return when (this) {
                TOP -> BOTTOM
                BOTTOM -> TOP
                LEFT -> RIGHT
                RIGHT -> LEFT
                TOP_LEFT -> BOTTOM_RIGHT
                TOP_RIGHT -> BOTTOM_LEFT
                BOTTOM_LEFT -> TOP_RIGHT
                BOTTOM_RIGHT -> TOP_LEFT
            }
        }
}