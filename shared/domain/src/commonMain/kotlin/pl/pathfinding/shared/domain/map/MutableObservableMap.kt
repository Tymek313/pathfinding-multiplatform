package pl.pathfinding.shared.domain.map

internal class MutableObservableMap<K, V>(
    private val delegate: MutableMap<K, V>
    // Inherit from Map since this class doesn't implement all MutableMap methods
) : Map<K, V> by delegate {

    var onChange: ((Map<K, V>) -> Unit)? = null

    @JvmName("jvmPutAll")
    fun putAll(from: Map<K, V>) {
        delegate.putAll(from)
        onChange?.invoke(this)
    }

    fun swap(firstKey: K, secondKey: K) {
        val stateOfFirst = delegate.getValue(firstKey)
        delegate[firstKey] = delegate.getValue(secondKey)
        delegate[secondKey] = stateOfFirst
        onChange?.invoke(this)
    }

    operator fun set(key: K, value: V) {
        delegate[key] = value
        onChange?.invoke(this)
    }
}