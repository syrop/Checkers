package pl.org.seva.checkers.game.view

data class Position(
        val position: ArrayList<Pair<Int, Int>>,
) {
    override fun toString() = buildString {
        for (p in position) {
            append(p.first)
            append(p.second)
        }
    }
}
