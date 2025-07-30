package pl.pathfinding.shared.domain.pathfinder

sealed interface Pathfinder {
    /**
     * @return Whether graph traversing is finished (regardless of finding a path or not).
     */
    fun advance(): Boolean
}
