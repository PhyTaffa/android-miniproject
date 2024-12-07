package com.innoveworkshop.gametest.assets

import android.annotation.SuppressLint
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
    private var gravity = 10f;
    public var dropRateY: Float = 10f
    var horizontalDamping: Float = 0f;

    var velocity: Vector = Vector(0f, dropRateY)
    private var velocityDeprecation = 10f;
    private var deprecationPerFrame = velocityDeprecation / 240f

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

    fun velocityCalculator() {
        position.x += velocity.x;
        position.y += velocity.y;
        //Log.e("speed", velocity.y.toString())


        if (Math.abs(velocity.x) <= 0f) {
            velocity.x = 0f;
        } else {
            //Log.e("speed X", velocity.x.toString())
            velocity.x -= Math.signum(velocity.x) * deprecationPerFrame;
        }

        if (Math.abs(velocity.y) <= gravity) {
            velocity.y = dropRateY;
        } else {
            //Log.e("speed Y", velocity.y.toString())
            velocity.y -= Math.signum(velocity.y) * deprecationPerFrame;
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

                BounceCalculator(directionX, directionY, magnitude)
                return true;
            }else
                return false;
            }
        return false;
        //return (position.x + width / 2) >= gameSurface!!.width
    }

    private fun BounceCalculator(directionX :Float, directionY: Float, magnitude: Double){

//        Log.v("Alleged direction from static ball to dropping ball ",
//            "X: $directionX Y:$directionY"
//        )

        velocity.x = 0f;
        velocity.y = 0f;

        velocity.x += (directionX/magnitude.toFloat() * magnitude.toFloat());
        velocity.y += (directionY/magnitude.toFloat() * magnitude.toFloat());

        //Log.v("suca X",velocity.x.toString())
        //Log.v("suca Y",velocity.y.toString())
    }

    fun mirrorXVelocity()
    {
        this.velocity.x *= -1;
    }

    fun mirrorYVelocity()
    {
        this.velocity.y *= -1;
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
