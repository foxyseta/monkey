package monkey.util;

import java.util.function.ToIntFunction;

/**
 * A generic direct address table indexed using [0 .. length - 1] integers as
 * keys. See T. H. Cormen, C. E. Leiserson, R. L. Rivest, C. Stein,
 * <i>Introduction to Algorithms</i>, 3rd ed., Mcgraw-Hill Book Company, p.
 * 254f.
 *
 * @param <T> The type of the values to be stored.
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class DirectAddressTable<T> {

	/**
	 * A function associating each value to an integer key in the range [0 .. length
	 * - 1].
	 */
	final public ToIntFunction<T> KEYFUNCTION;

	/**
	 * Constructs a new {@link DirectAddressTable}.
	 *
	 * @param type        Type token to allocate the underlying
	 *                    {@link java.lang.reflect.Array Array}.
	 * @param keyFunction Initializer for {@link #KEYFUNCTION}.
	 * @param length      Number of possible keys.
	 * @throws IllegalArgumentException length is negative
	 * @throws NullPointerException     type, or keyFunction, or both are null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public DirectAddressTable(Class<T> type, ToIntFunction<T> keyFunction, int length) {
		if (length < 0)
			throw new IllegalArgumentException("length is negative");
		if (type == null)
			throw new NullPointerException("type is null");
		if (keyFunction == null)
			throw new NullPointerException("keyFunction is null");
		table = (T[]) java.lang.reflect.Array.newInstance(type, length);
		KEYFUNCTION = keyFunction;
	}

	/**
	 * The number of possible keys.
	 *
	 * @return Length of this {@link DirectAddressTable}.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int length() {
		return table.length;
	}

	/**
	 * Given a key, returns the value associated to it, or <code>null</code> if such
	 * a value does not exist. If <code>null</code> is returned, there is no way to
	 * tell whether it represents a missing element or an actual <code>null</code>
	 * element previously inserted.
	 *
	 * @param key Key of the value to be returned. It must be in the interval [0 ..
	 *            length - 1].
	 * @throws IndexOutOfBoundsException key is out of bounds
	 * @return The value associated to the key, or <code>null</code> if such a value
	 *         does not exist.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public T search(int key) {
		try {
			return table[key];
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException(key + " is out of direct address table bounds [0;" + table.length + "[");
		}
	}

	/**
	 * Inserts an element in the table, updating it if it was already contained in
	 * it.
	 *
	 * @param element The element to be inserted. It may be <code>null</code>.
	 * @throws IndexOutOfBoundsException The element's key is out of bounds.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void insert(T element) {
		final int key = KEYFUNCTION.applyAsInt(element);
		try {
			table[key] = element;
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException(
					element + "'s key (" + key + ") is out of direct address table bounds [0;" + table.length + "[");
		}
	}

	/**
	 * Deletes an element from the table, if it was contained in it. Otherwise,
	 * calling this method does nothing.
	 *
	 * @param element The element to be deleted. It may be <code>null</code>.
	 * @throws IndexOutOfBoundsException The element's key is out of bounds.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void delete(T element) {
		final int key = KEYFUNCTION.applyAsInt(element);
		try {
			table[key] = null;
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException(
					element + "'s key (" + key + ") is out of direct address table bounds [0;" + table.length + "[");
		}
	}

	private T[] table;

}
