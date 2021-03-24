package jenny;

import java.util.Random;
import mnkgame.*;

/**
 * Totally random software player.
 */
public class Jenny implements MNKPlayer {
	private Random rand;

	/**
   * Default empty constructor
   */
	public Jenny() {
	}

	public void initPlayer(int M, int N, int K, boolean first) {
		// New random seed for each game
		rand = new Random(System.currentTimeMillis());

		// Uncomment to chech the initialization timeout
		/*
		 * try { Thread.sleep(1000*61); } catch(Exception e) { }
		 */
	}

	/**
	 * Selects a random cell in <code>FC</code>
	 */
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		// Uncomment to chech the move timeout
		/*
		 * try { Thread.sleep(1000*11); } catch(Exception e) { }
		 */
		return FC[rand.nextInt(FC.length)];
	}

	public String playerName() {
		return "R4nd0m";
	}
}
