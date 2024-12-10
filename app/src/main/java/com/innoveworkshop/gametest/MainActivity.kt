package com.innoveworkshop.gametest

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DroppingCircle
import com.innoveworkshop.gametest.assets.Trajectory
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.assets.PegRandomSpawns

class MainActivity : AppCompatActivity() {
    protected var gameSurface: GameSurface? = null
    protected var upButton: Button? = null
    protected var downButton: Button? = null
    protected var leftButton: Button? = null
    protected var rightButton: Button? = null
    protected var smallDecrease: Button? = null
    protected var smallIncrease: Button? = null
    protected var scoreTextView: TextView? = null
    protected var ballCounterTextView: TextView? = null
    protected var debugTextView: TextView? = null

    protected var game: Game? = null

    protected var trajectory: Trajectory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)



        setupControls()
        setUpTexts()
    }

    private fun setUpTexts()
    {
        scoreTextView = findViewById<View>(R.id.score) as TextView

        ballCounterTextView = findViewById<View>(R.id.ballCounter) as TextView

        debugTextView = findViewById<View>(R.id.Debug) as TextView
    }

    private fun setupControls() {
        upButton = findViewById<View>(R.id.up_button) as Button
        upButton!!.setOnClickListener { game!!.ball!!.counter += 1; trajectory?.plotting() }

        downButton = findViewById<View>(R.id.down_button) as Button
        downButton!!.setOnClickListener { game!!.ball!!.launchBall(trajectory!!) }


        leftButton = findViewById<View>(R.id.big_decrease) as Button
        leftButton!!.setOnClickListener { TrajectoryUpdate(-10f) }

        rightButton = findViewById<View>(R.id.big_increase) as Button
        rightButton!!.setOnClickListener { TrajectoryUpdate(+10f) }

        smallDecrease = findViewById<View>(R.id.small_decrease) as Button
        smallDecrease!!.setOnClickListener { TrajectoryUpdate(-1f) }

        smallIncrease = findViewById<View>(R.id.small_increase) as Button
        smallIncrease!!.setOnClickListener { TrajectoryUpdate(+1f) }
    }

    //change this into a function into the trajectory
    private fun TrajectoryUpdate(delta: Float) {
        trajectory?.variateAngle(delta)

        var angelDisplayValue = trajectory!!.angle

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            // This will update the UI safely on the main thread
            debugTextView?.text = "Angle: ${trajectory!!.angle}"
        }
    }

    inner class Game : GameObject() {
        private var circleList = mutableListOf<Circle>()
        var circle: Circle? = null
        var ball: DroppingCircle? = null
        private var scoreInt = 0;
        //var scoreText: String = "";
        private var valueBlue: Int = 100;
        private var valueRed: Int = 300;

        private var startX = 0;
        private var startY = 0

        //var ballCounter = 1;

        @SuppressLint("SetTextI18n")
        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            startX = surface!!.width / 2;
            startY = surface!!.width / 8;

            //Log.e("inizio", startX.toString() + " " + startY.toString())

            //trajecotry
            trajectory = Trajectory(
                (startX).toFloat(),
                (startY).toFloat(),
            )


            //peggle ball
            ball = DroppingCircle(
                (startX).toFloat(),
                (startY).toFloat(),
                40f,
                10f,
                10f,
                Color.rgb(100, 140, 0),
                0,
                2
            )
            surface.addGameObject(ball!!)

            //boundries for height
            var radius = 50f;

//            for (i in 1..20) {
//                val randomX = (Math.random() * (surface.width - radius)).toFloat() // Random X within a range
//                val randomY = (Math.random() * (surface.height - radius)).toFloat()
//
//                // Create circle object based on the condition
//                val circle = if (i % 5 == 0) {
//                    Circle(randomX, randomY, radius, Color.RED, valueRed)
//                } else {
//                    Circle(randomX, randomY, radius, Color.BLUE, valueBlue)
//                }
////                circle = Circle(
////                (surface.width / 2).toFloat(),
////                (surface.height / 2).toFloat(),
////                40f,
////                Color.RED,
////                valueRed
////            )
//
//                // Add circle to the list and surface
//                circleList.add(circle!!)
//                surface.addGameObject(circle!!)
//            }
//
            var pegController = PegRandomSpawns(
                radius = radius,
                colorRed = Color.RED,
                colorBlue = Color.BLUE,
                valueRed = valueRed,
                valueBlue = valueBlue,
                surface = surface,
                screenHeight = surface.height,
                screenWidth = surface.width
            )

            circleList = pegController.generatePegs(20).toMutableList();

            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                // This will update the UI safely on the main thread
                ballCounterTextView?.text = "Ball Counter: ${ball!!.counter}"
                scoreTextView?.text = "SCORE: $scoreInt"
                debugTextView?.text = "Angle: ${trajectory!!.angle}"
            }


        }

        @SuppressLint("SetTextI18n")
        override fun onFixedUpdate() {
            super.onFixedUpdate()

//            if (!circle!!.isFloored && !circle!!.hitRightWall() && !circle!!.isDestroyed) {
//                //moves the circle towards bottom left as long as it doesn't touch the ground and the right wall
//                //circle!!.setPosition(circle!!.position.x + 1, circle!!.position.y + 1)
//            } else {
//                circle!!.destroy()
//            }

            //custom Collision for pegs
            val toRemove = mutableListOf<Circle>() // Temporary list to store circles to be removed

            for (checkCircle in circleList) {
                if (ball!!.CollideWithCircle(checkCircle)) {
                    scoreInt += checkCircle.value

                    // Update the score on the main thread
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        // This will update the UI safely on the main thread
                        scoreTextView?.text = "SCORE: $scoreInt"
                    }

                    checkCircle.destroy() // Call destroy on the collided circle
                    // Add the circle to the list of circles to remove
                    toRemove.add(checkCircle)
                }
            }
            // After the loop, remove the circles from the original list
            circleList.removeAll(toRemove)


            //wall cheeks
            if (ball!!.hitRightWall())
                ball!!.mirrorXVelocity()

            if (ball!!.hitLeftWall())
                ball!!.mirrorXVelocity()

            if (ball!!.hitCeiling())
                ball!!.mirrorYVelocity()

            //amogus
            if (ball!!.isFloored) {

                ball!!.resetPosition()

                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    // This will update the UI safely on the main thread
                    ballCounterTextView?.text = "Ball Counter: ${ball!!.counter}"
                }


                //Log.e("ball counter", ball!!.counter.toString())
            }
        }


    }
}
