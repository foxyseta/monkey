package monkey.mnk;

import monkey.ai.Player;

/**
 * A <code>ThreatsCounter</code> keeps track of the number of {@link Threat}s of
 * a certain type.
 *
 * @author Gaia Clerici
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
		THREAT = threat;
	}

	/**
	 * Increments a given {@link monkey.ai.Player Player}'s counter. Does not
	 * increment anything if such {@link monkey.ai.Player Player} is <code>
	 * null</code>.
	 *
	 * @param player Specifies whose counter is to be incremented.
	 * @throws NullPointerException The player is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void increment(Player player) {
		if (player == null)
			throw new NullPointerException("The player can't be null.");
		if (Player.P1 == player)
			p1counter++;
		else
			p2counter++;
	}

	/**
	 * Decrements a given {@link monkey.ai.Player Player}'s counter. Does not
	 * increment anything if such {@link monkey.ai.Player Player} is <code>
	 * null</code>.
	 *
	 * @param player Specifies whose counter is to be decremented.
	 * @throws IllegalCallerException The counter is already at 0.
	 * @throws NullPointerException   The player is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void decrement(Player player) {
		if (player == null)
			throw new NullPointerException("The player can't be null.");
		if (Player.P1 == player) {
			if (p1counter == 0)
				throw new IllegalCallerException("The counter can't be negative.");
			--p1counter;
		} else {
			if (p2counter == 0)
				throw new IllegalCallerException("The counter can't be negative.");
			--p2counter;
		}
	}

	/**
	 * Returns the current value of a given {@link monkey.ai.Player Player}'s
	 * counter.
	 *
	 * @param player Specifies whose counter is to be decremented. Cannot be
	 *               <code>null</code>.
	 * @throws NullPointerException The player is <code>null</code>.
	 * @return The current value of the desired counter.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public int get(Player player) {
		if (player == null)
			throw new NullPointerException("The player can't be null.");
		return Player.P1 == player ? p1counter : p2counter;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return THREAT + ": " + p1counter + " - " + p2counter;
	}

	/** Number of {@link Threat}s by the first {@link monkey.ai.Player Player}. */
	private int p1counter = 0;
	/** Number of {@link Threat}s by the second {@link monkey.ai.Player Player}. */
	private int p2counter = 0;

}
