package monkey;

import java.util.Random;
import mnkgame.*;

/**
 * Totally random software player.
 */
public class MoNKey implements MNKPlayer {
	private Random rand;

	/**
	 * Default empty constructor
	 */
	public MoNKey() {
	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		rand = new Random(System.currentTimeMillis());
	}

	/**
	 * Selects a random cell in <code>FC</code>
	 */
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		return FC[rand.nextInt(FC.length)];
	}

	public String playerName() {
		return "🅼🐵🅽🅺ey";
	}
}
