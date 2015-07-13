package org.uma.jmetal.parameter.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.uma.jmetal.parameter.Parameter;

/**
 * The {@link ParameterFactory} provides some useful methods to build specific
 * {@link Parameter}s.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class ParameterFactory {

	/**
	 * Create {@link Parameter}s based on the setters/getters pairs available
	 * from an instance, whatever it is. The {@link Class} of the instance is
	 * analyzed to retrieve its public methods and a {@link Parameter} is built
	 * for each <i>X</i> having both a <i>setX()</i> method and a <i>getX()</i>
	 * method. The name of the method (<i>X</i>) is further exploited to name
	 * the {@link Parameter}.
	 * 
	 * @param instance
	 *            the instance to cover
	 * @return the {@link Parameter}s retrieved for the instance
	 */
	public Collection<Parameter<Object>> createParametersFromSettersGetters(
			final Object instance) {
		Map<String, Method> setters = new HashMap<>();
		Map<String, Method> getters = new HashMap<>();
		Class<? extends Object> clazz = instance.getClass();
		for (Method method : clazz.getMethods()) {
			if (method.getParameterTypes().length == 0
					&& !method.getReturnType().equals(Void.TYPE)
					&& !method.getName().equals("getClass")
					&& method.getName().matches("get[^a-z].*")) {
				String key = method.getName().substring(3);
				getters.put(key, method);
			} else if (method.getParameterTypes().length == 1
					&& method.getReturnType().equals(Void.TYPE)
					&& method.getName().matches("set[^a-z].*")) {
				String key = method.getName().substring(3);
				setters.put(key, method);
			} else {
				// not a getter nor a setter, ignore it
			}
		}

		Set<String> setKeys = setters.keySet();
		Set<String> getKeys = getters.keySet();
		setKeys.retainAll(getKeys);
		getKeys.retainAll(setKeys);

		Collection<Parameter<Object>> parameters = new LinkedList<>();
		for (final String key : getKeys) {
			final Method setter = setters.get(key);
			final Method getter = getters.get(key);
			if (setter.getParameterTypes()[0].equals(getter.getReturnType())) {
				parameters.add(new Parameter<Object>() {

					@Override
					public String getName() {
						return key;
					}

					@Override
					public String getDescription() {
						return key + " parameter for " + instance;
					}

					@Override
					public void set(Object value) {
						try {
							setter.invoke(instance, value);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public Object get() {
						try {
							return getter.invoke(instance);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public String toString() {
						return getName() + "=" + get();
					}
				});
			} else {
				//  type misalignment, ignore them
			}
		}
		return parameters;
	}

	/**
	 * Create a {@link Parameter} based on an existing setter/getter pair of an
	 * instance. The {@link Class} of the instance is analyzed to retrieve a
	 * <i>setX()</i> method and a <i>getX()</i> method corresponding to the
	 * requested name <i>X</i>.
	 * 
	 * @param instance
	 *            the instance to cover
	 * @param name
	 *            the name of the {@link Parameter}
	 * @param description
	 *            the description of the {@link Parameter}
	 * @return the {@link Parameter}s retrieved for the instance
	 * @throws NoSuchElementException
	 *             if a corresponding setter or getter miss to build the
	 *             {@link Parameter}
	 */
	public <Value> Parameter<Value> createParameterFromSetterGetter(
			final Object instance, final String name, final String description) {
		Method setter = null;
		Method getter = null;
		String key = name.replaceAll("\\s+", "");
		Class<? extends Object> clazz = instance.getClass();
		for (Method method : clazz.getMethods()) {
			if (method.getParameterTypes().length == 0
					&& !method.getReturnType().equals(Void.TYPE)
					&& method.getName().equalsIgnoreCase("get" + key)) {
				getter = method;
			} else if (method.getParameterTypes().length == 1
					&& method.getReturnType().equals(Void.TYPE)
					&& method.getName().equalsIgnoreCase("set" + key)) {
				setter = method;
			} else {
				// not the searched getter nor setter, ignore it
			}
		}

		if (setter == null) {
			throw new NoSuchElementException("No setter found for " + name);
		} else if (getter == null) {
			throw new NoSuchElementException("No getter found for " + name);
		} else {
			final Method[] methods = new Method[] { setter, getter };
			return new Parameter<Value>() {

				@Override
				public String getName() {
					return name;
				}

				@Override
				public String getDescription() {
					return description;
				}

				@Override
				public void set(Value value) {
					try {
						methods[0].invoke(instance, value);
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				public Value get() {
					try {
						return (Value) methods[1].invoke(instance);
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public String toString() {
					return getName() + "=" + get();
				}
			};
		}
	}

}
