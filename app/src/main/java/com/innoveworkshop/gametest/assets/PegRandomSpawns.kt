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
    private val surface: GameSurface,  // Reference to the Surface
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    // Function to generate a list of pegs (circles)
    fun generatePegs(numberOfPegToGenerate: Int): List<Circle> {
        val generatedCircles = mutableListOf<Circle>()

        // Define Y-axis boundaries
        val minY = screenHeight / 8 + radius  // Starting point of valid Y-range (adjusted for radius)
        val maxY = screenHeight - radius  // Ending point of valid Y-range (adjusted for radius)
        val middleY = screenHeight / 2

        // Track how many pegs are generated in the first and second thirds
        var firstThirdCount = 0
        var secondThirdCount = 0

        // Loop to generate the required number of circles
        repeat(numberOfPegToGenerate) { index ->
            var newCircle: Circle? = null  // Declare newCircle as nullable to handle initialization
            var isValidPosition = false

            // Keep trying until we get a valid position
            while (!isValidPosition) {
                // Randomize the X position within the screen bounds
                val randomX = Random.nextFloat() * (screenWidth - 2 * radius) + radius

                // Randomize the Y position based on the specified thirds
                val randomY: Float
                val isInFirstThird = firstThirdCount < numberOfPegToGenerate / 3
                val isInSecondThird = secondThirdCount < 2 * numberOfPegToGenerate / 3

                if (isInFirstThird) {
                    // Randomly place the peg between surfaceHeight / 8 + radius and surfaceHeight / 2 (first third of the range)
                    randomY = Random.nextFloat() * (middleY - minY - 2 * radius) + minY
                    firstThirdCount++  // Increment the first third counter
                } else if (isInSecondThird) {
                    // Randomly place the peg between surfaceHeight / 2 and surfaceHeight - radius (second third of the range)
                    randomY = Random.nextFloat() * (maxY - middleY - 2 * radius) + middleY
                    secondThirdCount++  // Increment the second third counter
                } else {
                    // If the first two thirds are filled, place in the remaining space (outside the first and second thirds)
                    randomY = Random.nextFloat() * (maxY - minY - 2 * radius) + minY
                }

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

            // Optionally, add the new circle to the surface (for rendering or gameplay)
            surface.addGameObject(newCircle!!)
        }

        // Return the list of generated circles
        return generatedCircles
    }
}
