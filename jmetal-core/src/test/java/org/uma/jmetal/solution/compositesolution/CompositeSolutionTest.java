package org.uma.jmetal.solution.compositesolution;

import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CompositeSolutionTest {

  @Test
  public void shouldConstructorCreateAValidNotNullCompositeSolutionComposedOfASolution() {
    DoubleSolution doubleSolution =
            new DefaultDoubleSolution(
                    3, 0, Arrays.asList(Bounds.create(3.0, 5.0)));

    assertNotNull(new CompositeSolution(Arrays.asList(doubleSolution))) ;
  }

  @Test(expected = Exception.class)
  public void shouldConstructorRaiseAnExceptionIfTheNumberOfObjectivesIsIncoherent() {
    DoubleSolution doubleSolution =
            new DefaultDoubleSolution(
                    3, 0, Arrays.asList(Bounds.create(3.0, 5.0)));
    IntegerSolution integerSolution =
            new DefaultIntegerSolution(
                    2, 0, Arrays.asList(Bounds.create(2, 10)));

    new CompositeSolution(Arrays.asList(doubleSolution, integerSolution)) ;
  }

  @Test
  public void
      shouldConstructorCreateAValidCompositeSolutionComposedOfaDoubleAndAnIntegerSolutions() {
    int numberOfObjectives = 2;
    int numberOfConstraints = 1;
    DoubleSolution doubleSolution =
        new DefaultDoubleSolution(
            numberOfObjectives, numberOfConstraints, Arrays.asList(Bounds.create(3.0, 5.0)));
    IntegerSolution integerSolution =
        new DefaultIntegerSolution(
            numberOfObjectives, numberOfConstraints, Arrays.asList(Bounds.create(2, 10)));

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
            numberOfObjectives,
            numberOfConstraints,
            Arrays.asList(Bounds.create(3.0, 5.0), Bounds.create(1.0, 3.0)));
    IntegerSolution integerSolution =
        new DefaultIntegerSolution(
            numberOfObjectives, numberOfConstraints, Arrays.asList(Bounds.create(2, 10)));

    CompositeSolution solution =
        new CompositeSolution(Arrays.asList(doubleSolution, integerSolution));

    CompositeSolution newSolution = new CompositeSolution(solution) ;

    assertEquals(solution.getNumberOfVariables(), newSolution.getNumberOfVariables());
    assertEquals(solution.getNumberOfObjectives(), newSolution.getNumberOfObjectives());
    assertEquals(solution.getNumberOfConstraints(), newSolution.getNumberOfConstraints());

    assertEquals(solution.getVariable(0).variables().size(), newSolution.getVariable(0).variables().size());
    assertEquals(solution.getVariable(1).variables().size(), newSolution.getVariable(1).variables().size());
    assertNotSame(solution.getVariable(0), newSolution.getVariable(0));
    assertNotSame(solution.getVariable(1), newSolution.getVariable(1));

    assertEquals(solution.getVariable(0).variables(), newSolution.getVariable(0).variables());
    assertEquals(solution.getVariable(1).variables(), newSolution.getVariable(1).variables());
  }
}
