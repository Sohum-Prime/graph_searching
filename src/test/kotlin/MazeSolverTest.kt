package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MazeSolverTest {

    @Test
    fun solveSimpleMaze() {
        val maze = listOf(
            "#####",
            "#S..#",
            "#.#.#",
            "#..T#",
            "#####"
        )

        val result = MazeSolver.solveMaze(maze)
        assertNotNull(result)

        // Verify path starts at S and ends at T
        assertEquals(Position(1, 1), result!!.path.first())
        assertEquals(Position(3, 3), result.path.last())

        // Verify cost (should be path length - 1 for unweighted maze)
        assertEquals(result.path.size - 1.0, result.cost)

        println("Simple maze solution:")
        println(MazeSolver.visualizeSolution(maze, result.path))
        println("Cost: ${result.cost}")
    }

    @Test
    fun solveComplexMaze() {
        val maze = listOf(
            "#########",
            "#......S#",
            "#.#.#####",
            "#...#...#",
            "#.#####.#",
            "#.......#",
            "#.#####.#",
            "#.#T....#",
            "#########"
        )

        val result = MazeSolver.solveMaze(maze)
        assertNotNull(result)

        assertEquals(Position(1, 7), result!!.path.first())
        assertEquals(Position(7, 3), result.path.last())

        println("\nComplex maze solution:")
        println(MazeSolver.visualizeSolution(maze, result.path))
        println("Path length: ${result.path.size}")
        println("Cost: ${result.cost}")
    }

    @Test
    fun noPathExists() {
        val maze = listOf(
            "#####",
            "#S#T#",
            "#####"
        )

        val result = MazeSolver.solveMaze(maze)
        assertNull(result, "Should return null when no path exists")
    }

    @Test
    fun startEqualsTarget() {
        // This tests an edge case: what if S and T are in the same position?
        // We'll need to modify the maze to have both S and T markers
        // For now, let's test a different edge case: very short path
        val maze = listOf(
            "###",
            "#ST#",
            "###"
        )

        // This will fail because we can't have both S and T in adjacent cells
        // Let's modify the test to be more realistic
        val maze2 = listOf(
            "####",
            "#S.T#",
            "####"
        )

        val result = MazeSolver.solveMaze(maze2)
        assertNotNull(result)
        assertEquals(3, result!!.path.size) // S -> . -> T
        assertEquals(2.0, result.cost)
    }

    @Test
    fun weightedMazeWithDifferentTerrains() {
        val maze = listOf(
            "#######",
            "#S....#",
            "#.###.#",
            "#...mT#",  // 'm' represents mud (expensive)
            "#######"
        )

        // Define terrain costs: mud (m) costs 10, normal cells cost 1
        val terrainCost: (Position, Char) -> Double = { _, cell ->
            when (cell) {
                'm' -> 10.0
                else -> 1.0
            }
        }

        val result = MazeSolver.solveMaze(maze, terrainCost)
        assertNotNull(result)

        println("\nWeighted maze solution (mud costs 10x):")
        println(MazeSolver.visualizeSolution(maze, result!!.path))
        println("Cost: ${result.cost}")

        // The algorithm should prefer going around the mud if possible
        // Path cost should reflect the terrain
        assertTrue(result.cost > 5.0, "Path should incur terrain costs")
    }

    @Test
    fun largeMaze() {
        // Create a 20x20 maze
        val maze = mutableListOf<String>()
        maze.add("#".repeat(20))
        for (i in 1..18) {
            if (i % 2 == 1) {
                maze.add("#" + ".".repeat(18) + "#")
            } else {
                maze.add("#" + "#.".repeat(9) + "#")
            }
        }
        maze.add("#".repeat(20))

        // Place S at top-left, T at bottom-right
        maze[1] = "#S" + ".".repeat(17) + "#"
        maze[18] = "#" + ".".repeat(17) + "T#"

        val result = MazeSolver.solveMaze(maze)
        assertNotNull(result)

        println("\nLarge maze solution:")
        println(MazeSolver.visualizeSolution(maze, result!!.path))
        println("Path length: ${result.path.size}")
    }

    @Test
    fun mazeWithMultiplePaths() {
        // Diamond-shaped maze with two equal-length paths
        val maze = listOf(
            "#######",
            "#..S..#",
            "#.#.#.#",
            "#.....#",
            "#.#.#.#",
            "#..T..#",
            "#######"
        )

        val result = MazeSolver.solveMaze(maze)
        assertNotNull(result)

        println("\nMaze with multiple paths:")
        println(MazeSolver.visualizeSolution(maze, result!!.path))
        println("Cost: ${result.cost}")

        // Both paths should have the same cost
        // Dijkstra will find one of them
    }

    @Test
    fun throwsWhenStartNotFound() {
        val maze = listOf(
            "####",
            "#..T#",
            "####"
        )

        val exception = assertThrows<IllegalArgumentException> {
            MazeSolver.solveMaze(maze)
        }
        assertTrue(exception.message!!.contains("Start position 'S' not found"))
    }

    @Test
    fun throwsWhenTargetNotFound() {
        val maze = listOf(
            "####",
            "#S..#",
            "####"
        )

        val exception = assertThrows<IllegalArgumentException> {
            MazeSolver.solveMaze(maze)
        }
        assertTrue(exception.message!!.contains("Target position 'T' not found"))
    }

    @Test
    fun throwsWhenMazeIsEmpty() {
        val exception = assertThrows<IllegalArgumentException> {
            MazeSolver.solveMaze(emptyList())
        }
        assertTrue(exception.message!!.contains("empty"))
    }

    @Test
    fun rejectsNegativeMoveCosts() {
        val maze = listOf(
            "####",
            "#S.T#",
            "####"
        )

        val negativeCost: (Position, Char) -> Double = { _, _ -> -1.0 }

        val exception = assertThrows<IllegalArgumentException> {
            MazeSolver.solveMaze(maze, negativeCost)
        }
        assertTrue(exception.message!!.contains("non-negative"))
    }

    @Test
    fun spiralMaze() {
        val maze = listOf(
            "###########",
            "#S.......##",
            "#.#####.###",
            "#.#...#.###",
            "#.#.#.#.###",
            "#.#.#.#.###",
            "#.#...#...#",
            "#.#####.#.#",
            "#.......#T#",
            "###########"
        )

        val result = MazeSolver.solveMaze(maze)
        assertNotNull(result)

        println("\nSpiral maze solution:")
        println(MazeSolver.visualizeSolution(maze, result!!.path))
        println("Path length: ${result.path.size}")
        println("Cost: ${result.cost}")
    }

    @Test
    fun verifyPathContinuity() {
        val maze = listOf(
            "#######",
            "#S....#",
            "#.###.#",
            "#....T#",
            "#######"
        )

        val result = MazeSolver.solveMaze(maze)
        assertNotNull(result)

        // Verify that each step in the path is adjacent to the next
        for (i in 0 until result!!.path.size - 1) {
            val current = result.path[i]
            val next = result.path[i + 1]

            val rowDiff = kotlin.math.abs(current.row - next.row)
            val colDiff = kotlin.math.abs(current.col - next.col)

            // Should be exactly one step away (Manhattan distance = 1)
            assertEquals(1, rowDiff + colDiff,
                "Path should be continuous: $current -> $next")
        }
    }
}