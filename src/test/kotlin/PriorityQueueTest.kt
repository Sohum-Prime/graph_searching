package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PriorityQueueTest {

    @Test
    fun emptyAndBasicAddRemoveOrder() {
        val q = PriorityQueue<String>()
        assertTrue(q.isEmpty())

        q.addWithPriority("A", 5.0)
        q.addWithPriority("B", 2.0)
        q.addWithPriority("C", 3.0)
        assertFalse(q.isEmpty())

        // B (2.0), C (3.0), A (5.0)
        assertEquals("B", q.next())
        assertEquals("C", q.next())
        assertEquals("A", q.next())
        assertNull(q.next())
        assertTrue(q.isEmpty())
    }

    @Test
    fun addUpdatesExistingPriority() {
        val q = PriorityQueue<String>()
        q.addWithPriority("X", 10.0)
        // Updating an existing element lowers its priority:
        q.addWithPriority("X", 1.0)
        q.addWithPriority("Y", 5.0)

        // X should come out first after update
        assertEquals("X", q.next())
        assertEquals("Y", q.next())
        assertNull(q.next())
    }

    @Test
    fun adjustPriorityDecreaseAndIncrease() {
        val q = PriorityQueue<String>()
        q.addWithPriority("A", 5.0)
        q.addWithPriority("B", 2.0)
        q.addWithPriority("C", 7.0)

        // Increase B's priority (make it worse): move behind A
        q.adjustPriority("B", 6.0)

        // Decrease C's priority (make it better): should be first now
        q.adjustPriority("C", 1.0)

        assertEquals("C", q.next()) // 1.0
        assertEquals("A", q.next()) // 5.0
        assertEquals("B", q.next()) // 6.0
        assertNull(q.next())
    }

    @Test
    fun adjustPriorityInsertsIfAbsent() {
        val q = PriorityQueue<String>()
        assertTrue(q.isEmpty())

        // Not present: treat as insert
        q.adjustPriority("ghost", 4.0)
        assertFalse(q.isEmpty())
        assertEquals("ghost", q.next())
        assertTrue(q.isEmpty())
    }

    @Test
    fun rejectsNonFinitePriorities() {
        val q = PriorityQueue<String>()
        assertThrows<IllegalArgumentException> { q.addWithPriority("A", Double.NaN) }
        assertThrows<IllegalArgumentException> { q.addWithPriority("B", Double.POSITIVE_INFINITY) }
        assertThrows<IllegalArgumentException> { q.adjustPriority("C", Double.NEGATIVE_INFINITY) }

        // Ensure queue still functions after failed inserts/updates
        q.addWithPriority("ok", 0.0)
        assertEquals("ok", q.next())
        assertNull(q.next())
    }

    @Test
    fun equalPrioritiesHaveNoStabilityGuaranteeButRemainCorrect() {
        val q = PriorityQueue<String>()
        q.addWithPriority("A", 1.0)
        q.addWithPriority("B", 1.0)
        q.addWithPriority("C", 1.0)

        // All have equal priority; any order of A/B/C is acceptable.
        val first = q.next()
        val second = q.next()
        val third = q.next()

        val set = setOf(first, second, third)
        assertEquals(setOf("A", "B", "C"), set)
        assertNull(q.next())
    }
}