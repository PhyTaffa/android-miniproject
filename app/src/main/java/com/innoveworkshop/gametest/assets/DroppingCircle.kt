package com.innoveworkshop.gametest.assets

import android.annotation.SuppressLint
import android.util.Log
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.Vector

class DroppingCircle(
    x: Float,
    y: Float,
    radius: Float,
    dropRateY: Float,

    color: Int,
    value: Int,
    counter: Int
) : Circle(x, y, radius, color, value) {
    private var gravity = 4f;
    var dropRateY: Float = 10f


    var velocity: Vector = Vector(0f, dropRateY)
    private var velocityDeprecation = 10f;
    private var deprecationPerFrame = velocityDeprecation / 60f

    public var counter = 3;

    private var startX = 0f;
    private var startY = 0f;



    public var isLaunched = false;

    init {

        this.dropRateY = dropRateY

        this.counter = counter;

        this.startX = x
        this.startY = y

    }

    override fun onFixedUpdate() {
        super.onFixedUpdate()

        if(counter >= 0 && isLaunched){
            velocityCalculator()
        }
    }

    @SuppressLint("DefaultLocale")
    fun velocityCalculator() {


        var minClampX = -50f
        var maxClampX = 50f

        var minClampY = -70f
        var maxClampY = 50f


        // X Velocity Damping (Friction/Drag)
        if (Math.abs(velocity.x) <= 0f) {
            velocity.x = 0f
        } else {
            // Apply linear velocity reduction (friction)
            velocity.x -= Math.signum(velocity.x) * deprecationPerFrame
        }

        var airResistanceFactor = 0.1f
        var smallThreshold = 0.01f

        // Apply air resistance (for upward motion)
        if (velocity.y > 0) {
            velocity.y -= velocity.y * airResistanceFactor  // Slow down upwards motion due to air resistance
        }

        // Apply gravity (always pulling downwards)
        velocity.y += gravity  // Gravity should always be considered, regardless of motion direction

        // Prevent the object from getting stuck at zero velocity and ensure smooth transition
        if (Math.abs(velocity.y) < smallThreshold) {
            // Once the velocity becomes very small, allow gravity to take over and pull down
            if (velocity.y >= 0) {
                velocity.y = 0f  // Stop upward motion once it reaches near zero
            }
            // Ensure gravity is applied immediately after the velocity reaches near zero
            if (velocity.y == 0f) {
                velocity.y += gravity  // Gravity starts pulling down after stop
            }
        }


        // Clamping the velocity between -100 and 100 for both X and Y
        velocity.x = clamp(velocity.x, minClampX, maxClampX)
        velocity.y = clamp(velocity.y, minClampY, maxClampY)

        // Now that the velocity is calculated and clamped, update the position
        position.x += velocity.x
        position.y += velocity.y
    }

    // Clamp function to limit the velocity between a minimum and maximum value
    private fun clamp(value: Float, min: Float, max: Float): Float {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }




    @SuppressLint("DefaultLocale")
    fun CollideWithCircle(circleCollided: Circle): Boolean {
        if(!circleCollided.isDestroyed){ // cna be deleted due to listing like in schindler's

            var radiusSum = (this.radius + circleCollided.radius).toDouble();

            var distanceXsquared :Double;
            distanceXsquared = Math.pow((circleCollided.position.x - this.position.x).toDouble(), 2.toDouble());
            var directionX: Float
            directionX = (this.position.x - circleCollided.position.x);

            var distanceYsquared : Double;
            distanceYsquared = Math.pow((circleCollided.position.y - this.position.y).toDouble() , 2.toDouble())
            var directionY: Float
            directionY = (this.position.y - circleCollided.position.y);

            var magnitude :Double;
            magnitude = Math.sqrt( distanceXsquared + distanceYsquared )

            if(magnitude <= radiusSum)
            {
                BounceCalculator(directionX, directionY, circleCollided.position.y)
                return true;
            }else
                return false;
            }
        return false;
    }

    private fun BounceCalculator(directionX: Float, directionY: Float, currentPosY: Float, scaleFactorX: Float = 45f, scaleFactorYUp: Float = 50f, scaleFactorYDown: Float = 20f, bounceMultiplier: Float = 1.2f) {
        // Calculate the magnitude of the direction vector
        val magnitude = Math.sqrt((directionX * directionX + directionY * directionY).toDouble()).toFloat()

        // Avoid division by zero if magnitude is zero
        if (magnitude == 0f) return

        // Normalize direction vector
        val normalizedDirectionX = directionX / magnitude
        val normalizedDirectionY = directionY / magnitude

        velocity.x = 0f
        velocity.y = 0f

        // Apply scaling factors to direction components
        velocity.x += normalizedDirectionX * scaleFactorX

        // Adjust vertical velocity with a moderate multiplier for a controlled bounce
        if (currentPosY >= this.position.y) { // ball comining from the top
            // Moderate upward bounce with a toned-down multiplier
            velocity.y += normalizedDirectionY * scaleFactorYUp * bounceMultiplier
        } else {
            // Controlled downward bounce
            velocity.y += normalizedDirectionY * scaleFactorYDown * bounceMultiplier
        }

        // Optional: Damping effect to slow down the bounce over time
        //if (Math.abs(velocity.x) < 0.5f) velocity.x = 0f // Slow down horizontally
        //if (Math.abs(velocity.y) < 0.5f) velocity.y = 0f // Slow down vertically
    }





    fun mirrorXVelocity()
    {
        this.velocity.x *= -0.99f;
    }

    fun mirrorYVelocity()
    {
        this.velocity.y *= -0.99f;
    }

    fun resetPosition(){
        this.position.x = startX
        this.position.y = startY

        this.velocity.x = 0f
        this.velocity.y = 0f



        isLaunched = false
    }

    fun launchBall(trajectoryRef: Trajectory){

        var (rotationX, rotationY) = trajectoryRef.plotting()

        BounceCalculator(rotationX, rotationY, this.position.y)

        if(counter >= 0)
            this.counter--
        else
            this.counter = 0

        isLaunched = true;

    }

}
