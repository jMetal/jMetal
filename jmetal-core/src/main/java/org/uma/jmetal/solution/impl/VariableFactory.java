package org.uma.jmetal.solution.impl;

import org.uma.jmetal.solution.SolutionBuilder.Variable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * This factory provides facilities to generate {@link Variable}s from usual
 * situations.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class VariableFactory {

	/**
	 * This method retrieves all the values accessible through a getter (
	 * <code>getX()</code> method) in order to build the corresponding set of
	 * {@link Variable}s. Notice that {@link Variable}s are supposed to
	 * represent the fundamental description of a {@link Solution}, so if the
	 * {@link Solution} has computation or other additional methods which are
	 * named as getters, they will also be retrieved as {@link Variable}s. In
	 * such a case, you should filter the returned {@link Variable}s, rely on
	 * more advanced methods, or generate the {@link Variable}s manually.
	 * 
	 * @param solutionClass
	 *            the {@link Solution} class to analyze
	 * @return the set of {@link Variable}s retrieved from this class
	 * @see #createFromGettersAndSetters(Class)
	 * @see #createFromGettersAndConstructors(Class)
	 */
	public <Solution> Collection<Variable<Solution, ?>> createFromGetters(
			Class<Solution> solutionClass) {
		Collection<Variable<Solution, ?>> variables = new LinkedList<>();
		for (Method method : solutionClass.getMethods()) {
			if (isGetter(method)) {
				String name = method.getName().substring(3);
				variables.add(createVariableOn(solutionClass, method, name,
						method.getReturnType()));
			} else {
				// not a getter, ignore it
			}
		}
		return variables;
	}

	/**
	 * This method retrieves all the values accessible through a getter (
	 * <code>getX()</code> method) in order to build the corresponding set of
	 * {@link Variable}s. At the opposite of {@link #createFromGetters(Class)},
	 * an additional filter is used: we build a {@link Variable} for each getter
	 * which corresponds to a setter (<code>setX()</code> method with the same
	 * <code>X</code> than the getter). This method is adapted for dynamic
	 * {@link Solution} implementations, thus allowing to change the value of
	 * its {@link Variable}s (e.g. change the path of a TSP {@link Solution}).<br/>
	 * <br/>
	 * Notice that, if all the relevant setters are not present (or they do not
	 * strictly respect the naming of the getter), the corresponding
	 * {@link Variable}s will not be retrieved. On the opposite, any additional
	 * setter/getter couple which does not correspond to a relevant
	 * {@link Variable} will be mistakenly retrieved. So be sure that the
	 * relevant elements (and only these ones) have their setter and getter.
	 * Otherwise, you should use a different method or generate the
	 * {@link Variable}s manually.
	 * 
	 * @param solutionClass
	 *            the {@link Solution} class to analyze
	 * @return the set of {@link Variable}s retrieved from this class
	 */
	public <Solution> Collection<Variable<Solution, ?>> createFromGettersAndSetters(
			Class<Solution> solutionClass) {
		Map<String, Method> getters = new HashMap<>();
		Map<String, Method> setters = new HashMap<>();
		for (Method method : solutionClass.getMethods()) {
			if (isGetter(method)) {
				String name = method.getName().substring(3);
				getters.put(name, method);
			} else if (isSetter(method)) {
				String name = method.getName().substring(3);
				setters.put(name, method);
			} else {
				// not a getter/setter, ignore it
			}
		}

		getters.keySet().retainAll(setters.keySet());
		setters.keySet().retainAll(getters.keySet());
		for (String name : getters.keySet()) {
			Method getter = getters.get(name);
			Method setter = setters.get(name);
			if (getter.getReturnType().equals(setter.getParameterTypes()[0])) {
				// setter and getter are compatible
			} else {
				getters.remove(name);
				setters.remove(name);
			}
		}

		Collection<Variable<Solution, ?>> variables = new LinkedList<>();
		for (Entry<String, Method> entry : getters.entrySet()) {
			String name = entry.getKey();
			Method getter = entry.getValue();
			variables.add(createVariableOn(solutionClass, getter, name,
					getter.getReturnType()));
		}
		return variables;
	}

	private boolean isSetter(Method method) {
		return method.getParameterTypes().length == 1
				&& method.getReturnType() == void.class
				&& method.getName().matches("set[^a-z].*");
	}

	private boolean isGetter(Method method) {
		return method.getParameterTypes().length == 0
				&& method.getReturnType() != void.class
				&& !method.getName().equals("getClass")
				&& method.getName().matches("get[^a-z].*");
	}

	/**
	 * This method retrieves all the values accessible through a getter (
	 * <code>getX()</code> method) in order to build the corresponding set of
	 * {@link Variable}s. At the opposite of {@link #createFromGetters(Class)},
	 * an additional filter is used: we build a {@link Variable} for each getter
	 * which corresponds to a constructor argument (argument of the same type).
	 * This method is adapted for static {@link Solution} implementations, which
	 * usually have a constructor which takes all the relevant values and
	 * provide getters to retrieve them.<br/>
	 * <br/>
	 * Because Java reflection does not always provide the required information
	 * (e.g. names of constructor arguments), this method can be applied only on
	 * solution classes which meet strict constraints:
	 * <ul>
	 * <li>only one getter should return a given type</li>
	 * <li>for each constructor and between constructors, only one argument
	 * should be of a given type (it can appear in several constructors, but it
	 * should be always the same argument)</li>
	 * </ul>
	 * If all the constraints are not met, an exception will be thrown.
	 * 
	 * @param solutionClass
	 *            the {@link Solution} class to analyze
	 * @return the set of {@link Variable}s retrieved from this class
	 * @throws IllegalArgumentException
	 *             if one of the constraints is not met
	 * @throws IsInterfaceException
	 *             if the {@link Solution} class to analyze is an interface,
	 *             thus constructors make no sense
	 */
	public <Solution> Collection<Variable<Solution, ?>> createFromGettersAndConstructors(
			Class<Solution> solutionClass) {
		if (solutionClass.isInterface()) {
			throw new IsInterfaceException(solutionClass);
		} else {
			Map<String, Method> getters = new HashMap<>();
			Map<Class<?>, String> types = new HashMap<>();
			for (Method method : solutionClass.getMethods()) {
				Class<?> returnType = method.getReturnType();
				if (method.getParameterTypes().length == 0
						&& returnType != null
						&& !method.getName().equals("getClass")
						&& method.getName().matches("get[^a-z].*")) {
					String name = method.getName().substring(3);
					getters.put(name, method);
					if (types.containsKey(returnType)) {
						throw new IllegalArgumentException(
								types.get(returnType) + " and " + name
										+ " are both of type " + returnType
										+ ", we cannot differentiate them");
					} else {
						types.put(returnType, name);
					}
				} else {
					// not a getter, ignore it
				}
			}

			Collection<Variable<Solution, ?>> variables = new LinkedList<>();
			for (Constructor<?> constructor : solutionClass.getConstructors()) {
				Class<?>[] constructorTypes = constructor.getParameterTypes();
				Set<Class<?>> uniqueTypes = new HashSet<>(
						Arrays.asList(constructorTypes));
				if (uniqueTypes.size() < constructorTypes.length) {
					throw new IllegalArgumentException(
							"Some constructor types are redundant, we cannot differentiate them: "
									+ Arrays.asList(constructorTypes));
				} else {
					for (Class<?> type : constructorTypes) {
						String name = types.remove(type);
						if (name == null) {
							// constructor value without getter or already done
						} else {
							Method getter = getters.get(name);
							variables.add(createVariableOn(solutionClass,
									getter, name, type));
						}
					}
				}
			}
			return variables;
		}
	}

	@SuppressWarnings("serial")
	public static class IsInterfaceException extends RuntimeException {
		public IsInterfaceException(Class<?> solutionClass) {
			super("No constructor exists for " + solutionClass
					+ ", are you sure it is not an interface?");
		}
	}

	private <Solution, Value> Variable<Solution, Value> createVariableOn(
			final Class<Solution> solutionClass, final Method getter,
			final String name, final Class<Value> type) {
		return new Variable<Solution, Value>() {

			@SuppressWarnings("unchecked")
			@Override
			public Value get(Solution solution) {
				try {
					return (Value) getter.invoke(solution);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getDescription() {
				return type.getSimpleName() + " value for the "
						+ solutionClass.getSimpleName() + " solutions.";
			}

			@Override
			public String toString() {
				return getName();
			}
		};
	}

}
