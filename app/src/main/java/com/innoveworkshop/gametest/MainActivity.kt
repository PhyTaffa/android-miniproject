package com.innoveworkshop.gametest

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.innoveworkshop.gametest.assets.DroppingCircle
import com.innoveworkshop.gametest.assets.Trajectory
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.assets.PegRandomSpawns
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class MainActivity : AppCompatActivity() {
    protected var gameSurface: GameSurface? = null
    protected var scoreTextView: TextView? = null
    protected var ballCounterTextView: TextView? = null
    protected var debugTextView: TextView? = null
    protected var controlsLayout: ConstraintLayout? = null

    protected var game: Game? = null

    protected var trajectory: Trajectory? = null
    var ball: DroppingCircle? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)

        setUpTexts()

        controlsLayout = findViewById<View>(R.id.controls_layout) as ConstraintLayout
        controlsLayout!!.setOnTouchListener { _, event ->
            when (event?.action) {
                //Prints position while pressing the screen
                MotionEvent.ACTION_MOVE -> {
                    Log.i("aaaa", "${event.x} ${event.y}")
                    //save the position and calculate the vector connecting it to the ball.
                    var initialTouch = Vector(event.x, event.y)

                    trajectory?.variateAngle(trajectory!!.findAngleTouch(initialTouch, ball!!))
                    //pass the that thing to the trjectory thing.
                    //ball?.findTrajectory(initialTouch)

                }
                //Prints position when first pressed screen (and more)
                MotionEvent.ACTION_DOWN -> {
                    var initialTouch = Vector(event.x, event.y)

                    trajectory?.variateAngle(trajectory!!.findAngleTouch(initialTouch, ball!!))

                    Log.i("ACTION DOWN", "Touch started at: (${event.x}, ${event.y})")
                    Log.i("ANGLE", trajectory!!.angle.toString())
                }
                // i love man
                MotionEvent.ACTION_UP -> {
                    var initialTouch = Vector(event.x, event.y)
                    Log.i("ACTION DOWN", "Touch started at: (${event.x}, ${event.y})")

                    trajectory?.let { ball?.launchBall(it) }
//                    if (initialTouch != null && !hasLaunch) {
//                        val releaseTouch = Vector(event.x, event.y)
//                        val forceVector = Vector(
//                            releaseTouch.x - initialTouch!!.x,
//                            releaseTouch.y - initialTouch!!.y
//                        )
//                        // Reverse the force vector
//                        val reversedForce = forceVector.reverse()
//                        Log.i("VECTOR", "Applied force: (${reversedForce.x}, ${reversedForce.y})")
//
//                        // Apply the reversed force to the circle
//                        bowlingBall?.applyForce(reversedForce)
//                        hasLaunch = true
//                    }
                }
            }
            true
        }

    }

    private fun setUpTexts()
    {
        scoreTextView = findViewById<View>(R.id.score) as TextView

        ballCounterTextView = findViewById<View>(R.id.ballCounter) as TextView

        debugTextView = findViewById<View>(R.id.Debug) as TextView
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

        private var scoreInt = 0;
        //var scoreText: String = "";
        private var valueBlue: Int = 10;
        private var valueRed: Int = 100;

        private var startX = 0;
        private var startY = 0

        private var middle: Vector? = null

        //var ballCounter = 1;

//        fun onDraw(canvas :Canvas) {
//            super.onDraw(canvas);
//
//            var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
//
//            paint.color = Color.GREEN
//            paint.style = Paint.Style.FILL
//
//            //paint.setColor(Color.parseColor("#f58442"));
//            paint.setStrokeWidth(15F);
//            canvas.drawLine(0f, 0f, gameSurface!!.width.toFloat(),
//                gameSurface!!.height.toFloat(), paint);
//        }

        @SuppressLint("SetTextI18n")
        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            middle = Vector(
                (surface!!.width / 2).toFloat(), (surface!!.width / 2).toFloat()
            )

            //onDraw(canvas: Canvas?)

            startX = surface!!.width / 2;
            startY = surface!!.width / 2;

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
                Color.rgb(100, 140, 0),
                0,
                5
            )
            surface.addGameObject(ball!!)

            //boundries for height
            var radius = 50f;
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

            drawBoundries(surface)

        }

        private fun drawBoundries(surface: GameSurface) {
            surface.addGameObject(Rectangle(
                position = Vector(0f, surface.height.toFloat() / 2),
                width = 20f,
                height = surface.height.toFloat(),
                color = Color.MAGENTA
            ))
            surface.addGameObject(Rectangle(
                position = Vector(surface.width.toFloat(), surface.height.toFloat() / 2),
                width = 20f,
                height = surface.height.toFloat(),
                color = Color.MAGENTA
            ))
            surface.addGameObject(Rectangle(
                position = Vector(surface.width.toFloat() / 2, 0f),
                width = surface.width.toFloat(),
                height = 20f,
                color = Color.MAGENTA
            ))
            surface.addGameObject(Rectangle(
                position = Vector(surface.width.toFloat() / 2, surface.height.toFloat()),
                width = surface.width.toFloat(),
                height = 20f,
                color = Color.MAGENTA
            ))
        }

        @SuppressLint("SetTextI18n")
        override fun onFixedUpdate() {
            super.onFixedUpdate()

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
            }
        }


    }
}
