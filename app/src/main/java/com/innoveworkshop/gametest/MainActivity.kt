package com.innoveworkshop.gametest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DroppingCircle
import com.innoveworkshop.gametest.assets.DroppingRectangle
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class MainActivity : AppCompatActivity() {
    protected var gameSurface: GameSurface? = null
    protected var upButton: Button? = null
    protected var downButton: Button? = null
    protected var leftButton: Button? = null
    protected var rightButton: Button? = null

    protected var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)

        setupControls()
    }

    private fun setupControls() {
        var verticalSpeed = 40f;
        var horizontalSpeed = 40f;
        //upButton = findViewById<View>(R.id.up_button) as Button
        //upButton!!.setOnClickListener { game!!.circle!!.position.y -= 10f }

        //downButton = findViewById<View>(R.id.down_button) as Button
        //downButton!!.setOnClickListener { game!!.circle!!.position.y += 10f }


//        leftButton = findViewById<View>(R.id.left_button) as Button
//        leftButton!!.setOnClickListener { game!!.platfrom!!.position.x -= horizontalSpeed }
//
//        rightButton = findViewById<View>(R.id.right_button) as Button
//        rightButton!!.setOnClickListener { game!!.platfrom!!.position.x += horizontalSpeed }
    }

    inner class Game : GameObject() {
        val circleList = mutableListOf<Circle>()
        var circle: Circle? = null
        var circle1: Circle? = null
        var platfrom: Rectangle? = null
        var ball: DroppingCircle? = null

        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            //peggle all
            ball = DroppingCircle(
                Vector((surface!!.width / 2).toFloat(), (surface.height / 8).toFloat()),
                (surface.width / 2).toFloat(),
                (surface.height / 6).toFloat(),
                30f,
                10f,
                10f,
                Color.rgb(128, 140, 80)
            )
            surface.addGameObject(ball!!)


            circle = Circle(
                (surface.width / 2).toFloat() + 50f,
                (surface.height / 2).toFloat(),
                40f,
                Color.RED
            )
            circleList.add(circle!!);
            surface.addGameObject(circle!!)

            circle1 = Circle(
                (surface.width / 2).toFloat() - 30f,
                (surface.height / 2).toFloat() - 150f,
                40f,
                Color.BLUE
            )
            circleList.add(circle1!!);
            surface.addGameObject(circle1!!)

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



        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()

            if (!circle!!.isFloored && !circle!!.hitRightWall() && !circle!!.isDestroyed) {
                //moves the circle towards bottom left as long as it doesn't touch the ground and the right wall
                //circle!!.setPosition(circle!!.position.x + 1, circle!!.position.y + 1)
            } else {
                circle!!.destroy()
            }

            //custom Collision for pegs
            for (checkCircle in circleList) {
                if (ball!!.CollideWithCircle(checkCircle)) {
                    checkCircle.destroy()
                    // Optionally change ball color or handle further logic
                }
            }

            if (ball!!.hitRightWall())
                ball!!.mirrorXVelocity()

            if (ball!!.hitLeftWall())
                ball!!.mirrorXVelocity()

            if (ball!!.isFloored)
                ball!!.mirrorYVelocity()


            if (ball!!.hitCeiling())
                ball!!.mirrorYVelocity()
        }
    }
}