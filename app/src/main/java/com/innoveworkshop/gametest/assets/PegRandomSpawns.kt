package com.innoveworkshop.gametest.assets

import android.view.Surface
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameSurface
import kotlin.math.pow
import kotlin.random.Random

class PegRandomSpawns(
    private val radius: Float,
    private val colorRed: Int,
    private val colorBlue: Int,
    private val valueRed: Int,
    private val valueBlue: Int,
    private val surface: GameSurface,  // Reference to the Surface (not used here yet)
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    // Function to generate a list of pegs (circles)
    fun generatePegs(numberOfPegToGenerate: Int): List<Circle> {
        val generatedCircles = mutableListOf<Circle>()

        // Loop to generate the required number of circles
        repeat(numberOfPegToGenerate) { index ->
            var newCircle: Circle? = null  // Declare newCircle as nullable to handle initialization
            var isValidPosition = false

            // Keep trying until we get a valid position
            while (!isValidPosition) {
                // Randomize the X and Y positions within the screen bounds
                val randomX = Random.nextFloat() * (screenWidth - 2 * radius) + radius
                val randomY = Random.nextFloat() * (screenHeight - 2 * radius) + radius

                // Set color based on the peg count (4 blue for every 1 red)
                val isRedPeg = index % 5 == 4 // Every 5th peg will be red
                val chosenColor = if (isRedPeg) colorRed else colorBlue

                // Set the value (using the same method for all pegs)
                val chosenValue = if (isRedPeg) valueRed else valueBlue

                newCircle = Circle(randomX, randomY, radius, chosenColor, chosenValue)

                // Check if the new position does not overlap with any already generated circles
                isValidPosition = true
                for (circle in generatedCircles) {
                    val distance = Math.sqrt(
                        ((newCircle!!.position.x - circle.position.x).toDouble().pow(2.0) +
                                (newCircle.position.y - circle.position.y).toDouble().pow(2.0))
                    )
                    if (distance < (newCircle.radius + circle.radius)) {
                        isValidPosition = false
                        break
                    }
                }
            }

            // Add the valid circle to the generatedCircles list
            newCircle?.let { generatedCircles.add(it) }
            surface.addGameObject(newCircle!!)
        }

        // Return the list of generated circles
        return generatedCircles
    }
}
