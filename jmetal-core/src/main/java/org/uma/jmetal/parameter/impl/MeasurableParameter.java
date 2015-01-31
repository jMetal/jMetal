package org.uma.jmetal.parameter.impl;

import org.uma.jmetal.measure.Measure;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;
import org.uma.jmetal.measure.impl.SimplePushMeasure;
import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.ParameterManager;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

/**
 * A {@link MeasurableParameter} aims at being usable both as a
 * {@link Parameter} and a {@link Measure}. This way, one can store it both in a
 * {@link ParameterManager} and a {@link MeasureManager}. You can directly
 * instantiate a fully featured {@link MeasurableParameter} with
 * {@link #MeasurableParameter()} or wrap an existing {@link Parameter} with
 * {@link #MeasurableParameter(Parameter)}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class MeasurableParameter<Value> extends SimpleDescribedEntity implements
		Parameter<Value>, PullMeasure<Value>, PushMeasure<Value> {

	/**
	 * The {@link Parameter} which will fulfill the {@link Parameter} and
	 * {@link PullMeasure} requirements (method signatures are the same).
	 */
	private final Parameter<Value> parameter;
	/**
	 * The {@link PushMeasure} which will fulfill the {@link PushMeasure}
	 * requirements.
	 */
	private final SimplePushMeasure<Value> pusher;

	/**
	 * Create an autonomous {@link MeasurableParameter}.
	 */
	public MeasurableParameter() {
		this(new SimpleParameter<Value>());
	}

	/**
	 * Create a {@link MeasurableParameter} based on an existing
	 * {@link Parameter}. The {@link Parameter} provided is wrapped by this
	 * {@link MeasurableParameter}, so any modification made to the
	 * {@link Parameter} is applied to this {@link MeasurableParameter} too, and
	 * vice-versa.<br/>
	 * <br/>
	 * <b>ATTENTION:</b> Although setting the {@link Value} of the original
	 * {@link Parameter} changes the {@link Value} of the
	 * {@link MeasurableParameter} wrapping it, no notification is sent by the
	 * {@link MeasurableParameter} to the {@link MeasureListener}s registered
	 * through {@link #register(MeasureListener)}. These notifications only
	 * occur when the {@link MeasurableParameter} itself is used to change the
	 * {@link Parameter}'s {@link Value}.
	 * 
	 * @param parameter
	 *            the {@link Parameter} to wrap in this
	 *            {@link MeasurableParameter}
	 */
	/*
	 * TODO Find a solution to ensure that the modifications made to the wrapped
	 * parameter generate a notification to the registered listeners.
	 */
	public MeasurableParameter(Parameter<Value> parameter) {
		this.parameter = parameter;
		this.pusher = new SimplePushMeasure<Value>();
	}

	@Override
	public void set(Value value) {
		parameter.set(value);
		pusher.push(value);
	}

	@Override
	public Value get() {
		return parameter.get();
	}

	@Override
	public void register(MeasureListener<Value> listener) {
		pusher.register(listener);
	}

	@Override
	public void unregister(MeasureListener<Value> listener) {
		pusher.unregister(listener);
	}

}
