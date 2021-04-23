package monkey.ai;

/**
 * A <code>Player</code> can refer to one of the two agents partaking in the
 * game.
 *
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
enum Player {
	/** The first agent to play. */
	P1(true) {
		/**
		 * Returns the other {@link Player}.
		 *
		 * @return The other {@link Player}.
		 * @author Gaia Clerici
		 * @version 1.0
		 * @since 1.0
		 */
		// Player not() { }
	},
	/** The second agent to play. */
	P2(false) {
		/**
		 * Returns the other {@link Player}.
		 *
		 * @return The other {@link Player}.
		 * @author Gaia Clerici
		 * @version 1.0
		 * @since 1.0
		 */
		// Player not() { }
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
}
