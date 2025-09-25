package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFailsWith

class DirectedWeightedGraphTest {

    @Test
    fun addAndRetrieveEdgesAndVertices() {
        val g = DirectedWeightedGraph<String>()
        g.addEdge("A", "B", 1.5)
        g.addEdge("A", "C", 2.0)
        g.addEdge("B", "C", 3.25)

        // Vertices should include all seen endpoints
        val vs = g.getVertices()
        assertEquals(setOf("A", "B", "C"), vs)

        // Outgoing from A
        val aOut = g.getEdges("A")
        assertEquals(2, aOut.size)
        assertEquals(1.5, aOut["B"])
        assertEquals(2.0, aOut["C"])

        // Outgoing from B
        val bOut = g.getEdges("B")
        assertEquals(1, bOut.size)
        assertEquals(3.25, bOut["C"])

        // Vertex with in-degree only (C) has no outgoing edges
        val cOut = g.getEdges("C")
        assertTrue(cOut.isEmpty())
    }

    @Test
    fun overwriteEdgeWeight() {
        val g = DirectedWeightedGraph<Int>()
        g.addEdge(1, 2, 10.0)
        g.addEdge(1, 2, 42.0) // overwrite

        val out = g.getEdges(1)
        assertEquals(1, out.size)
        assertEquals(42.0, out[2])
    }

    @Test
    fun negativeAndSelfLoopAllowed() {
        val g = DirectedWeightedGraph<String>()
        g.addEdge("X", "X", -7.0) // self-loop with negative weight

        val vs = g.getVertices()
        assertEquals(setOf("X"), vs)

        val xOut = g.getEdges("X")
        assertEquals(1, xOut.size)
        assertEquals(-7.0, xOut["X"])
    }

    @Test
    fun rejectsNaNAndInfinity() {
        val g = DirectedWeightedGraph<String>()
        assertThrows<IllegalArgumentException> { g.addEdge("A", "B", Double.NaN) }
        assertThrows<IllegalArgumentException> { g.addEdge("A", "B", Double.POSITIVE_INFINITY) }
        assertThrows<IllegalArgumentException> { g.addEdge("A", "B", Double.NEGATIVE_INFINITY) }
        assertTrue(g.getVertices().isEmpty())
    }

    @Test
    fun defensiveCopiesFromGetters() {
        val g = DirectedWeightedGraph<String>()
        g.addEdge("A", "B", 1.0)

        // Attempt to mutate the returned collections directly.
        // If the returned collections are unmodifiable, these lines may throw;
        // if they are simple copies, the mutations will succeed but must NOT
        // affect the graph's internal state. Either way, we only care that
        // the graph remains unchanged.
        val vs = g.getVertices()
        runCatching { (vs as MutableSet<String>).add("C") }  // may succeed or throw; both are fine

        val edges = g.getEdges("A")
        runCatching { (edges as MutableMap<String, Double>)["C"] = 2.0 } // may succeed or throw

        // The graph must remain unchanged regardless of what happened above.
        assertEquals(setOf("A", "B"), g.getVertices())
        assertEquals(mapOf("B" to 1.0), g.getEdges("A"))

        // Extra sanity: mutating *separate* copies obviously cannot affect the graph.
        val vsCopy = g.getVertices().toMutableSet()
        vsCopy.add("Z")
        val edgesCopy = g.getEdges("A").toMutableMap()
        edgesCopy["Q"] = 99.0

        assertEquals(setOf("A", "B"), g.getVertices())
        assertEquals(mapOf("B" to 1.0), g.getEdges("A"))
    }

    @Test
    fun clearEmptiesGraph() {
        val g = DirectedWeightedGraph<String>()
        g.addEdge("A", "B", 1.0)
        g.addEdge("B", "C", 2.0)

        g.clear()

        assertTrue(g.getVertices().isEmpty())
        assertTrue(g.getEdges("A").isEmpty())
        assertTrue(g.getEdges("B").isEmpty())
        assertTrue(g.getEdges("C").isEmpty())
    }
}