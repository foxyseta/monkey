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
	 * @author Stefano Volpe
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
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void increment(Player player) {
		if (player == Player.P1)
			++p1counter;
		else
			++p2counter;
	}

	/**
	 * Decrements a given {@link monkey.ai.Player Player}'s counter. Does not
	 * increment anything if such {@link monkey.ai.Player Player} is <code>
	 * null</code>.
	 *
	 * @param player Specifies whose counter is to be decremented.
	 * @throws IllegalCallerException The counter is already at 0.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void decrement(Player player) {
		if (player == Player.P1) {
			if (p1counter == 0)
				throw new IllegalCallerException("P1's counter is already at 0");
			++p1counter;
		} else {
			if (p2counter == 0)
				throw new IllegalCallerException("P2's counter is already at 0");
			++p2counter;
		}
	}

	/**
	 * Returns the current value of a given {@link monkey.ai.Player Player}'s
	 * counter.
	 *
	 * @param player Specifies whose counter is to be decremented. Cannot be
	 *               <code>null</code>.
	 * @throws IllegalArgumentException player is <code>null</code>.
	 * @return The current value of the desired counter.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int get(Player player) {
		if (player == null)
			throw new IllegalArgumentException("player is null.");
		return player == Player.P1 ? p1counter : p2counter;
	}

	/** Number of {@link Threat}s by the first {@link monkey.ai.Player Player}. */
	private int p1counter = 0;
	/** Number of {@link Threat}s by the second {@link monkey.ai.Player Player}. */
	private int p2counter = 0;

}
