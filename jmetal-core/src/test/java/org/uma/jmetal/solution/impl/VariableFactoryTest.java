package org.uma.jmetal.solution.impl;

import org.junit.Test;
import org.uma.jmetal.solution.SolutionBuilder.Variable;
import org.uma.jmetal.solution.impl.VariableFactory.IsInterfaceException;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class VariableFactoryTest {

	private <Solution> Collection<String> retrieveVariableNames(
			Collection<Variable<Solution, ?>> variables) {
		Collection<String> names = new LinkedList<>();
		for (Variable<Solution, ?> variable : variables) {
			names.add(variable.getName());
		}
		return names;
	}

	private class FakeVariable implements Variable<Object, Object> {

		private final String name;
		private final Object value;

		public FakeVariable(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return "Description of " + name;
		}

		@Override
		public Object get(Object solution) {
			return value;
		}

	}

	@Test
	public void testRetrieveVariableNames() {
		Variable<Object, ?> variable1 = new FakeVariable("a", null);
		Variable<Object, ?> variable2 = new FakeVariable("b", null);
		Variable<Object, ?> variable3 = new FakeVariable("c", null);
		Collection<Variable<Object, ?>> variables = Arrays.asList(variable1,
				variable2, variable3);

		Collection<String> names = retrieveVariableNames(variables);
		assertEquals(names.toString(), 3, names.size());
		assertTrue(names.contains(variable1.getName()));
		assertTrue(names.contains(variable2.getName()));
		assertTrue(names.contains(variable3.getName()));
	}

	private interface EmptySolution {
	}

	private interface DifferentGettersFewSettersSolution {
		public int getA();

		public void setA(int a);

		public Object getB();
	}

	private interface DifferentGettersAllSettersSolution {
		public int getA();

		public void setA(int a);

		public Object getB();

		public void setB(Object b);

	}

	private interface SimilarGettersFewSettersSolution {
		public int getA();

		public void setA(int a);

		public Object getB();

		public int getC();
	}

	private interface SimilarGettersAllSettersSolution {
		public int getA();

		public void setA(int a);

		public Object getB();

		public void setB(Object b);

		public int getC();

		public void setC(int c);
	}

	@Test
	public void testCreateFromGettersRetrievesAllGetters() {
		VariableFactory factory = new VariableFactory();

		{
			Collection<Variable<EmptySolution, ?>> variables = factory
					.createFromGetters(EmptySolution.class);
			assertTrue(variables.toString(), variables.isEmpty());
		}

		{
			Collection<Variable<DifferentGettersFewSettersSolution, ?>> variables = factory
					.createFromGetters(DifferentGettersFewSettersSolution.class);
			assertEquals(variables.toString(), 2, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}

		{
			Collection<Variable<DifferentGettersAllSettersSolution, ?>> variables = factory
					.createFromGetters(DifferentGettersAllSettersSolution.class);
			assertEquals(variables.toString(), 2, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}

		{
			Collection<Variable<SimilarGettersFewSettersSolution, ?>> variables = factory
					.createFromGetters(SimilarGettersFewSettersSolution.class);
			assertEquals(variables.toString(), 3, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
			assertTrue(names.contains("C"));
		}

		{
			Collection<Variable<SimilarGettersAllSettersSolution, ?>> variables = factory
					.createFromGetters(SimilarGettersAllSettersSolution.class);
			assertEquals(variables.toString(), 3, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
			assertTrue(names.contains("C"));
		}
	}

	@Test
	public void testCreateFromGettersAndSettersRetrievesOnlyGettersWithSetters() {
		VariableFactory factory = new VariableFactory();

		{
			Collection<Variable<EmptySolution, ?>> variables = factory
					.createFromGettersAndSetters(EmptySolution.class);
			assertTrue(variables.toString(), variables.isEmpty());
		}

		{
			Collection<Variable<DifferentGettersFewSettersSolution, ?>> variables = factory
					.createFromGettersAndSetters(DifferentGettersFewSettersSolution.class);
			assertEquals(variables.toString(), 1, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
		}

		{
			Collection<Variable<DifferentGettersAllSettersSolution, ?>> variables = factory
					.createFromGettersAndSetters(DifferentGettersAllSettersSolution.class);
			assertEquals(variables.toString(), 2, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}

		{
			Collection<Variable<SimilarGettersFewSettersSolution, ?>> variables = factory
					.createFromGettersAndSetters(SimilarGettersFewSettersSolution.class);
			assertEquals(variables.toString(), 1, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
		}

		{
			Collection<Variable<SimilarGettersAllSettersSolution, ?>> variables = factory
					.createFromGettersAndSetters(SimilarGettersAllSettersSolution.class);
			assertEquals(variables.toString(), 3, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
			assertTrue(names.contains("C"));
		}
	}

	@Test
	public void testCreateFromGettersAndConstructorsThrowExceptionIfInterface() {
		VariableFactory factory = new VariableFactory();

		try {
			factory.createFromGettersAndConstructors(EmptySolution.class);
			fail("No exception thrown");
		} catch (IsInterfaceException e) {
		}
	}

	@SuppressWarnings("unused")
	private class NoConstructorSolution {
		public int getA() {
			return 0;
		}

		public Object getB() {
			return 0;
		}
	}

	@SuppressWarnings("unused")
	private class EmptyConstructorSolution {

		public EmptyConstructorSolution() {
		}

		public int getA() {
			return 0;
		}

		public Object getB() {
			return 0;
		}
	}

	@SuppressWarnings("unused")
	private class SmallConstructorSolution {

		private final int a;

		public SmallConstructorSolution(int a) {
			this.a = a;
		}

		public int getA() {
			return a;
		}

		public Object getB() {
			return 0;
		}
	}

	@SuppressWarnings("unused")
	private class FullConstructorSolution {

		private final int a;
		private final Object b;

		public FullConstructorSolution(int a, Object b) {
			this.a = a;
			this.b = b;
		}

		public int getA() {
			return a;
		}

		public Object getB() {
			return b;
		}
	}

	@SuppressWarnings("unused")
	private class ProgressiveConstructorsSolution {

		private final int a;
		private final Object b;

		public ProgressiveConstructorsSolution(int a, Object b) {
			this.a = a;
			this.b = b;
		}

		public ProgressiveConstructorsSolution(int a) {
			this(a, null);
		}

		public ProgressiveConstructorsSolution(Object b) {
			this(0, b);
		}

		public int getA() {
			return a;
		}

		public Object getB() {
			return b;
		}
	}

	@SuppressWarnings("unused")
	private class DifferentConstructorsSolution {

		private final int a;
		private final Object b;

		public DifferentConstructorsSolution(int a) {
			this.a = a;
			this.b = null;
		}

		public DifferentConstructorsSolution(Object b) {
			this.a = 0;
			this.b = b;
		}

		public int getA() {
			return a;
		}

		public Object getB() {
			return b;
		}
	}

	@Test
	public void testCreateFromGettersAndConstructorsRetrievesOnlyGettersWithConstructorArgument() {
		VariableFactory factory = new VariableFactory();

		{
			Collection<Variable<NoConstructorSolution, ?>> variables = factory
					.createFromGettersAndConstructors(NoConstructorSolution.class);
			assertTrue(variables.toString(), variables.isEmpty());
		}

		{
			Collection<Variable<EmptyConstructorSolution, ?>> variables = factory
					.createFromGettersAndConstructors(EmptyConstructorSolution.class);
			assertTrue(variables.toString(), variables.isEmpty());
		}

		{
			Collection<Variable<SmallConstructorSolution, ?>> variables = factory
					.createFromGettersAndConstructors(SmallConstructorSolution.class);
			assertEquals(variables.toString(), 1, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
		}

		{
			Collection<Variable<FullConstructorSolution, ?>> variables = factory
					.createFromGettersAndConstructors(FullConstructorSolution.class);
			assertEquals(variables.toString(), 2, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}

		{
			Collection<Variable<ProgressiveConstructorsSolution, ?>> variables = factory
					.createFromGettersAndConstructors(ProgressiveConstructorsSolution.class);
			assertEquals(variables.toString(), 2, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}

		{
			Collection<Variable<DifferentConstructorsSolution, ?>> variables = factory
					.createFromGettersAndConstructors(DifferentConstructorsSolution.class);
			assertEquals(variables.toString(), 2, variables.size());
			Collection<String> names = retrieveVariableNames(variables);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}
	}

	@SuppressWarnings("unused")
	private class OverlappingConstructorSolution {

		private final int a;
		private final Object b;

		public OverlappingConstructorSolution(int a1, int a2, Object b) {
			this.a = a1 - a2;
			this.b = b;
		}

		public int getA() {
			return a;
		}

		public Object getB() {
			return b;
		}
	}

	@SuppressWarnings("unused")
	private class OverlappingGettersSolution {

		public int getA() {
			return 0;
		}

		public Object getB() {
			return null;
		}

		public int getC() {
			return 0;
		}
	}

	@Test
	public void testCreateFromGettersAndConstructorsThrowExceptionIfOverlappingTypes() {
		VariableFactory factory = new VariableFactory();

		try {
			factory.createFromGettersAndConstructors(OverlappingConstructorSolution.class);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}

		try {
			factory.createFromGettersAndConstructors(OverlappingGettersSolution.class);
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

}
