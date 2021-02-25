package com.company

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

val problemLetter = "f"

fun main() {
    val sc = Scanner(File("inputs/${problemLetter}.txt"))

    val D = sc.nextInt()
    val I = sc.nextInt()
    val S = sc.nextInt()
    val V = sc.nextInt()
    val F = sc.nextInt()

    val intersections = Array(I) { Intersection(it) }
    val streets: Map<String, Street> = (0 until S).map { index ->
        val street = Street(intersections[sc.nextInt()], intersections[sc.nextInt()], sc.next(), sc.nextInt())
        street.name to street
    }.toMap()
    val cars = Array(V) {
        val P = sc.nextInt()
        Car(it, P, (0 until P).map { streets[sc.next()]!! })
    }

    val problem = Problem(D, I, S, V, F, intersections, streets, cars)

    // Print output
    val solution = PrimitiveSolver().getSchedule(problem)

    printSolution(solution)
    println("outputted problem ${problemLetter}")
}

fun printSolution(solution: Map<Intersection, IntersectionSchedule>) {
    File("outputs/${problemLetter}.out").printWriter().use { out ->
        out.println("${solution.size}")
        solution.forEach { intersection, schedule ->
            out.println("${intersection.id}")
            out.println("${schedule.size}")
            schedule.forEach { (street, time) ->
                out.println("${street.name} ${time}")
            }

        }
    }
}


class Problem(
        val D: Int, val I: Int, val S: Int, val V: Int, val F: Int,
        val intersections: Array<Intersection>,
        val streets: Map<String, Street>,
        val cars: Array<Car>)


class Street(val beginning: Intersection, val end: Intersection, val name: String, val L: Int) {
    val passingCars = ArrayList<Car>()

    init {
        beginning.outStreets.add(this)
        end.inStreets.add(this)
    }

    var trafficLight = TrafficLight.RED
    fun switchRed() {
        trafficLight = TrafficLight.RED
    }

    fun switchGreen() {
        trafficLight = TrafficLight.GREEN
        for (street in end.inStreets) {
            street.switchRed()
        }
    }
}

class Intersection(val id: Int) {
    val inStreets: ArrayList<Street> = ArrayList()
    val outStreets: ArrayList<Street> = ArrayList()

    lateinit var schedule: IntersectionSchedule
}

class Car(val id: Int, val P: Int, val path: List<Street>) {
    init {
        path.forEach { street ->
            street.passingCars.add(this)
        }
    }
    var untilEndOfRoad = 0
    var roadIndex = 0
    fun isFinished() = roadIndex == path.size
}

enum class TrafficLight {
    RED, GREEN
}

interface Solver {
    fun getSchedule(
            problem: Problem
    ): Solution
}

typealias IntersectionSchedule = List<Pair<Street, Int>>
typealias Solution = Map<Intersection, IntersectionSchedule>
