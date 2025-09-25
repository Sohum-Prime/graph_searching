//package org.example
//
//import kotlin.time.measureTime
//
//class MyMutableIntList : MutableIntList {
//
//    private var elements: IntArray
//    private var size: Int = 0
//
//    /**
//     * Add [element] to the end of the list
//     */
//    override fun add(element: Int) {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Remove all elements from the list
//     */
//    override fun clear() {
//        TODO("Not yet implemented")
//    }
//
//    override fun size(): Int {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * @param index the index to return
//     * @return the element at [index]
//     */
//    override fun get(index: Int): Int {
//        TODO("Not yet implemented")
//    }
//
//    /**
//     * Store [value] at position [index]
//     * @param index the index to set
//     * @param value to store at [index]
//     */
//    override fun set(index: Int, value: Int) {
//        TODO("Not yet implemented")
//    }
//
//}
//
//
//
//fun main() {
//    val arraySizes = listOf(100, 1000, 10000, 100000, 1000000, 10000000, 100000000)
//    println("numberOfElements totalTime timePerElement")
//    for (arraySize in arraySizes) {
//        val myList = MyMutableIntList()
//        val timeTaken = measureTime {
//            for (i in 0..<arraySize) {
//                myList.add(i)
//            }
//        }
//        println("$arraySize $timeTaken ${timeTaken/arraySize}")
//    }
//}