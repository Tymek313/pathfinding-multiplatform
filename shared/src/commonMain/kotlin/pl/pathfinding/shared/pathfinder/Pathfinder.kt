package pl.pathfinding.shared.pathfinder

internal sealed interface Pathfinder {
    /**
     * @return A value indicating if traversing is finished.
     *
     * `true` if the path was found and marked or no path exists.
     * In this case this method should not be called again for the current instance.
     */
    fun advance(): Boolean
}