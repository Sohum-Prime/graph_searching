package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DijkstraTest {

    private fun smallGraph(): DirectedWeightedGraph<String> {
        val g = DirectedWeightedGraph<String>()
        // A -> B (1), A -> C (4), B -> C (2), B -> D (6), C -> D (3), A -> E (10), E -> D (1)
        g.addEdge("A", "B", 1.0)
        g.addEdge("A", "C", 4.0)
        g.addEdge("B", "C", 2.0)
        g.addEdge("B", "D", 6.0)
        g.addEdge("C", "D", 3.0)
        g.addEdge("A", "E", 10.0)
        g.addEdge("E", "D", 1.0)
        return g
    }

    @Test
    fun findsShortestPathAndCost() {
        val g = smallGraph()

        val (path, cost) = Dijkstra.shortestPathWithCost(g, "A", "D") ?: error("unreachable")
        // Optimal path A -> B -> C -> D with cost 1 + 2 + 3 = 6
        // Note: A -> E -> D costs 11; A -> C -> D costs 7; A -> B -> D costs 7
        assertEquals(listOf("A", "B", "C", "D"), path)
        assertEquals(6.0, cost)
    }

    @Test
    fun returnsNullIfUnreachable() {
        val g = DirectedWeightedGraph<String>()
        g.addEdge("X", "Y", 1.0)
        // "Z" is isolated relative to X->Y component
        g.addEdge("Z", "Z", 0.0)

        assertNull(Dijkstra.shortestPath(g, "X", "Z"))
        assertNull(Dijkstra.shortestPathWithCost(g, "X", "Z"))
    }

    @Test
    fun startEqualsDestGivesTrivialPath() {
        val g = smallGraph()
        val (path, cost) = Dijkstra.shortestPathWithCost(g, "A", "A") ?: error("unreachable")
        assertEquals(listOf("A"), path)
        assertEquals(0.0, cost)
    }

    @Test
    fun rejectsNegativeEdgeWeights() {
        val g = DirectedWeightedGraph<String>()
        g.addEdge("S", "A", 2.0)
        g.addEdge("A", "T", -5.0)  // illegal for Dijkstra

        assertThrows<IllegalArgumentException> {
            Dijkstra.shortestPath(g, "S", "T")
        }
    }

    @Test
    fun handlesGraphWhereDestIsDiscoveredLate() {
        val g = DirectedWeightedGraph<String>()
        // A “ladder” with a better late route
        g.addEdge("S", "A", 5.0)
        g.addEdge("S", "B", 1.0)
        g.addEdge("B", "C", 1.0)
        g.addEdge("C", "A", 1.0)
        g.addEdge("A", "T", 1.0)

        val (path, cost) = Dijkstra.shortestPathWithCost(g, "S", "T") ?: error("unreachable")
        // Best: S -> B -> C -> A -> T with cost 1 + 1 + 1 + 1 = 4 (beats S->A->T with 6)
        assertEquals(listOf("S", "B", "C", "A", "T"), path)
        assertEquals(4.0, cost)
    }
}