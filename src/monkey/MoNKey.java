package monkey;

import mnkgame.MNKCell;
import mnkgame.MNKPlayer;
import monkey.ai.AI;
import monkey.ai.Player;
import monkey.mnk.Board;
import monkey.mnk.Position;

/**
 * A <code>MoNKey</code> offers a possible implementation of
 * <code>MNKPlayer</code> using an instance of <code>AI</code>.
 *
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class MoNKey implements MNKPlayer {

	@Override // inherit doc comment
	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		ai = new AI<Board, Position, Integer>(first ? Player.P1 : Player.P2, new Board(M, N, K), timeout_in_secs);
		m = M;
		n = N;
	}

	@Override // inherit doc comment
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		long startTime = System.currentTimeMillis();
		if (MC.length != 0)
			ai.update(new Position(m, n, MC[MC.length - 1]));
		final Position p = ai.alphaBetaSearch();
		ai.update(p);
		System.out.println(System.currentTimeMillis() - startTime);
		return new MNKCell(p.getRow(), p.getColumn());
	}

	@Override // inherit doc comment
	public String playerName() {
		return "🅼🐵🅽🅺ey";
	}

	/** Artificial intelligence used by <code>MoNKey</code>. */
	private AI<Board, Position, Integer> ai = null;
	/** Number of rows. */
	private int m;
	/** Number of columns. */
	private int n;
}
