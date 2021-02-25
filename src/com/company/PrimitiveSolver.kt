package com.company

class PrimitiveSolver : Solver {
    override fun getSchedule(problem: Problem): Map<Intersection, IntersectionSchedule> = with(problem) {

        fun Intersection.getSchedule(): List<Pair<Street, Int>>? {
            val sumPassingCars = inStreets.sumBy { street -> street.passingCars.size }
            if (sumPassingCars == 0) return null
            if (inStreets.size == 1) return listOf(inStreets[0] to 1)
            val relevantStreet = inStreets.filter { it.passingCars.size > 0 }
            if (relevantStreet.size == 0) return null
            return if (sumPassingCars <= 2 * inStreets.size) {
                relevantStreet.map { street ->
                    street to street.passingCars.size
                }
            } else {
                val k = 2 * inStreets.size.toDouble() / sumPassingCars
                relevantStreet.map { street ->
                    street to Math.ceil(street.passingCars.size * k).toInt()
                }
            }
        }

        return intersections.mapNotNull {
            val schedule = it.getSchedule()
            if (schedule === null) null else (it to schedule)
        }.toMap()
    }
}
