package com.company

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

fun main() {
    val problemLetter = "b"
    val problem = parseInput("b")

    //val solver = PrimitiveSolver()
    val solver = SolverTwo(4)

    val solution = solver.getSchedule(problem)
    simulate(problem, solution)
    val score = getScore(problem)

    outputSolution(problemLetter, solution)
    println("problem $problemLetter - score $score")
}

fun parseInput(problemLetter: String): Problem {
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

    return Problem(D, I, S, V, F, intersections, streets, cars)
}

fun outputSolution(problemLetter: String, solution: Map<Intersection, IntersectionSchedule>) {
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


data class Street(val beginning: Intersection, val end: Intersection, val name: String, val L: Int) {
    val passingCars = ArrayList<Car>()

    init {
        beginning.outStreets.add(this)
        end.inStreets.add(this)
    }
}

data class Intersection(val id: Int) {
    val inStreets: ArrayList<Street> = ArrayList()
    val outStreets: ArrayList<Street> = ArrayList()

    var schedule: IntersectionSchedule? = null
    var congestion = HashMap<Street, Int>()

    var scheduleIndex = 0
    var scheduleIndexTime = 0

    var waiting: HashMap<Street, LinkedList<Car>> = HashMap()
    fun addWaiter(car: Car, fromStreet: Street) {
        val currQueue = waiting.getOrPut(fromStreet) { LinkedList() }
        if (car !in currQueue) {
            currQueue.addLast(car)
        }
    }

    fun reset() {
        schedule = null
        congestion = HashMap<Street, Int>()
        scheduleIndex = 0
        scheduleIndexTime = 0
        waiting.clear()
    }

    fun step() {
        val schedule = schedule
        if (schedule != null) {
            val currStreet = schedule[scheduleIndex].first
            val currQueue = waiting[currStreet]
            if (currQueue !== null && currQueue.isNotEmpty()) {
                currQueue.removeFirst().moveAfterIntersection()
            }
        }
        waiting.forEach { street, carList ->
            if (street !in congestion) {
                congestion[street] = 0
            }
            congestion.put(street, congestion[street]!! + carList.size)
        }

        if (schedule != null) {
            scheduleIndexTime++
            if (scheduleIndexTime == schedule[scheduleIndex].second) {
                scheduleIndexTime = 0
                scheduleIndex = (scheduleIndex + 1) % schedule.size
            }
        }
    }
}

data class Car(val id: Int, val P: Int, val path: List<Street>) {
    init {
        path.forEach { street ->
            street.passingCars.add(this)
        }
    }

    var pathIndex = 0
    var onThisStreet = path[0].L
    var finishedTime: Int? = null
    var ignore = false
    fun isFinished() = pathIndex == path.size - 1
            && onThisStreet == path[pathIndex].L

    fun step() {
        if (isFinished()) {
            return
        }
        if (onThisStreet < path[pathIndex].L) {
            onThisStreet++
        } else {
            val currentStreet = path[pathIndex]
            currentStreet.end.addWaiter(this, currentStreet)
        }
    }

    fun checkFinish(time: Int) {
        if (isFinished() && finishedTime == null)
        finishedTime = time
    }

    fun moveAfterIntersection() {
        pathIndex++
        onThisStreet = 1
    }

    fun reset() {
        pathIndex = 0
        onThisStreet = path[0].L
        finishedTime = null
        ignore = false
    }
}

interface Solver {
    fun getSchedule(problem: Problem): Solution
}

typealias IntersectionSchedule = List<Pair<Street, Int>>
typealias Solution = Map<Intersection, IntersectionSchedule>

