package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Circle
import kotlin.math.pow
import kotlin.random.Random

class PegRandomSpawns(
    private val radius: Float,
    val colorRed: Int,
    val colorBlue: Int,
    private val valueRed: Int,
    private val valueBlue: Int,
    existingCircles: List<Circle>,  // Input list of existing circles
    private val screenWidth: Float,
    private val screenHeight: Float
) {
    // Use the provided existingCircles directly (no redeclaration)
    private val existingCircles = existingCircles.toMutableList()

    // Function to generate a list of pegs (circles)
    fun generatePegs(numberOfPegToGenerate: Int): List<Circle> {
        val generatedCircles = mutableListOf<Circle>()

        // Loop to generate the required number of circles
        repeat(numberOfPegToGenerate) { index ->
            var newCircle: Circle? = null // Declare newCircle as nullable to handle initialization
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

                // Check if the new position does not overlap with any existing circles
                isValidPosition = true
                for (circle in existingCircles) {
                    val distance = Math.sqrt(
                        ((newCircle!!.position.x - circle.position.x).toDouble().pow(2.0) + (newCircle.position.y - circle.position.y).toDouble().pow(2.0))
                    )
                    if (distance < (newCircle.radius + circle.radius)) {
                        isValidPosition = false
                        break
                    }
                }
            }

            // Add the valid circle to the list of existing circles
            newCircle?.let { existingCircles.add(it) } // Safely add the newCircle if not null

            // Add the new circle to the generatedCircles list
            newCircle?.let { generatedCircles.add(it) }
        }

        // Return the list of generated circles
        return generatedCircles
    }

    // Optionally, you can access existing circles
    fun getExistingCircles(): List<Circle> {
        return existingCircles
    }
}
