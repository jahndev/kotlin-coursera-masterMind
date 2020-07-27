package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> = allDrivers.subtract(trips.map { it.driver })

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        allPassengers
                .asSequence()
                .map { it to trips.filter { t -> t.passengers.contains(it)}.size }
                .filter { it.second >= minTrips }
                .sortedBy { p -> p.first.name.removePrefix("P-").toInt()}
                .map { it.first }
                .toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        trips.filter { trip -> trip.driver == driver }
                .flatMap { t -> t.passengers }
                .groupBy { trip -> trip.name }
                .filter { trip -> trip.value.size > 1 }
                .flatMap { it.value }
                .sortedBy { it.name.removePrefix("P-").toInt() }
                .toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val partitionByDiscount = trips
            .partition { it.discount != null && it.discount > 0 }

    val pWithDiscount = partitionByDiscount.first
            .map { it.passengers }
            .flatten()
            .groupBy { it.name }
    val pWithoutDiscount = partitionByDiscount.second
            .map { it.passengers }
            .flatten()
            .groupBy { it.name }

    return pWithDiscount.filter { it.value.size > pWithoutDiscount[it.key]?.size ?: 0 }
            .values
            .flatten()
            .toSet()
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    return trips
            .map { trip -> trip.duration / 10 * 10..trip.duration / 10 * 10 + 9 to trip.duration }
            .groupBy(Pair<IntRange, Int>::first)
            .maxBy { it.value.size }
            ?.key
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    val income = trips
            .sumByDouble { it.cost }
            .toFloat()
    val incomeByDriver = trips
            .map { it.driver to it.cost }
            .groupBy { entry -> entry.first }
            .values.map { trip -> trip.first().first to (trip.sumByDouble { it.second } * 100/income) }
    var maxIndex: Int = (allDrivers.size * 0.2).toInt()
    if(maxIndex > incomeByDriver.size) maxIndex = incomeByDriver.size
    return incomeByDriver
            .sortedByDescending { (_,i) -> i }
            .subList(0, maxIndex)
            .sumByDouble { it.second }
            .compareTo(80) >= 0
}