package com.company

class SolverTwo(val iterations: Int) : Solver {
    override fun getSchedule(problem: Problem): Map<Intersection, IntersectionSchedule> = with(problem) {

        var weights: Map<Intersection, Map<Street, Double>> = intersections.map { intersection ->
            intersection to intersection.inStreets.map { street ->
                street to street.passingCars.size.toDouble()
            }.toMap()
        }.toMap()

        fun getSolution(): Map<Intersection, List<Pair<Street, Int>>?> {
            return weights.map { (intersection, weightsPerStreet) ->
                intersection to with(intersection) {
                    val filteredStreets = weightsPerStreet.filterValues {
                        it >= 0.0000000001
                    }
                    val sumWeights = filteredStreets.values.sum()
                    val intersectionSchedule: IntersectionSchedule? =
                            if (filteredStreets.values.isEmpty() || sumWeights < 0.000001) {
                                null
                            } else if (filteredStreets.size == 1) {
                                listOf(filteredStreets.keys.first() to 1)
                            } else {
                                val maxCycleLength = 2 * filteredStreets.size.toDouble()
                                filteredStreets.map { (street, weight) ->
                                    street to Math.ceil(weight * maxCycleLength / sumWeights).toInt()
                                }
                            }
                    intersectionSchedule
                }
            }.toMap()
        }

        (0 until iterations).forEach { i ->

            val solution = getSolution().mapNotNull { (intersection, schedule) ->
                if (schedule === null) null else (intersection to schedule)
            }.toMap()

            simulate(problem, solution)

            val newWeights = weights.map { (intersection, weightsPerStreet) ->
                val sumWeightsPerStreet = weightsPerStreet.values.sum()
                val normalizedWeightsPerStreet = weightsPerStreet.mapValues { (street, v) -> v / sumWeightsPerStreet }
                val sumCongestion = intersection.congestion.values.sum().toDouble()
                intersection to if (sumCongestion == 0.0) {
                    normalizedWeightsPerStreet
                } else {
                    val normalizedCongestion = intersection.congestion.mapValues { (street, congestion) ->
                        congestion / sumCongestion
                    }

                    val alpha = 0.5 // learning factor
                    intersection.inStreets.map { street ->
                        val newWeight = (
                                (1 - alpha) * (normalizedWeightsPerStreet[street] ?: 0.0)
                                        + alpha * (normalizedCongestion[street] ?: 0.0)
                                )
                        street to newWeight
                    }.toMap()
                }
            }.toMap()
            weights = newWeights

            val score = getScore(problem)
            resetWorld(problem)
            println("Iteration $i done, score $score")
        }

        return getSolution().filter { (k, v) -> v != null }.mapValues { (k, v) -> v!! }
    }
}
