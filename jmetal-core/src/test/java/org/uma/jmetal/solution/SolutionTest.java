package org.uma.jmetal.solution;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public abstract class SolutionTest<T, S extends Solution<T>> {

  public abstract T createVariable();

  private double createObjective() {
    return JMetalRandom.getInstance().nextDouble();
  }

  public abstract S createSolution();

  @Test
  public void shouldSetVariablesFailIfNullVariables() {
    S solution = createSolution();
    try {
      solution.setVariables(null);
      fail("No exception thrown");
    } catch (NullPointerException | IllegalArgumentException cause) {
      // OK
    }
  }

  @Test
  public void shouldSetVariablesFailIfNotEnoughVariables() {
    S solution = createSolution();
    List<T> expected = IntStream.range(0, solution.getNumberOfVariables() - 1).mapToObj(i -> createVariable())
        .collect(Collectors.toList());
    try {
      solution.setVariables(Collections.unmodifiableList(expected));
      fail("No exception thrown");
    } catch (IllegalArgumentException cause) {
      // OK
    }
  }

  @Test
  public void shouldSetVariablesFailIfTooMuchVariables() {
    S solution = createSolution();
    List<T> expected = IntStream.range(0, solution.getNumberOfVariables() + 1).mapToObj(i -> createVariable())
        .collect(Collectors.toList());
    try {
      solution.setVariables(Collections.unmodifiableList(expected));
      fail("No exception thrown");
    } catch (IllegalArgumentException cause) {
      // OK
    }
  }

  @Test
  public void shouldSetVariablesReplaceAllVariables() {
    S solution = createSolution();
    List<T> expected = IntStream.range(0, solution.getNumberOfVariables()).mapToObj(i -> createVariable())
        .collect(Collectors.toList());
    solution.setVariables(Collections.unmodifiableList(expected));
    List<T> actual = solution.getVariables();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldSetObjectivesFailIfNullObjectives() {
    S solution = createSolution();
    try {
      solution.setObjectives(null);
      fail("No exception thrown");
    } catch (NullPointerException | IllegalArgumentException cause) {
      // OK
    }
  }

  @Test
  public void shouldSetObjectivesFailIfNotEnoughObjectives() {
    S solution = createSolution();
    double[] expected = IntStream.range(0, solution.getNumberOfObjectives() - 1).mapToDouble(i -> createObjective())
        .toArray();
    try {
      solution.setObjectives(expected);
      fail("No exception thrown");
    } catch (IllegalArgumentException cause) {
      // OK
    }
  }

  @Test
  public void shouldSetObjectivesFailIfTooMuchObjectives() {
    S solution = createSolution();
    double[] expected = IntStream.range(0, solution.getNumberOfObjectives() + 1).mapToDouble(i -> createObjective())
        .toArray();
    try {
      solution.setObjectives(expected);
      fail("No exception thrown");
    } catch (IllegalArgumentException cause) {
      // OK
    }
  }

  @Test
  public void shouldSetObjectivesReplaceAllObjectives() {
    S solution = createSolution();
    double[] expected = IntStream.range(0, solution.getNumberOfObjectives()).mapToDouble(i -> createObjective())
        .toArray();
    solution.setObjectives(expected);
    double[] actual = solution.getObjectives();
    assertArrayEquals(expected, actual, 0.0);
  }
}
