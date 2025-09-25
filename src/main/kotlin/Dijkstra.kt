package org.example

/**
 * Dijkstra’s algorithm for shortest paths in directed graphs with non-negative weights.
 *
 * @param V vertex type (must be usable as map/set key; i.e., equals/hashCode OK)
 */
object Dijkstra {

    /**
     * Compute the shortest path from [start] to [dest].
     *
     * @return the path as a list of vertices [start, ..., dest], or null if no path exists.
     *
     * @throws IllegalArgumentException if a negative edge weight is encountered.
     */
    fun <V> shortestPath(graph: Graph<V>, start: V, dest: V): List<V>? {
        val result = shortestPathWithCost(graph, start, dest) ?: return null
        return result.first
    }

    /**
     * Variant that returns both the shortest path and its total cost.
     * Useful for testing and debugging.
     *
     * @return Pair(path, cost) or null if unreachable.
     *
     * @throws IllegalArgumentException if a negative edge weight is encountered.
     */
    fun <V> shortestPathWithCost(graph: Graph<V>, start: V, dest: V): Pair<List<V>, Double>? {
        // Distance map: best-known distance from start
        val dist = HashMap<V, Double>()
        // Predecessor map for path reconstruction
        val prev = HashMap<V, V>()

        // Initialize: unknown vertices have +∞; start has 0
        val pq = PriorityQueue<V>()
        for (v in graph.getVertices()) {
            // We don't have a direct API to add isolated vertices, but vertices that appear
            // as targets or sources will be present. If start/dest were never seen, we still handle it.
            dist[v] = Double.POSITIVE_INFINITY
        }
        // Ensure presence of start (in case it had no edges yet but was given)
        dist.putIfAbsent(start, Double.POSITIVE_INFINITY)
        dist[start] = 0.0
        pq.addWithPriority(start, 0.0)

        // Standard Dijkstra loop
        while (!pq.isEmpty()) {
            val u = pq.next()!!  // element with smallest tentative distance
            if (u == dest) break  // early exit: dest is finalized

            val edges = graph.getEdges(u)
            for ((v, w) in edges) {
                require(w >= 0.0) { "Dijkstra requires non-negative edge weights. Found $w on edge $u -> $v" }

                val du = dist[u] ?: Double.POSITIVE_INFINITY
                val dv = dist.getOrDefault(v, Double.POSITIVE_INFINITY)
                val alt = du + w

                if (alt < dv) {
                    dist[v] = alt
                    prev[v] = u
                    // Either insert or decrease-key (our PQ supports both via add/update semantics)
                    pq.adjustPriority(v, alt)
                }
            }
        }

        val best = dist[dest] ?: Double.POSITIVE_INFINITY
        if (best.isInfinite()) return null

        // Reconstruct path dest <- ... <- start
        val path = ArrayList<V>()
        var cur: V? = dest
        while (cur != null) {
            path.add(cur)
            cur = prev[cur]
        }
        path.reverse()
        return path to best
    }
}