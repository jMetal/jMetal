package org.uma.jmetal.measure.impl;

import org.uma.jmetal.measure.Measure;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;
import org.uma.jmetal.util.naming.DescribedEntity;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

/**
 * A {@link PullPushMeasure} aims at providing both the {@link PushMeasure} and
 * {@link PullMeasure} abilities into a single {@link Measure}. One could simply
 * build a brand new {@link Measure} by calling
 * {@link #PullPushMeasure(String, String)}, but in the case where some existing
 * measures are available, he can wrap them into a {@link PullPushMeasure} by
 * calling {@link #PullPushMeasure(PushMeasure, Object)} or other constructors
 * taking a {@link Measure} as argument.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
@SuppressWarnings("serial")
public class PullPushMeasure<Value> implements PullMeasure<Value>,
		PushMeasure<Value> {

	/**
	 * The measure responsible of the {@link #get()} method.
	 */
	private final PullMeasure<Value> puller;
	/**
	 * The {@link Measure} responsible of the {@link #register(MeasureListener)}
	 * and {@link #unregister(MeasureListener)} methods.
	 */
	private final PushMeasure<Value> pusher;
	/**
	 * The entity responsible of the {@link #getName()} and
	 * {@link #getDescription()} methods, potentially the same than
	 * {@link #puller} or {@link #pusher}.
	 */
	private final DescribedEntity reference;

	/**
	 * Create a {@link PullPushMeasure} which wraps both a {@link PullMeasure}
	 * and a {@link PushMeasure}. The assumption is that both {@link Measure}s
	 * already represent the same {@link Measure} (i.e. the same {@link Value})
	 * but were implemented separately. Instantiating a {@link PullPushMeasure}
	 * this way allows to merge them easily without creating a completely new
	 * measure. Don't use this constructor to merge two different
	 * {@link Measure}s. The last parameter is generally used to specify which
	 * of the two {@link Measure}s should be used for {@link #getName()} and
	 * {@link #getDescription()}, but you can also provide a completely new
	 * instance to change them.
	 * 
	 * @param pull
	 *            the {@link PullMeasure} to wrap
	 * @param push
	 *            the {@link PushMeasure} to wrap
	 * @param reference
	 *            the reference to use for the name and the description of this
	 *            {@link PullPushMeasure}
	 */
	public PullPushMeasure(PullMeasure<Value> pull, PushMeasure<Value> push,
			DescribedEntity reference) {
		this.puller = pull;
		this.pusher = push;
		this.reference = reference;
	}

	/**
	 * Equivalent to
	 * {@link #PullPushMeasure(PullMeasure, PushMeasure, DescribedEntity)} but
	 * the reference parameter is replaced by the specific name and description
	 * that you want to provide. This is a shortcut to the creation of the
	 * {@link DescribedEntity} instance followed by the call of the
	 * reference-based method.
	 * 
	 * @param pull
	 *            the {@link PullMeasure} to wrap
	 * @param push
	 *            the {@link PushMeasure} to wrap
	 * @param name
	 *            the name of the {@link PullPushMeasure}
	 * @param description
	 *            the description of the {@link PullPushMeasure}
	 */
	public PullPushMeasure(PullMeasure<Value> pull, PushMeasure<Value> push,
			String name, String description) {
		this(pull, push, new SimpleDescribedEntity(name, description));
	}

	/**
	 * Create a {@link PullPushMeasure} which wraps a {@link PushMeasure}. The
	 * {@link PullMeasure} ability corresponds the storage of the {@link Value}
	 * pushed by the {@link PushMeasure} in order to retrieve it on demand
	 * through {@link PullMeasure#get()}. The name and the description of the
	 * {@link PullPushMeasure} are the ones provided by the wrapped
	 * {@link PushMeasure}.
	 * 
	 * @param push
	 *            the {@link PushMeasure} to wraps
	 * @param initialValue
	 *            the {@link Value} to return before the next notification of
	 *            the {@link PushMeasure}
	 */
	public PullPushMeasure(PushMeasure<Value> push, Value initialValue) {
		this(new MeasureFactory().createPullFromPush(push, initialValue), push,
				push);
	}

	/**
	 * Create a {@link PullPushMeasure} from scratch.
	 * 
	 * @param name
	 *            the name of the {@link PullPushMeasure}
	 * @param description
	 *            the description of the {@link PullPushMeasure}
	 */
	public PullPushMeasure(String name, String description) {
		/*
		 * FIXME No way to access the newly created push measure. Probably
		 * enclosing existing measures and creating a new one are conceptually
		 * incompatible (the source of push is different) and so should not be
		 * together in the same class.
		 */
		this(new SimplePushMeasure<Value>(name, description), null);
	}

	@Override
	public void register(MeasureListener<Value> listener) {
		pusher.register(listener);
	}

	@Override
	public void unregister(MeasureListener<Value> listener) {
		pusher.unregister(listener);
	}

	@Override
	public Value get() {
		return puller.get();
	}

	@Override
	public String getName() {
		return reference.getName();
	}

	@Override
	public String getDescription() {
		return reference.getDescription();
	}
}
