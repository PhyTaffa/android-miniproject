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
    horizontalDamping: Float,
    color: Int,
    value: Int,
    counter: Int
) : Circle(x, y, radius, color, value) {
    private var gravity = 4f;
    public var dropRateY: Float = 10f
    var horizontalDamping: Float = 0f;

    var velocity: Vector = Vector(0f, dropRateY)
    private var velocityDeprecation = 10f;
    private var deprecationPerFrame = velocityDeprecation / 60f

    public var counter = 1;

    private var startX = 0f;
    private var startY = 0f;



    private var isLaunched = false;

    init {

        this.dropRateY = dropRateY

        this.counter = counter;

        this.startX = x
        this.startY = y

        //velocity.x = 8f;
        //velocity.y = 8f;

        // declare vector to be applied each frame, (dumpingX, gravity)
        // apply it each frame as long as the horizontal velocity is higher than 0.

        // create the vector of velocity with x,y and some force multiplier.
        // like the gravity, its applyed to the ball's position and each single time it's reduced to 0.
        // if a value it's 0, don't apply that one.



    }

    override fun onFixedUpdate() {
        super.onFixedUpdate()


        //moves it towards the floor
        //if (!isFloored) position.y += dropRateY

        //if (!isFloored) position.x -= dropRateY
        if(counter > 0 && isLaunched){
            velocityCalculator()
        }

        //is called only once so doesnt reduce the speed as intended
        //if(Math.abs(velocity.x) >= 0 || Math.abs(velocity.y) >= -10f)



    }

    @SuppressLint("DefaultLocale")
    fun velocityCalculator() {


        var minClampX = -35f
        var maxClampX = 35f

        var minClampY = -60f
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

        // Log the current velocities
//        val spingiOstia = String.format(
//            "{\"Applied Velocity X\":%f, \"Applied Velocity Y\":%f}",
//            velocity.x, velocity.y
//        )
//        Log.d("Velocity Current", spingiOstia)
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
        if(!circleCollided.isDestroyed){

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

            if(magnitude < radiusSum)
            {
//                val spingiOstia = String.format(
//                    "{\"DroppingCircle x\":%f, \"DroppingCircle Y\":%f, \"DroppingCircle R\":%f, \"Circle X\":%f, \"Circle Y\":%f, \"Circle R\":%f}",
//                    position.x, position.y, radius,
//                    circleCollided.position.x, circleCollided.position.y, circleCollided.radius
//                )
//
//                Log.v("Json", spingiOstia);

                //dropRateY = 0f;

                BounceCalculator(directionX, directionY, circleCollided.position.y)
                return true;
            }else
                return false;
            }
        return false;
        //return (position.x + width / 2) >= gameSurface!!.width
    }

    @SuppressLint("DefaultLocale")
    private fun BounceCalculator(directionX :Float, directionY: Float, collisionRelativePos: Float){

//        Log.v("Alleged direction from static ball to dropping ball ",
//            "X: $directionX Y:$directionY"
//        )
        var scaleFactorX = 40f;
        var scaleFactorYUp = 200000f;
        var scaleFactorYDown = 1000000f;

        var magnitude = Math.sqrt((directionX * directionX + directionY * directionY).toDouble())
        //resets the velocity for smoother bounce
        velocity.x = 0f;
        velocity.y = 0f;


        velocity.x += directionX/magnitude.toFloat() * scaleFactorX;

        if(this.position.y <= collisionRelativePos)
        {
            velocity.y += directionY/magnitude.toFloat() * scaleFactorYUp;
        }else{
            velocity.y += directionY/magnitude.toFloat() * scaleFactorYDown;
        }


        val spingiOstia = String.format(
            "{\"Applied Velocity X\":%f, \"Applied Velocity Y\":%f}",
            velocity.x, velocity.y
        )
        Log.d("Velocity Applied", spingiOstia)

        //Log.v("suca X",velocity.x.toString())
        //Log.v("suca Y",velocity.y.toString())
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

        this.counter--

        isLaunched = false
    }

    fun launchBall(){

        isLaunched = true;

//        this.dropRateY = 10f;
//        this.gravity = 10f;
    }

}
