package org.example

/**
 * Represents a position in the maze as a coordinate pair.
 *
 * @property row the row index (y-coordinate)
 * @property col the column index (x-coordinate)
 */
data class Position(val row: Int, val col: Int) {
    override fun toString(): String = "($row,$col)"
}

/**
 * Result of a maze pathfinding operation.
 *
 * @property path the sequence of positions from start to target (inclusive)
 * @property cost the total cost of the path
 */
data class MazePath(val path: List<Position>, val cost: Double)

/**
 * Solves maze pathfinding problems using Dijkstra's algorithm.
 *
 * The maze is represented as a 2D character array where:
 * - '#' represents walls (impassable)
 * - '.' represents open walkable cells
 * - 'S' represents the start position
 * - 'T' represents the target/goal position
 *
 * The solver supports weighted movement costs, making it suitable for:
 * - Simple unweighted mazes (all moves cost 1)
 * - Terrain with different costs (e.g., mud=5, water=10, grass=1)
 * - Any scenario with non-negative edge weights
 */
object MazeSolver {

    /**
     * The four cardinal directions: up, down, left, right.
     * Each direction is represented as a (deltaRow, deltaCol) pair.
     */
    private val DIRECTIONS = arrayOf(
        Position(-1, 0),  // Up
        Position(1, 0),   // Down
        Position(0, -1),  // Left
        Position(0, 1)    // Right
    )

    /**
     * Finds the shortest path through a maze from 'S' to 'T' using Dijkstra's algorithm.
     *
     * @param maze 2D character array representing the maze
     * @param moveCost function that returns the cost to move into a cell (default: 1.0 for all cells)
     * @return MazePath containing the path and its cost, or null if no path exists
     * @throws IllegalArgumentException if start 'S' or target 'T' is not found in the maze
     */
    fun solveMaze(
        maze: Array<CharArray>,
        moveCost: (Position, Char) -> Double = { _, _ -> 1.0 }
    ): MazePath? {
        if (maze.isEmpty() || maze[0].isEmpty()) {
            throw IllegalArgumentException("Maze cannot be empty")
        }

        val rows = maze.size
        val cols = maze[0].size

        // Find start and target positions
        val start = findPosition(maze, 'S')
            ?: throw IllegalArgumentException("Start position 'S' not found in maze")
        val target = findPosition(maze, 'T')
            ?: throw IllegalArgumentException("Target position 'T' not found in maze")

        // Distance map: best-known distance to reach each cell
        val dist = Array(rows) { DoubleArray(cols) { Double.POSITIVE_INFINITY } }
        dist[start.row][start.col] = 0.0

        // Parent map: tracks which cell we came from to reconstruct the path
        val parent = Array(rows) { arrayOfNulls<Position>(cols) }

        // Priority queue: cells to explore, ordered by distance
        val pq = PriorityQueue<Position>()
        pq.addWithPriority(start, 0.0)

        // Set to track finalized cells
        val finalized = HashSet<Position>()

        // Main Dijkstra loop
        while (!pq.isEmpty()) {
            val current = pq.next() ?: break

            // Skip if already finalized (can happen with duplicate entries in PQ)
            if (current in finalized) continue
            finalized.add(current)

            // Early exit: we've found the shortest path to target
            if (current == target) break

            // Explore all four neighbors
            for (direction in DIRECTIONS) {
                val neighbor = Position(
                    row = current.row + direction.row,
                    col = current.col + direction.col
                )

                // Check bounds
                if (neighbor.row !in 0 until rows || neighbor.col !in 0 until cols) {
                    continue
                }

                val cellChar = maze[neighbor.row][neighbor.col]

                // Skip walls
                if (cellChar == '#') continue

                // Skip already finalized cells
                if (neighbor in finalized) continue

                // Calculate cost to reach this neighbor
                val edgeCost = moveCost(neighbor, cellChar)
                require(edgeCost >= 0.0) {
                    "Move cost must be non-negative, got $edgeCost for cell $neighbor"
                }

                val newDist = dist[current.row][current.col] + edgeCost

                // If we found a better path to this neighbor, update it
                if (newDist < dist[neighbor.row][neighbor.col]) {
                    dist[neighbor.row][neighbor.col] = newDist
                    parent[neighbor.row][neighbor.col] = current
                    pq.adjustPriority(neighbor, newDist)
                }
            }
        }

        // Check if target is reachable
        if (dist[target.row][target.col].isInfinite()) {
            return null
        }

        // Reconstruct path by backtracking from target to start
        val path = reconstructPath(parent, start, target)
        val totalCost = dist[target.row][target.col]

        return MazePath(path, totalCost)
    }

    /**
     * Solves a maze represented as a list of strings.
     * This is a convenience method for easier testing and usage.
     *
     * @param mazeLines list of strings, each representing a row of the maze
     * @param moveCost function that returns the cost to move into a cell
     * @return MazePath or null if no path exists
     */
    fun solveMaze(
        mazeLines: List<String>,
        moveCost: (Position, Char) -> Double = { _, _ -> 1.0 }
    ): MazePath? {
        val maze = mazeLines.map { it.toCharArray() }.toTypedArray()
        return solveMaze(maze, moveCost)
    }

    /**
     * Finds the first occurrence of a character in the maze.
     *
     * @param maze the maze to search
     * @param target the character to find
     * @return the position of the character, or null if not found
     */
    private fun findPosition(maze: Array<CharArray>, target: Char): Position? {
        for (row in maze.indices) {
            for (col in maze[row].indices) {
                if (maze[row][col] == target) {
                    return Position(row, col)
                }
            }
        }
        return null
    }

    /**
     * Reconstructs the path from start to target by following parent pointers.
     *
     * @param parent 2D array where parent[i][j] is the position we came from to reach (i,j)
     * @param start the starting position
     * @param target the target position
     * @return list of positions from start to target (inclusive)
     */
    private fun reconstructPath(
        parent: Array<Array<Position?>>,
        start: Position,
        target: Position
    ): List<Position> {
        val path = ArrayList<Position>()
        var current: Position? = target

        // Backtrack from target to start
        while (current != null) {
            path.add(current)
            if (current == start) break
            current = parent[current.row][current.col]
        }

        // Reverse to get path from start to target
        path.reverse()
        return path
    }

    /**
     * Visualizes the maze with the solution path marked.
     *
     * @param maze the original maze
     * @param path the solution path
     * @return string representation with path marked by '*'
     */
    fun visualizeSolution(maze: Array<CharArray>, path: List<Position>): String {
        // Create a copy of the maze
        val visual = Array(maze.size) { row -> maze[row].copyOf() }

        // Mark the path (except start and target)
        for (pos in path) {
            if (visual[pos.row][pos.col] == '.') {
                visual[pos.row][pos.col] = '*'
            }
        }

        return visual.joinToString("\n") { it.joinToString("") }
    }

    /**
     * Visualizes the maze with the solution path marked.
     * Convenience overload that accepts List<String> for the maze.
     */
    fun visualizeSolution(mazeLines: List<String>, path: List<Position>): String {
        val maze = mazeLines.map { it.toCharArray() }.toTypedArray()
        return visualizeSolution(maze, path)
    }
}