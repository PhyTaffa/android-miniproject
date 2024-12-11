package com.innoveworkshop.gametest.assets

import android.util.Log
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    fun variateAngle(delta: Float, circles: List<Circle>, startX: Int, startY: Int) {
        // Update the angle
        angle += delta

        val angelCheck = angle + delta

        // Limit the angle to be within -90 and 90 degrees
        if (angelCheck > 90f) {
            angle = 90f
        } else if (angelCheck < -90f) {
            angle = -90f
        }

        // Rotate each circle in the list
        rotateObject(circles, startX, startY)

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
        val directionX = (radius) * sin(angleInRadians)  // X = sin(θ)
        val directionY = (radius) * cos(angleInRadians)  // Y = cos(θ)

        // Calculate the new x and y end positions based on the direction
        val (endX, endY) = normalize(directionX, directionY)
//        val endX = direction_x
//        val endY = direction_y

        Log.e("punti finali", "X: $endX  Y: $endY")

        // Return the new position as a Pair of x and y
        return Pair(endX, endY)
    }

    private fun normalize(x: Double, y: Double): Pair<Float, Float> {
        // Calculate the magnitude of the vector (x, y)
        val magnitude = sqrt(x * x + y * y).toFloat()

        // Avoid division by zero (if magnitude is 0, return the original coordinates)
        if (magnitude == 0f) {
            return Pair(x.toFloat(), y.toFloat())
        }

        // Normalize the vector
        val normalizedX = x / magnitude
        val normalizedY = y / magnitude

        return Pair(normalizedX.toFloat(), normalizedY.toFloat())
    }


    fun rotateObject(circles: List<Circle>, startX: Int, startY: Int) {
        val angleInRadians = Math.toRadians(angle.toDouble())

        // Loop through each circle with index, print initial position, and apply rotation
        circles.forEachIndexed { index, circle ->
            // Print the position of the circle before rotation
            println("Circle #$index - Before rotation: x = ${circle.position.x}, y = ${circle.position.y}")

            // Translate the object to origin (relative to the rotation center)
            val translatedX = circle.position.x - startX
            val translatedY = circle.position.y - startY

            // Apply the rotation matrix
            val newX = translatedX * cos(angleInRadians) - translatedY * sin(angleInRadians) + startX
            val newY = translatedX * sin(angleInRadians) + translatedY * cos(angleInRadians) + startY

            // Update the circle's position directly
            circle.position.x = newX.toFloat()
            circle.position.y = newY.toFloat()

            // Print the position of the circle after rotation
            println("Circle #$index - After rotation: x = ${circle.position.x}, y = ${circle.position.y}")
        }
    }



}
