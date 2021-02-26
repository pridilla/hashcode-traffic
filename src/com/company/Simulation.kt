package com.company

fun simulate(problem: Problem, solution: Solution) = with(problem) {

    solution.forEach { intersection, schedule ->
        intersection.schedule = schedule
    }
    var time = 0
    while (time < D) {

        cars.forEach { it.step() }
        intersections.forEach { it.step() }
        cars.forEach { it.checkFinish(time) }

        time++
    }

}

fun resetWorld(problem: Problem) = with(problem) {
    intersections.forEach { it.reset() }
    cars.forEach { it.reset() }
}

fun getScore(problem: Problem): Int {
    return problem.cars.sumBy { car ->
        val finishedTime = car.finishedTime
        if (finishedTime == null)
            0
        else
            problem.F + problem.D - finishedTime
    }
}
