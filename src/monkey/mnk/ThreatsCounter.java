package monkey.mnk;

import monkey.ai.Player;

/**
 * A <code>ThreatsCounter</code> keeps track of the number of {@link Threat}s of
 * a certain type.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class ThreatsCounter {

	/** The kind of {@link Threat} to keep track of. */
	final public Threat THREAT;

	/**
	 * Constructs a new {@link ThreatsCounter}.
	 *
	 * @param threat {@link Threat} to keep track of.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public ThreatsCounter(Threat threat) {
		THREAT = null; // TODO missing implementation
	}

	/**
	 * Increments a given {@link monkey.ai.Player Player}'s counter. Does not
	 * increment anything if such {@link monkey.ai.Player Player} is <code>
	 * null</code>.
	 *
	 * @param player Specifies whose counter is to be incremented.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void increment(Player player) {
		// TODO missing implementation
	}

	/**
	 * Decrements a given {@link monkey.ai.Player Player}'s counter. Does not
	 * increment anything if such {@link monkey.ai.Player Player} is <code>
	 * null</code>.
	 *
	 * @param player Specifies whose counter is to be decremented.
	 * @throws IllegalCallerException The counter is already at 0.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void decrement(Player player) {
		// TODO missing implementation
	}

	/**
	 * Returns the current value of a given {@link monkey.ai.Player Player}'s
	 * counter.
	 *
	 * @param player Specifies whose counter is to be decremented. Cannot be
	 *               <code>null</code>.
	 * @throws IllegalArgumentException player is <code>null</code>.
	 * @return The current value of the desired counter.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public int get(Player player) {
		return 0;// TODO missing implementation
	}

	/** Number of {@link Threat}s by the first {@link monkey.ai.Player Player}. */
	private int p1counter = -1; // TODO missing inizialization
	/** Number of {@link Threat}s by the second {@link monkey.ai.Player Player}. */
	private int p2counter = -1; // TODO missing inizialization

}
