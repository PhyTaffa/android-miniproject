package com.innoveworkshop.gametest.assets

import android.util.Log
import com.innoveworkshop.gametest.engine.GameObject

class Trajectory(
    x: Float,
    y: Float,

) : GameObject(x, y){

    private var startX = x
    private var startY = y

    public var angle = 0f

    init {

    }

    override fun onFixedUpdate() {
        super.onFixedUpdate()

        Log.d("angle", angle.toString())
        //trajectoryPlotting(angle)
    }

    fun angleVariation(delta: Float){

        angle += delta

        var angelCheck = angle + delta;

        if ((angelCheck) > 90f) {
            angle = 90f
        } else if (angelCheck < -90f) {
            angle = -90f
        }

    }
    //
    fun plotting() : Pair<Float, Float> {
        // Set the radius to 1 (unit distance)
        val radius = 1f

        // Adjust the angle so that 0° points downward (towards increasing Y)
        // Positive angles go right, negative angles go left
        var adjustedAngle = this.angle


        // Convert the angle to radians
        val angleInRadians = Math.toRadians(adjustedAngle.toDouble())

        // Calculate the direction vector components based on the adjusted angle
        val direction_x = (startX) * Math.sin(angleInRadians)  // X = sin(θ)
        val direction_y = (startY) * Math.cos(angleInRadians)  // Y = cos(θ)

        // Calculate the new x and y end positions based on the direction
        val (endX, endY) = normalize(direction_x, direction_y)
//        val endX = direction_x
//        val endY = direction_y

        Log.e("punti finali", "X: $endX  Y: $endY")

        // Return the new position as a Pair of x and y
        return Pair(endX, endY)
    }

    private fun normalize(x: Double, y: Double): Pair<Float, Float> {
        // Calculate the magnitude of the vector (x, y)
        val magnitude = Math.sqrt((x * x + y * y).toDouble()).toFloat()

        // Avoid division by zero (if magnitude is 0, return the original coordinates)
        if (magnitude == 0f) {
            return Pair(x.toFloat(), y.toFloat())
        }

        // Normalize the vector
        val normalizedX = x / magnitude
        val normalizedY = y / magnitude

        return Pair(normalizedX.toFloat(), normalizedY.toFloat())
    }


}
