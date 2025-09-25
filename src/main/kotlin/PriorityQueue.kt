package org.example

/**
 * A thin, user-facing min-priority queue backed by [MinBinaryHeap].
 *
 * Semantics:
 * - addWithPriority(e, p): insert if new; otherwise update existing priority (last-write-wins).
 * - next(): remove and return the element with the lowest priority; null if empty.
 * - adjustPriority(e, p): if present, update priority; if absent, inserts with the given priority.
 *
 * Constraints:
 * - Priorities must be finite Double values (no NaN/±∞). Negative values are allowed.
 *
 * Complexity (amortized):
 * - addWithPriority: O(log n)
 * - next: O(log n)
 * - adjustPriority: O(log n)
 * - isEmpty: O(1)
 */
class PriorityQueue<T> : MinPriorityQueue<T> {

    private val heap = MinBinaryHeap<T>()

    override fun isEmpty(): Boolean = heap.isEmpty()

    override fun addWithPriority(elem: T, priority: Double) {
        heap.addOrUpdate(elem, priority) // insert or update in O(log n)
    }

    override fun next(): T? = heap.pop()?.elem

    override fun adjustPriority(elem: T, newPriority: Double) {
        val updated = heap.updatePriority(elem, newPriority)
        if (!updated) {
            // Absent: insert with that priority (friendly semantics for graph algorithms).
            heap.addOrUpdate(elem, newPriority)
        }
    }
}