package monkey;

import java.util.Random;
import mnkgame.MNKCell;
import mnkgame.MNKPlayer;
import monkey.ai.AI;
import monkey.mnk.Board;
import monkey.mnk.Position;

/**
 * Totally random software player.
 */
public class MoNKey implements MNKPlayer {

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		rand = new Random(System.currentTimeMillis()); // TODO use AI instead
	}

	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		return FC[rand.nextInt(FC.length)]; // TODO use AI instead
	}

	public String playerName() {
		return "🅼🐵🅽🅺ey";
	}

	private Random rand;
	private AI<Board, Position, Integer> ai = null;
}
