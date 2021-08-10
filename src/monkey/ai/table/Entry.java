package monkey.ai.table;

/**
 * An <code>Entry</code> for two-level transposition tables. It can store one to
 * two {@link SearchResult}s. The two-level replacement scheme used is <code>
 * TWOBIG1</code>. See D.M. Breuker, J.W.H.M. Uiterwijk, H.J. van den Herik,
 * <i>Replacement Schemes for Transposition Tables</i>, in <i>ICCA Journal</i>,
 * 17, 1970, 7.
 *
 * @param <S> The type to be used for game {@link monkey.ai.State}s.
 * @param <A> The type of the moves of the game.
 * @param <U> The type used to quantify the payoffs.
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class Entry<S extends monkey.ai.State<S, A, U>, A, U extends Comparable<U>> {

	/**
	 * Constructs a new {@link Entry} given its first {@link SearchResult}.
	 *
	 * @param searchResult An initial, non-<code>null</code> {@link SearchResult}.
	 * @throws NullPointerException searchResult is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Entry(SearchResult<A, U> searchResult) {
		if (searchResult == null)
			throw new NullPointerException("searchResult can't be null.");
		first = searchResult;
	}

	/**
	 * A getter for the first {@link SearchResult}.
	 *
	 * @see #getSecond
	 * @return The (non-<code>null</code>) first {@link SearchResult}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public SearchResult<A, U> getFirst() {
		return first;
	}

	/**
	 * A getter for the second {@link SearchResult}.
	 *
	 * @see #getFirst
	 * @return The second {@link SearchResult}, or <code>null</code> if it does not
	 *         exist.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public SearchResult<A, U> getSecond() {
		return second;
	}

	/**
	 * Decides which {@link SearchResult} should be used.
	 *
	 * @param state The current state of the game.
	 * @param depth The maximum depth to be inspected.
	 * @return The selected {@link SearchResult}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public SearchResult<A, U> pickSearchResult(S state, int depth) {
		final boolean isFirstLegal = state.isLegal(first.MOVE),
				isSecondLegal = second == null ? false : state.isLegal(second.MOVE);
		if (!isFirstLegal)
			return isSecondLegal ? second : null;
		if (!isSecondLegal)
			return isFirstLegal ? first : null;
		if (depth <= first.SEARCHDEPTH && first.FLAG == SearchResult.ScoreType.TRUEVALUE)
			return first;
		if (depth <= second.SEARCHDEPTH && second.FLAG == SearchResult.ScoreType.TRUEVALUE)
			return second;
		if (depth <= first.SEARCHDEPTH)
			return first;
		if (depth <= second.SEARCHDEPTH)
			return second;
		return first;
	}

	/**
	 * Adds a new {@link SearchResult} using the <code>TWOBIG1</code> replacement
	 * scheme.
	 *
	 * @param searchResult The non-<code>null</code> {@link SearchResult} to add.
	 * @throws NullPointerException searchResult is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void add(SearchResult<A, U> searchResult) {
		if (searchResult == null)
			throw new NullPointerException("searchResult can't be null.");
		if (searchResult.compareTo(first) >= 0) {
			second = first;
			first = searchResult;
		} else
			second = searchResult;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return A string representation of the object.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return first + " " + second;
	}

	/** First position of the {@link Entry}. */
	private SearchResult<A, U> first;
	/**
	 * Second position of the {@link Entry}. It is <code>null</code> if it does not
	 * exist.
	 */
	private SearchResult<A, U> second = null;

}
