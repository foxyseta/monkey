package monkey.ai;

/**
 * A <code>Player</code> can refer to one of the two agents partaking in the
 * game.
 *
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public enum Player {
	/** The first agent to play. */
	P1(true) {
		public Player not() {
			return P2;
		}
	},
	/** The second agent to play. */
	P2(false) {
		public Player not() {
			return P1;
		}
	};

	/**
	 * Constructs a new {@link Player} based on whether they will be playing first
	 * or not.
	 *
	 * @param first <code>true</code> just in case the {@link Player} will be acting
	 *              first.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	Player(boolean first) {
	}

	/**
	 * Returns the other {@link Player}.
	 *
	 * @return The other {@link Player}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	abstract public Player not();
}
