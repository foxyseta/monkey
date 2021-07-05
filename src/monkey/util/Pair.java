package monkey.util;

/**
 * <code>Pair</code> is a convenience object used to represent key-value
 * relationships.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class Pair<K, V> {

	/** Key of the {@link Pair}. */
	protected K key;
	/** Value of the {@link Pair}. */
	protected V value;

	/**
	 * Creates a new {@link Pair}.
	 *
	 * @param k The key for this {@link Pair}.
	 * @param v The value for this {@link Pair}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Pair(K k, V v) {
		key = k;
		value = v;
	}

	/**
	 * Gets the key for this {@link Pair}.
	 *
	 * @return The key for this {@link Pair}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Gets the value for this {@link Pair}.
	 *
	 * @return The value for this {@link Pair}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public V getValue() {
		return value;
	}

}
