package com.innoveworkshop.gametest

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

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
    private var circleTrajList = mutableListOf<Circle>()
    private var startX = 0;
    private var startY = 0

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
        downButton!!.setOnClickListener { if(!game!!.ball!!.isLaunched){
                                                game!!.ball!!.launchBall(trajectory!!)
                                            }
                                        }



        leftButton = findViewById<View>(R.id.big_decrease) as Button
        leftButton!!.setOnClickListener { TrajectoryUpdate(-10f, circleTrajList, startX, startY) }

        rightButton = findViewById<View>(R.id.big_increase) as Button
        rightButton!!.setOnClickListener { TrajectoryUpdate(+10f, circleTrajList, startX, startY) }

        smallDecrease = findViewById<View>(R.id.small_decrease) as Button
        smallDecrease!!.setOnClickListener { TrajectoryUpdate(-1f, circleTrajList, startX, startY) }

        smallIncrease = findViewById<View>(R.id.small_increase) as Button
        smallIncrease!!.setOnClickListener { TrajectoryUpdate(+1f, circleTrajList, startX, startY) }
    }

    //change this into a function into the trajectory
    private fun TrajectoryUpdate(delta: Float, circles: List<Circle>, startX: Int, startY: Int) {
        trajectory?.variateAngle(delta, circles, startX, startY)

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
        private var valueBlue: Int = 10;
        private var valueRed: Int = 100;



        private var circleTrajListPos = mutableListOf<Vector>()
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

            //onDraw(canvas: Canvas?)

            startX = surface!!.width / 2;
            startY = surface!!.width / 8;

            Log.e("inizio", startX.toString() + " " + startY.toString())

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
            simulateTrajecotry(surface)

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

        private fun simulateTrajecotry(surface: GameSurface) {
            val drawRadius = 50f;
            val drawDeltaY = (startY).toFloat() + drawRadius * 2

            val numbOfCircleTraj = 4

            repeat(numbOfCircleTraj){ index ->

                val vectorPos = Vector(
                    startX.toFloat(),
                    (drawDeltaY + drawRadius * 2 * index)
                )
                val circleTraj: Circle = Circle(
                    vectorPos.x,
                    vectorPos.y,
                    drawRadius,
                    Color.MAGENTA,
                    0
                )
                circleTrajList.add(circleTraj)
                surface.addGameObject(circleTraj)

                circleTrajListPos.add(vectorPos)

            }
        }


        @SuppressLint("SetTextI18n")
        override fun onFixedUpdate() {
            super.onFixedUpdate()

            //displayRotateCircleTrajectory()

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

// Save the positions of the circles
            if (ball!!.isLaunched) {
//                circleTrajList.forEachIndexed { i, circleTraj ->
//                    circleTrajListPos[i].set(circleTraj.position.x, circleTraj.position.y)
//                    circleTraj.position.x = -100f
//                    circleTraj.position.y = -100f
//                }
            }

// Restore the positions of the circles
            if (ball!!.isFloored) {
                ball!!.resetPosition()
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    ballCounterTextView?.text = "Ball Counter: ${ball!!.counter}"
                }

//                circleTrajList.forEachIndexed { i, circleTraj ->
//                    circleTraj.position.x = circleTrajListPos[i].x
//                    circleTraj.position.y = circleTrajListPos[i].y
//                }
            }

        }

        private fun displayRotateCircleTrajectory() {
            if(ball!!.isLaunched){
                for(circleTraj in circleTrajList){

                    circleTraj.position.x = -100f
                    circleTraj.position.y = -100f
                }
            } else if(!ball!!.isLaunched){
                repeat(circleTrajList.size) { i ->
                    // Update the position of each Circle in circleTrajList
                    val circleTraj = circleTrajList[i]
                    circleTraj.position.x = circleTrajListPos[i].x
                    circleTraj.position.y = circleTrajListPos[i].y
                }
            }
        }


    }
}
