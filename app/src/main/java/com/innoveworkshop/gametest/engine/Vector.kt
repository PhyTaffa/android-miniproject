package com.innoveworkshop.gametest.engine

class Vector(@JvmField var x: Float, @JvmField var y: Float) {
    fun set(x: Float, y: Float) {
        this.x = x;
        this.y = y;
    }
}
