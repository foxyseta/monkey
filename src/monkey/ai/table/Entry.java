package monkey.ai.table;

/**
 * An <code>Entry</code> for two-level transposition tables. It can store one to
 * two {@link SearchResult}s. The two-level replacement scheme used is <code>
 * TWOBIG1</code>. See D.M. Breuker, J.W.H.M. Uiterwijk, H.J. van den Herik,
 * <i>Replacement Schemes for Transposition Tables</i>, in <i>ICCA Journal</i>,
 * 17, 1970, 7.
 *
 * @param <A> The type of the moves of the game.
 * @param <U> The type used to quantify the payoffs.
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class Entry<A, U extends Comparable<U>> {

	/**
	 * Constructs a new {@link Entry} given its first {@link SearchResult}.
	 *
	 * @param searchResult An initial, non-<code>null</code> {@link SearchResult}.
	 * @throws NullPointerException searchResult is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// TODO constructor

	/**
	 * A getter for the first {@link SearchResult}.
	 *
	 * @return The (non-<code>null</code>) first {@link SearchResult}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// TODO getFirst

	/**
	 * A getter for the second {@link SearchResult}.
	 *
	 * @return The second {@link SearchResult}, or <code>null</code> if it does not
	 *         exist.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// TODO getSecond

	/**
	 * Adds a new {@link SearchResult} using the <code>TWOBIG1</code> replacement
	 * scheme.
	 *
	 * @param searchResult The non-<code>null</code> {@link searchResult} to add.
	 * @throws NullPointerException searchResult is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// TODO add

	@Override // inherit doc comment
	// TODO toString

	/** First position of the {@link Entry}. */
	private SearchResult<A, U> first;
	/**
	 * Second position of the {@link Entry}. It is <code>null</code> if it does not
	 * exist.
	 */
	private SearchResult<A, U> second = null;

}
