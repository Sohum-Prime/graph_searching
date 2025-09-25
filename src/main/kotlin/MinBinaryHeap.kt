package org.example

/**
 * A binary min-heap with O(log n) insertion, delete-min, and adjust-priority.
 *
 * Stores (elem, priority) pairs and maintains an index map for decrease/increase-key.
 * Only finite priorities are allowed (no NaN or ±∞).
 *
 * @param T element type; must have meaningful equals()/hashCode() for index tracking.
 */
internal class MinBinaryHeap<T> {

    internal data class Node<T>(val elem: T, var priority: Double)

    private val heap = ArrayList<Node<T>>()                 // binary heap array
    private val indexOf = HashMap<T, Int>()                 // elem -> index in heap

    fun isEmpty(): Boolean = heap.isEmpty()
    fun size(): Int = heap.size

    fun contains(elem: T): Boolean = indexOf.containsKey(elem)

    fun peek(): Node<T>? = heap.firstOrNull()

    /**
     * Insert elem with priority. Requires elem not already present.
     * Use addOrUpdate() or call contains() first if you want overwrite semantics.
     */
    fun push(elem: T, priority: Double) {
        require(priority.isFinite()) { "Priority must be finite. Got $priority" }
        require(!indexOf.containsKey(elem)) { "Element already present: $elem" }
        val node = Node(elem, priority)
        heap.add(node)
        val i = heap.lastIndex
        indexOf[elem] = i
        siftUp(i)
    }

    /**
     * Insert or update the element's priority.
     * @return true if inserted, false if updated.
     */
    fun addOrUpdate(elem: T, priority: Double): Boolean {
        require(priority.isFinite()) { "Priority must be finite. Got $priority" }
        val idx = indexOf[elem]
        return if (idx == null) {
            push(elem, priority)
            true
        } else {
            updateAt(idx, priority)
            false
        }
    }

    /**
     * Decrease or increase the priority for an existing element.
     * @return true if updated, false if not present.
     */
    fun updatePriority(elem: T, newPriority: Double): Boolean {
        require(newPriority.isFinite()) { "Priority must be finite. Got $newPriority" }
        val idx = indexOf[elem] ?: return false
        updateAt(idx, newPriority)
        return true
    }

    /**
     * Remove and return the node with the minimum priority, or null if empty.
     */
    fun pop(): Node<T>? {
        if (heap.isEmpty()) return null
        val min = heap[0]
        val last = heap.removeAt(heap.lastIndex)
        indexOf.remove(min.elem)

        if (heap.isNotEmpty()) {
            heap[0] = last
            indexOf[last.elem] = 0
            siftDown(0)
        }
        return min
    }

    // --- internal heap mechanics ---

    private fun updateAt(i: Int, newPriority: Double) {
        val old = heap[i].priority
        heap[i].priority = newPriority
        // Decide which direction to fix heap order.
        if (newPriority < old) siftUp(i) else siftDown(i)
    }

    private fun siftUp(i0: Int) {
        var i = i0
        while (i > 0) {
            val p = parent(i)
            if (heap[i].priority < heap[p].priority) {
                swap(i, p)
                i = p
            } else break
        }
    }

    private fun siftDown(i0: Int) {
        var i = i0
        while (true) {
            val l = left(i)
            val r = right(i)
            var smallest = i

            if (l < heap.size && heap[l].priority < heap[smallest].priority) smallest = l
            if (r < heap.size && heap[r].priority < heap[smallest].priority) smallest = r

            if (smallest == i) break
            swap(i, smallest)
            i = smallest
        }
    }

    private fun swap(i: Int, j: Int) {
        if (i == j) return
        val ni = heap[i]
        val nj = heap[j]
        heap[i] = nj
        heap[j] = ni
        indexOf[nj.elem] = i
        indexOf[ni.elem] = j
    }

    private fun parent(i: Int): Int = (i - 1) / 2
    private fun left(i: Int): Int = 2 * i + 1
    private fun right(i: Int): Int = 2 * i + 2
}