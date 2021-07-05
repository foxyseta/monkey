package monkey.mnk;

/**
 * A <code>ThreatsCounter</code> keeps track of the number of {@link Threat}s of
 * a certain type.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class ThreatsCounter extends monkey.util.Pair<Threat, Integer> {

	/**
	 * Constructs a new {@link ThreatsCounter}.
	 *
	 * @param t {@link Threat} to keep track of.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public ThreatsCounter(Threat t) {
		super(t, 0);
	}

	/**
	 * Increments the counter.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void increment() {
		++value;
	}

	/**
	 * Decrements the counter.
	 *
	 * @throws IllegalCallerException The counter is already at 0.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void decrement() {
		if (value == 0)
			throw new IllegalCallerException("The counter is already at 0");
		--value;
	}

}
