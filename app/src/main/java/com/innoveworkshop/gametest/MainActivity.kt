package com.innoveworkshop.gametest

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DroppingCircle
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.engine.Rectangle

class MainActivity : AppCompatActivity() {
    protected var gameSurface: GameSurface? = null
    protected var upButton: Button? = null
    protected var downButton: Button? = null
    protected var leftButton: Button? = null
    protected var rightButton: Button? = null
    protected var scoreTextView: TextView? = null
    protected var ballCounterTextView: TextView? = null

    protected var game: Game? = null

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
    }

    private fun setupControls() {
        var verticalSpeed = 40f;
        var horizontalSpeed = 40f;

        upButton = findViewById<View>(R.id.up_button) as Button
        upButton!!.setOnClickListener { game!!.ball!!.counter += 1 }

        downButton = findViewById<View>(R.id.down_button) as Button
        downButton!!.setOnClickListener { game!!.ball!!.launchBall() }


        leftButton = findViewById<View>(R.id.left_button) as Button
        //leftButton!!.setOnClickListener { game!!.platfrom!!.position.x -= horizontalSpeed }

        rightButton = findViewById<View>(R.id.right_button) as Button
        //rightButton!!.setOnClickListener { game!!.platfrom!!.position.x += horizontalSpeed }
    }

    inner class Game : GameObject() {
        var firstTrajectory = 0f;
        val circleList = mutableListOf<Circle>()
        var circle: Circle? = null
        var circle1: Circle? = null
        var platfrom: Rectangle? = null
        var ball: DroppingCircle? = null
        var scoreInt = 0;
        //var scoreText: String = "";
        var valueBlue: Int = 100;
        var valueRed: Int = 500;

        //var ballCounter = 1;

        @SuppressLint("SetTextI18n")
        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            //peggle ball
            ball = DroppingCircle(
                (surface!!.width / 2).toFloat(),
                (surface.height / 8).toFloat(),
                30f,
                10f,
                10f,
                Color.rgb(100, 140, 0),
                0,
                2
            )
            surface.addGameObject(ball!!)


//            circle = Circle(
//                (surface.width / 2).toFloat() + 50f,
//                (surface.height / 2).toFloat(),
//                40f,
//                Color.RED
//            )
//            circleList.add(circle!!);
//            surface.addGameObject(circle!!)
//
//            circle1 = Circle(
//                (surface.width / 2).toFloat() - 30f,
//                (surface.height / 2).toFloat() - 150f,
//                40f,
//                Color.BLUE
//            )
//            circleList.add(circle1!!);
//            surface.addGameObject(circle1!!)



            //boundries for height
            var radius = 40f;

            for (i in 1..20) {
                val randomX = (Math.random() * (surface.width - radius)).toFloat() // Random X within a range
                val randomY = (Math.random() * (surface.height - radius)).toFloat()

                // Create circle object based on the condition
                val circle = if (i % 5 == 0) {
                    Circle(randomX, randomY, radius, Color.RED, valueRed)
                } else {
                    Circle(randomX, randomY, radius, Color.BLUE, valueBlue)
                }
//                circle = Circle(
//                (surface.width / 2).toFloat(),
//                (surface.height / 2).toFloat(),
//                40f,
//                Color.RED,
//                valueRed
//            )

                // Add circle to the list and surface
                circleList.add(circle!!)
                surface.addGameObject(circle!!)
            }

            Log.e("ball counter", ball!!.counter.toString())
//            Log.d("Debug", "circleList: ${circleList?.size}")
//            if (circleList == null || circleList.isEmpty()) {
//                Log.e("Error", "Circle list is null or empty.")
//            }
//            Log.d("circleList", "Size of circleList: ${circleList.size}")

//            platfrom = Rectangle(
//                    Vector((surface.width / 3).toFloat(), (surface.height - 50f).toFloat()),
//                    200f,
//                    100f,
//                    Color.GREEN
//                )
//            surface.addGameObject(platfrom!!);

//            surface.addGameObject(
//                DroppingRectangle(
//                    Vector((surface.width / 3).toFloat(), (surface.height / 3).toFloat()),
//                    100f,
//                    100f,
//                    10f,
//                    Color.rgb(128, 14, 80)
//                )
//            )

            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                // This will update the UI safely on the main thread
                ballCounterTextView?.text = "Ball Counter: ${ball!!.counter}"
                scoreTextView?.text = "SCORE: $scoreInt"
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
            for (checkCircle in circleList) {
                if (ball!!.CollideWithCircle(checkCircle)) {
                    scoreInt += checkCircle.value
                    //runOnUiThread(scoreInt.toString())
                    //score?.text = scoreInt.toString()

                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        // This will update the UI safely on the main thread
                        scoreTextView?.text = "SCORE: $scoreInt"
                    }
                    checkCircle.destroy()
                    // Optionally change ball color or handle further logic
                }
            }

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
