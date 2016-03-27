package org.uma.jmetal.measure.impl;

import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A {@link CountingMeasure} provides a simple way to evaluate a number of
 * occurrences. For instance, it can be used to count how many solutions have
 * been generated within an algorithm, how many evaluations have been computed,
 * how many rounds have been run, etc. If these occurrences are provided by some
 * {@link PushMeasure}s, you can use {@link #link(PushMeasure)} to register the
 * {@link CountingMeasure} to these {@link PushMeasure}s. Otherwise, use
 * {@link #increment()} when the {@link CountingMeasure} need to count one more
 * occurrence. In order to get the count, one can access it immediately through
 * {@link #get()} or when it is updated by registering a listener with
 * {@link #register(MeasureListener)}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
@SuppressWarnings("serial")
public class CountingMeasure extends SimplePushMeasure<Long> implements
		PullMeasure<Long>, PushMeasure<Long> {

	/**
	 * The current amount of occurrences counted.
	 */
	long count = 0;

	/**
	 * The measures linked to this {@link CountingMeasure} through
	 * {@link #link(PushMeasure)}. We use a {@link WeakHashMap} to avoid keeping
	 * the measures/listeners in memory if they are not used anymore, allowing
	 * the garbage collection to free the memory properly. When this
	 * {@link CountingMeasure} is ready to be freed too, the remaining links are
	 * removed within {@link #finalize()}. This way, the listeners generated can
	 * also be properly freed and the measures will not notify us anymore.
	 */
	private final Map<PushMeasure<?>, MeasureListener<?>> linkedMeasures = new WeakHashMap<>();

	/**
	 * Create a {@link CountingMeasure} which starts at a given value. The next
	 * value to be pushed to the registered observers will be this value + 1.
	 * 
	 * @param name
	 *            the name of the measure
	 * @param description
	 *            the description of the measure
	 * @param initialCount
	 *            the value to start from
	 */
	public CountingMeasure(String name, String description, long initialCount) {
		super(name, description);
		count = initialCount;
	}

	/**
	 * Create a {@link CountingMeasure} starting from zero. The registered
	 * observers will receive their first notification when it will increment to
	 * 1.
	 * 
	 * @param name
	 *            the name of the measure
	 * @param description
	 *            the description of the measure
	 */
	public CountingMeasure(String name, String description) {
		this(name, description, 0);
	}

	/**
	 * Create a {@link CountingMeasure} which starts at a given value. The next
	 * value to be pushed to the registered observers will be this value + 1. A
	 * default name and description are used.
	 * 
	 * @param initialCount
	 *            the value to start from
	 */
	public CountingMeasure(long initialCount) {
		this(
				"Counter",
				"Generic counting measure which should be renamed/redescribed when it is used to count specific occurrences.",
				initialCount);
	}

	/**
	 * Create a {@link CountingMeasure} starting from zero. The registered
	 * observers will receive their first notification when it will increment to
	 * 1. A default name and description are used.
	 */
	public CountingMeasure() {
		this(0);
	}

	/**
	 * Add 1 to the current count and push its value to all the registered
	 * observers.
	 */
	public synchronized void increment() {
		increment(1);
	}

	/**
	 * Increment the current count in a given amount. If the amount is zero, no
	 * change occurs, thus no notification is sent.
	 * 
	 * @param amount
	 *            the amount to add
	 */
	public synchronized void increment(long amount) {
		if (amount == 0) {
			// No change, just ignore it
		} else {
			count += amount;
			push(count);
		}
	}

	/**
	 * 
	 * @return the current amount of occurrences counted
	 */
	@Override
	public synchronized Long get() {
		return count;
	}

	/**
	 * If this {@link CountingMeasure} is used to count the number of time a
	 * {@link PushMeasure} notifies its observers, you can use this method to
	 * link them. The {@link CountingMeasure} will automatically register a
	 * {@link MeasureListener} on the {@link PushMeasure} such that, every time
	 * the {@link PushMeasure} send a notification,
	 * {@link CountingMeasure#increment()} is called. You can link several
	 * {@link PushMeasure}s at the same time, but each of their notifications
	 * will increment the counter, leading to summing their notifications. When
	 * a {@link PushMeasure} should not be considered anymore, use
	 * {@link #unlink(PushMeasure)} to remove the link.
	 * 
	 * @param measure
	 *            the {@link PushMeasure} to link
	 */
	public <T> void link(PushMeasure<T> measure) {
		if (linkedMeasures.containsKey(measure)) {
			// already linked
		} else {
			MeasureListener<T> listener = new MeasureListener<T>() {

				@Override
				public void measureGenerated(T value) {
					increment();
				}
			};
			measure.register(listener);
			linkedMeasures.put(measure, listener);
		}
	}

	/**
	 * If you have linked a {@link PushMeasure} through
	 * {@link #link(PushMeasure)}, you can discard the link by using this
	 * method.
	 * 
	 * @param measure
	 *            the {@link PushMeasure} to unlink
	 */
	@SuppressWarnings("unchecked")
	public <T> void unlink(PushMeasure<T> measure) {
		MeasureListener<T> listener = (MeasureListener<T>) linkedMeasures
				.get(measure);
		if (listener == null) {
			// no registered listener
		} else {
			measure.unregister(listener);
			linkedMeasures.remove(measure);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		/*
		 * Use an intermediary collection for the loop to avoid the concurrent
		 * accesses leading to exceptions or inconsistent states.
		 */
		Collection<PushMeasure<?>> remainingMeasures = new LinkedList<>(
				linkedMeasures.keySet());
		for (PushMeasure<?> measure : remainingMeasures) {
			unlink(measure);
		}
		super.finalize();
	}

	/**
	 * Restart the counter to zero. Generate a notification if the value was not
	 * zero.
	 */
	public synchronized void reset() {
		reset(0);
	}

	/**
	 * Restart the counter to a given value. Generate a notification if the
	 * value was different.
	 * 
	 * @param value
	 *            the value to restart from
	 */
	public synchronized void reset(long value) {
		increment(value - count);
	}
}
