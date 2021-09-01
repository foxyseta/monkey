package monkey;

import mnkgame.MNKCell;
import mnkgame.MNKPlayer;
import monkey.ai.AI;
import monkey.ai.Player;
import monkey.mnk.Board;
import monkey.mnk.Position;

/**
 * A <code>MoNKey</code> offers a possible implementation of
 * <code>MNKPlayer</code> using an instance of {@link monkey.ai.AI}.
 *
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class MoNKey implements MNKPlayer {

	/**
	 * {@inheritDoc}
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		ai = new AI<Board, Position>(first ? Player.P1 : Player.P2, new Board(M, N, K), timeout_in_secs * S_TO_MS);
		m = M;
		n = N;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		// final long startTime = System.currentTimeMillis();
		if (MC.length > 1)
			ai.update(new Position(m, n, MC[MC.length - 2]));
		if (MC.length > 0)
			ai.update(new Position(m, n, MC[MC.length - 1]));
		final Position p = m * n > BIGGAME ? ai.immediateSearch() : ai.iterativeDeepeningSearch();
		// System.err.println(formatTimeInterval(System.currentTimeMillis() -
		// startTime));
		return new MNKCell(p.getRow(), p.getColumn());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public String playerName() {
		return "🅼🐵🅽🅺ey";
	}

	/**
	 * Formats a number of milliseconds converting it into seconds and milliseconds.
	 *
	 * @param milliseconds The number to convert and format.
	 * @return A formatted <code>String</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public static String formatTimeInterval(long milliseconds) {
		return String.format("⏱️ %2d\"%03d", milliseconds / S_TO_MS, milliseconds % S_TO_MS);
	}

	/** Artificial intelligence used by <code>MoNKey</code>. */
	private AI<Board, Position> ai = null;
	/** Number of rows. */
	private int m;
	/** Number of columns. */
	private int n;
	/**
	 * Maximum number of cells of a configuration commonly considered small enough
	 * to be explored.
	 */
	final static private int BIGGAME = 100;
	/** Conversion factor from seconds to milliseconds. */
	final static private int S_TO_MS = 1000;

}
