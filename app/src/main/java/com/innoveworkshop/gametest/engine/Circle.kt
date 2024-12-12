package com.innoveworkshop.gametest.engine

import android.graphics.Canvas
import android.graphics.Paint

open class Circle(x: Float, y: Float, var radius: Float, var color: Int, var value: Int) : GameObject(x, y), Caged {
    // Set up the paint.
    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawCircle(position.x, position.y, radius, paint)
    }

    override fun hitLeftWall(): Boolean {
        return (position.x - radius - radius/2) <= 0
    }

    override fun hitRightWall(): Boolean {
        return (position.x + radius + radius/2) >= gameSurface!!.width
    }

    override val isFloored: Boolean
        get() = (position.y + radius) >= gameSurface!!.height

    fun hitCeiling(): Boolean {
        return (position.y - radius) <= 0f
    }
}
