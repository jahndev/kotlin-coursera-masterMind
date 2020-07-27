package nicestring

fun String.isNice(): Boolean {
    val cond1 = listOf("bu","ba","be").none { contains(it) }
    val cond2 = filter { c -> listOf('a','e','i','o','u').contains(c) }.length > 2
    val cond3 = mapIndexed { i, c -> c to if (i + 1 < this.length)this[i + 1] else ' '}
            .any { p -> p.first == p.second }
    return listOf(cond1, cond2, cond3).filter { it }.size > 1
}