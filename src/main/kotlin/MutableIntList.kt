package org.example

interface MutableIntList {
    /**
     * Add [element] to the end of the list
     */
    fun add(element: Int)

    /**
     * Remove all elements from the list
     */
    fun clear()

    /*
     * @return the size of the list
     */
    fun size(): Int

    /**
     * @param index the index to return
     * @return the element at [index]
     */
    operator fun get(index: Int): Int

    /**
     * Store [value] at position [index]
     * @param index the index to set
     * @param value to store at [index]
     */
    operator fun set(index: Int, value: Int)
}

