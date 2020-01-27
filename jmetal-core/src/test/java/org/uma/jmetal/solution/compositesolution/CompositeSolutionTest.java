package org.uma.jmetal.solution.compositesolution;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CompositeSolutionTest {

  @Test
  public void shouldConstructorCreateAValidNotNullCompositeSolutionComposedOfASolution() {
    DoubleSolution doubleSolution =
            new DefaultDoubleSolution(
                    Arrays.asList(new MutablePair<>(3.0, 5.0)), 3, 0);

    assertNotNull(new CompositeSolution(Arrays.asList(doubleSolution))) ;
  }

  @Test(expected = Exception.class)
  public void shouldConstructorRaiseAnExceptionIfTheNumberOfObjectivesIsIncoherent() {
    DoubleSolution doubleSolution =
            new DefaultDoubleSolution(
                    Arrays.asList(new MutablePair<>(3.0, 5.0)), 3, 0);
    IntegerSolution integerSolution =
            new DefaultIntegerSolution(
                    Arrays.asList(new MutablePair<>(2, 10)), 2, 0);

    new CompositeSolution(Arrays.asList(doubleSolution, integerSolution)) ;
  }

  @Test
  public void
      shouldConstructorCreateAValidCompositeSolutionComposedOfaDoubleAndAnIntegerSolutions() {
    int numberOfObjectives = 2;
    int numberOfConstraints = 1;
    DoubleSolution doubleSolution =
        new DefaultDoubleSolution(
            Arrays.asList(new MutablePair<>(3.0, 5.0)), numberOfObjectives, numberOfConstraints);
    IntegerSolution integerSolution =
        new DefaultIntegerSolution(
            Arrays.asList(new MutablePair<>(2, 10)), numberOfObjectives, numberOfConstraints);

    CompositeSolution solution =
        new CompositeSolution(Arrays.asList(doubleSolution, integerSolution));
    assertNotNull(solution);
    assertEquals(2, solution.getNumberOfVariables());
    assertEquals(numberOfObjectives, solution.getNumberOfObjectives());
    assertEquals(numberOfConstraints, solution.getNumberOfConstraints());
    assertEquals(numberOfObjectives, solution.getVariable(0).getNumberOfObjectives());
    assertEquals(numberOfObjectives, solution.getVariable(1).getNumberOfObjectives());
    assertEquals(numberOfConstraints, solution.getVariable(0).getNumberOfConstraints());
    assertEquals(numberOfConstraints, solution.getVariable(1).getNumberOfConstraints());
    assertTrue(solution.getVariable(0) instanceof DoubleSolution);
    assertTrue(solution.getVariable(1) instanceof IntegerSolution);
  }

  @Test
  public void shouldCopyConstructorWorkProperly() {
    int numberOfObjectives = 2;
    int numberOfConstraints = 1;
    DoubleSolution doubleSolution =
        new DefaultDoubleSolution(
            Arrays.asList(new MutablePair<>(3.0, 5.0), new MutablePair<>(1.0, 3.0)),
            numberOfObjectives,
            numberOfConstraints);
    IntegerSolution integerSolution =
        new DefaultIntegerSolution(
            Arrays.asList(new MutablePair<>(2, 10)), numberOfObjectives, numberOfConstraints);

    CompositeSolution solution =
        new CompositeSolution(Arrays.asList(doubleSolution, integerSolution));

    CompositeSolution newSolution = new CompositeSolution(solution) ;

    assertEquals(solution.getNumberOfVariables(), newSolution.getNumberOfVariables());
    assertEquals(solution.getNumberOfObjectives(), newSolution.getNumberOfObjectives());
    assertEquals(solution.getNumberOfConstraints(), newSolution.getNumberOfConstraints());

    assertEquals(solution.getVariable(0).getNumberOfVariables(), newSolution.getVariable(0).getNumberOfVariables());
    assertEquals(solution.getVariable(1).getNumberOfVariables(), newSolution.getVariable(1).getNumberOfVariables());
    assertNotSame(solution.getVariable(0), newSolution.getVariable(0));
    assertNotSame(solution.getVariable(1), newSolution.getVariable(1));

    assertEquals(solution.getVariable(0).getVariables(), newSolution.getVariable(0).getVariables());
    assertEquals(solution.getVariable(1).getVariables(), newSolution.getVariable(1).getVariables());
  }
}
