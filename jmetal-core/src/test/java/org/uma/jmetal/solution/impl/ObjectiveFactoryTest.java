package org.uma.jmetal.solution.impl;

import org.junit.Test;
import org.uma.jmetal.solution.SolutionEvaluator.Objective;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObjectiveFactoryTest {

	private <Solution> Collection<String> retrieveObjectiveNames(
			Collection<Objective<Solution, ?>> objectives) {
		Collection<String> names = new LinkedList<>();
		for (Objective<Solution, ?> objective : objectives) {
			names.add(objective.getName());
		}
		return names;
	}

	private class FakeObjective implements Objective<Object, Object> {

		private final String name;
		private final Object value;

		public FakeObjective(String name, Object value) {
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
	public void testRetrieveObjectiveNames() {
		Objective<Object, ?> objective1 = new FakeObjective("a", null);
		Objective<Object, ?> objective2 = new FakeObjective("b", null);
		Objective<Object, ?> objective3 = new FakeObjective("c", null);
		Collection<Objective<Object, ?>> objectives = Arrays.asList(objective1,
				objective2, objective3);

		Collection<String> names = retrieveObjectiveNames(objectives);
		assertEquals(names.toString(), 3, names.size());
		assertTrue(names.contains(objective1.getName()));
		assertTrue(names.contains(objective2.getName()));
		assertTrue(names.contains(objective3.getName()));
	}

	private interface EmptySolution {
	}

	private interface GettersFewSettersSolution {
		public int getA();

		public void setA(int a);

		public Object getB();
	}

	private interface GettersAllSettersSolution {
		public int getA();

		public void setA(int a);

		public Object getB();

		public void setB(Object b);

	}

	@Test
	public void testCreateFromGettersRetrievesAllGetters() {
		ObjectiveFactory factory = new ObjectiveFactory();

		{
			Collection<Objective<EmptySolution, ?>> objectives = factory
					.createFromGetters(EmptySolution.class);
			assertTrue(objectives.toString(), objectives.isEmpty());
		}

		{
			Collection<Objective<GettersFewSettersSolution, ?>> objectives = factory
					.createFromGetters(GettersFewSettersSolution.class);
			assertEquals(objectives.toString(), 2, objectives.size());
			Collection<String> names = retrieveObjectiveNames(objectives);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}

		{
			Collection<Objective<GettersAllSettersSolution, ?>> objectives = factory
					.createFromGetters(GettersAllSettersSolution.class);
			assertEquals(objectives.toString(), 2, objectives.size());
			Collection<String> names = retrieveObjectiveNames(objectives);
			assertTrue(names.contains("A"));
			assertTrue(names.contains("B"));
		}
	}

	@Test
	public void testCreateFromGettersWithoutSettersRetrievesOnlyGettersWithoutSetters() {
		ObjectiveFactory factory = new ObjectiveFactory();

		{
			Collection<Objective<EmptySolution, ?>> variables = factory
					.createFromGettersWithoutSetters(EmptySolution.class);
			assertTrue(variables.toString(), variables.isEmpty());
		}

		{
			Collection<Objective<GettersFewSettersSolution, ?>> variables = factory
					.createFromGettersWithoutSetters(GettersFewSettersSolution.class);
			assertEquals(variables.toString(), 1, variables.size());
			Collection<String> names = retrieveObjectiveNames(variables);
			assertTrue(names.contains("B"));
		}

		{
			Collection<Objective<GettersAllSettersSolution, ?>> variables = factory
					.createFromGettersWithoutSetters(GettersAllSettersSolution.class);
			assertEquals(variables.toString(), 0, variables.size());
		}
	}

}
