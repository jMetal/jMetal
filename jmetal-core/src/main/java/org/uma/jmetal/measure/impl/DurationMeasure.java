package org.uma.jmetal.measure.impl;

/**
 * This measure allows to have a simple way to compute the time spent in doing
 * something. For instance, an algorithm can compute the time spent to run. In
 * such a case, the algorithm would call {@link #start()} at the beginning of
 * the running and {@link #stop()} at the end. Additional calls to these two
 * methods can also be made during the running to exclude specific parts from
 * the counting. At any time during (and after) the running, the {@link #get()}
 * method can be used to know how much time have been spent so far. If the
 * algorithm is rerun, it will restart and the additional time will sum up to
 * the time already spent before, but it can be avoided by resetting the measure
 * with {@link #reset()}.
 * 
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
@SuppressWarnings("serial")
public class DurationMeasure extends SimplePullMeasure<Long> {

	/**
	 * During a full round (start-stop), some time is spent. When
	 * {@link #stop()} is called, this time is added to this variable, so that
	 * it provides the total time spent during all the previous rounds (total
	 * time minus the time spent in the current round).
	 */
	private long previousRoundsDuration = 0;
	/**
	 * The timestamp of when the current round started. Its value is
	 * <code>null</code> if we are not in a round (if {@link #start()} has not
	 * been called or if {@link #stop()} has been called).
	 */
	private Long roundStart = null;

	public DurationMeasure() {
		super("duration", "Provide the duration of execution of an algorithm.");
	}

	/**
	 * Start a round. If the round is already started, it has no effect.
	 */
	public void start() {
		if (roundStart == null) {
			roundStart = System.currentTimeMillis();
		} else {
			// already started
		}
	}

	/**
	 * Stop a round. If the round is already stopped, it has no effect.
	 */
	public void stop() {
		if (roundStart == null) {
			// already stopped
		} else {
			previousRoundsDuration += getCurrentRoundDuration();
			roundStart = null;
		}
	}

	/**
	 * 
	 * @return the total time spent so far
	 */
	@Override
	public Long get() {
		return previousRoundsDuration + getCurrentRoundDuration();
	}

	private long getCurrentRoundDuration() {
		if (roundStart == null) {
			// not in a round
			return 0;
		} else {
			long now = System.currentTimeMillis();
			return now - roundStart;
		}
	}

	/**
	 * Reset the total time to zero. If a round is currently running, it is
	 * restarted.
	 */
	public void reset() {
		previousRoundsDuration = 0;
		if (roundStart == null) {
			// no round to restart
		} else {
			roundStart = System.currentTimeMillis();
		}
	}
}
