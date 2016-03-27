//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.operator.impl.crossover;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Note: this class does check that the BLX-aplha crossover operator does not return invalid
 * values, but not that it works properly (@see BLXAlphaCrossoverWorkingTest)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class BLXAlphaCrossoverTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double crossoverProbability = 0.1 ;
    double alpha = 0.5 ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    assertEquals(crossoverProbability, (Double) ReflectionTestUtils
        .getField(crossover, "crossoverProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double alpha = 0.5 ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, alpha) ;
    assertEquals(alpha, (Double) ReflectionTestUtils
        .getField(crossover, "alpha"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double crossoverProbability = -0.1 ;
    new BLXAlphaCrossover(crossoverProbability, 2.0) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructorFailWhenPassedANegativeAlphaValue() {
    double alpha = -0.1 ;
    new BLXAlphaCrossover(0.1, alpha) ;
  }

  @Test
  public void shouldGetProbabilityReturnTheRightValue() {
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, 0.5) ;
    assertEquals(0.1, crossover.getCrossoverProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetAlphaReturnTheRightValue() {
    double alpha = 0.75 ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, alpha) ;
    assertEquals(alpha, crossover.getAlpha(), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, 20.0) ;

    crossover.execute(null) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithInvalidSolutionListSizeThrowAnException() {
    DoubleProblem problem = new MockDoubleProblem(1) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, 0.1) ;

    crossover.execute(
        Arrays.asList(problem.createSolution(), problem.createSolution(), problem.createSolution())) ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfProbabilityIsZero() {
    double crossoverProbability = 0.0;
    double alpha = 0.25 ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), newSolutions.get(0)) ;
    assertEquals(solutions.get(1), newSolutions.get(1)) ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfNotCrossoverIsApplied() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;

    double crossoverProbability = 0.9;
    double alpha = 0.3 ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(1.0) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);
    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), newSolutions.get(0)) ;
    assertEquals(solutions.get(1), newSolutions.get(1)) ;
    verify(randomGenerator).nextDouble() ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnValidSolutions() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;
    double alpha = 0.35 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.2, 0.2, 0.6) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertThat(newSolutions.get(0).getVariableValue(0), Matchers
        .greaterThanOrEqualTo(solutions.get(0).getLowerBound(0))) ;
    assertThat(newSolutions.get(0).getVariableValue(0), Matchers
        .lessThanOrEqualTo(solutions.get(1).getUpperBound(0))) ;
    assertThat(newSolutions.get(1).getVariableValue(0), Matchers
        .lessThanOrEqualTo(solutions.get(0).getUpperBound(0))) ;
    assertThat(newSolutions.get(1).getVariableValue(0), Matchers
        .greaterThanOrEqualTo(solutions.get(1).getLowerBound(0))) ;
    verify(randomGenerator, times(3)).nextDouble();
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsWithSimilarValueReturnTheSameVariables() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;
    double alpha = 0.45;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.2, 0.2, 0.3) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;
    solutions.get(0).setVariableValue(0, 1.0);
    solutions.get(1).setVariableValue(0, 1.0);

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).getVariableValue(0), newSolutions.get(0).getVariableValue(0), EPSILON) ;
    assertEquals(solutions.get(1).getVariableValue(0), newSolutions.get(1).getVariableValue(0), EPSILON) ;
    verify(randomGenerator, times(3)).nextDouble();
  }

  @Test
  public void shouldCrossingTwoDoubleVariableSolutionsReturnValidSolutions() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double crossoverProbability = 0.9;
    double alpha = 0.35 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.2, 0.2, 0.8, 0.3, 0.2) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    DoubleProblem problem = new MockDoubleProblem(2) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.setVariableValue(0, 1.0);
    solution1.setVariableValue(1, 2.0);
    solution2.setVariableValue(0, 2.0);
    solution2.setVariableValue(1, 1.0);
    List<DoubleSolution> solutions = Arrays.asList(solution1, solution2) ;

    ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertThat(newSolutions.get(0).getVariableValue(0), Matchers
        .greaterThanOrEqualTo(solutions.get(0).getLowerBound(0))) ;
    assertThat(newSolutions.get(0).getVariableValue(0), Matchers
        .lessThanOrEqualTo(solutions.get(1).getUpperBound(0))) ;
    assertThat(newSolutions.get(1).getVariableValue(0), Matchers
        .lessThanOrEqualTo(solutions.get(0).getUpperBound(0))) ;
    assertThat(newSolutions.get(1).getVariableValue(0), Matchers
        .greaterThanOrEqualTo(solutions.get(1).getLowerBound(0))) ;
    assertThat(newSolutions.get(0).getVariableValue(1), Matchers
        .greaterThanOrEqualTo(solutions.get(0).getLowerBound(0))) ;
    assertThat(newSolutions.get(0).getVariableValue(1), Matchers
        .lessThanOrEqualTo(solutions.get(1).getUpperBound(0))) ;
    assertThat(newSolutions.get(1).getVariableValue(1), Matchers
        .lessThanOrEqualTo(solutions.get(0).getUpperBound(0))) ;
    assertThat(newSolutions.get(1).getVariableValue(1), Matchers
        .greaterThanOrEqualTo(solutions.get(1).getLowerBound(0))) ;
    verify(randomGenerator, times(5)).nextDouble();
  }

  /**
   * Mock class representing a double problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}
