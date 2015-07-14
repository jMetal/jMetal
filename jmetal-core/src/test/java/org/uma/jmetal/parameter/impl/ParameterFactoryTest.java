package org.uma.jmetal.parameter.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.uma.jmetal.parameter.Parameter;

public class ParameterFactoryTest {

	@Test
	public void testParametersFromSettersGettersRetrieveNothingFromEmptyClass() {
		ParameterFactory factory = new ParameterFactory();
		Object instance = new Object();
		Collection<Parameter<Object>> parameters = factory
				.createParametersFromSettersGetters(instance);
		assertTrue(parameters.toString(), parameters.isEmpty());
	}

	@SuppressWarnings("unused")
	@Test
	public void testParametersFromSettersGettersRetrieveSettersGetters() {
		ParameterFactory factory = new ParameterFactory();
		Object instance = new Object() {
			private int a;

			public void setA(int a) {
				this.a = a;
			}

			public int getA() {
				return a;
			}

			private boolean b;

			public void setB(boolean b) {
				this.b = b;
			}

			public boolean getB() {
				return b;
			}
		};
		Collection<Parameter<Object>> parameters = factory
				.createParametersFromSettersGetters(instance);
		assertEquals(parameters.toString(), 2, parameters.size());
		Iterator<Parameter<Object>> iterator = parameters.iterator();
		Parameter<Object> p1 = iterator.next();
		Parameter<Object> p2 = iterator.next();
		if (p1.getName().equals("A")) {
			assertEquals("A", p1.getName());
			assertEquals("B", p2.getName());
		} else {
			assertEquals("B", p1.getName());
			assertEquals("A", p2.getName());
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testParametersFromSettersGettersProperlyLinkToInstance() {
		ParameterFactory factory = new ParameterFactory();
		final Map<String, Object> values = new HashMap<>();
		values.put("A", 5);
		values.put("B", true);
		Object instance = new Object() {
			public void setA(int a) {
				values.put("A", a);
			}

			public int getA() {
				return (int) values.get("A");
			}

			public void setB(boolean b) {
				values.put("B", b);
			}

			public boolean getB() {
				return (boolean) values.get("B");
			}
		};

		Collection<Parameter<Object>> parameters = factory
				.createParametersFromSettersGetters(instance);
		Iterator<Parameter<Object>> iterator = parameters.iterator();
		Parameter<Object> p1 = iterator.next();
		Parameter<Object> p2 = iterator.next();
		Parameter<Object> pA;
		Parameter<Object> pB;
		if (p1.getName().equals("A")) {
			pA = p1;
			pB = p2;
		} else {
			pA = p2;
			pB = p1;
		}

		pA.set(20);
		assertEquals(20, pA.get());
		assertEquals(20, values.get("A"));

		pB.set(false);
		assertEquals(false, pB.get());
		assertEquals(false, values.get("B"));
	}

	@SuppressWarnings("unused")
	@Test
	public void testParameterFromSetterGetterThrowExceptionForWrongName() {
		ParameterFactory factory = new ParameterFactory();
		Object instance = new Object() {
			private int a = 5;

			public void setA(int a) {
				this.a = a;
			}

			public int getA() {
				return a;
			}

			private boolean b = true;

			public void setB(boolean b) {
				this.b = b;
			}

			public boolean getB() {
				return b;
			}
		};

		try {
			factory.createParameterFromSetterGetter(instance, "c",
					"inexistent parameter");
			fail("No exception thrown");
		} catch (NoSuchElementException e) {
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testParameterFromSetterGetterRetrieveCorrectSetterGetter() {
		ParameterFactory factory = new ParameterFactory();
		Object instance = new Object() {
			private int a = 5;

			public void setA(int a) {
				this.a = a;
			}

			public int getA() {
				return a;
			}

			private boolean b = true;

			public void setB(boolean b) {
				this.b = b;
			}

			public boolean getB() {
				return b;
			}
		};

		Parameter<Integer> pA = factory.createParameterFromSetterGetter(
				instance, "a", "description of a");
		assertNotNull(pA);
		assertEquals("a", pA.getName());
		assertEquals("description of a", pA.getDescription());

		Parameter<Boolean> pB = factory.createParameterFromSetterGetter(
				instance, "b", "description of b");
		assertNotNull(pB);
		assertEquals("b", pB.getName());
		assertEquals("description of b", pB.getDescription());
	}

	@SuppressWarnings("unused")
	@Test
	public void testParameterFromSetterGetterRobustToSpacesAndCase() {
		ParameterFactory factory = new ParameterFactory();
		Object instance = new Object() {
			private int parameterForInteger = 5;

			public void setParameterForInteger(int parameterForInteger) {
				this.parameterForInteger = parameterForInteger;
			}

			public int getParameterForInteger() {
				return parameterForInteger;
			}

			private boolean parameterForBoolean = true;

			public void setParameterForBoolean(boolean parameterForBoolean) {
				this.parameterForBoolean = parameterForBoolean;
			}

			public boolean getParameterForBoolean() {
				return parameterForBoolean;
			}
		};

		Parameter<Integer> pA = factory.createParameterFromSetterGetter(
				instance, "parameter for integer", null);
		assertNotNull(pA);
		assertEquals("parameter for integer", pA.getName());

		Parameter<Boolean> pB = factory.createParameterFromSetterGetter(
				instance, "PARAMETER FOR BOOLEAN", null);
		assertNotNull(pB);
		assertEquals("PARAMETER FOR BOOLEAN", pB.getName());
	}

	@SuppressWarnings("unused")
	@Test
	public void testParameterFromSetterGetterProperlyLinkToInstance() {
		ParameterFactory factory = new ParameterFactory();
		final Map<String, Object> values = new HashMap<>();
		values.put("A", 5);
		values.put("B", true);
		Object instance = new Object() {
			public void setA(int a) {
				values.put("A", a);
			}

			public int getA() {
				return (int) values.get("A");
			}

			public void setB(boolean b) {
				values.put("B", b);
			}

			public boolean getB() {
				return (boolean) values.get("B");
			}
		};

		Parameter<Integer> pA = factory.createParameterFromSetterGetter(
				instance, "a", null);
		assertEquals((Integer) 5, pA.get());
		pA.set(20);
		assertEquals((Integer) 20, pA.get());
		assertEquals(20, values.get("A"));

		Parameter<Boolean> pB = factory.createParameterFromSetterGetter(
				instance, "b", null);
		assertEquals(true, pB.get());
		pB.set(false);
		assertEquals(false, pB.get());
		assertEquals(false, values.get("B"));
	}
}
