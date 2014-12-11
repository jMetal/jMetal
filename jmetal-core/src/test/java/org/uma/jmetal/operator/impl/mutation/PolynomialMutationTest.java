//  PolynomialMutationTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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

package org.uma.jmetal.operator.impl.mutation;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.GenericBinarySolution;
import org.uma.jmetal.solution.impl.GenericDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PolynomialMutationTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double mutationProbability = 0.1 ;
    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, 2.0) ;
    assertEquals(mutationProbability, (Double) ReflectionTestUtils
        .getField(mutation, "mutationProbability"), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double distributionIndex = 15.0 ;
    PolynomialMutation mutation = new PolynomialMutation(0.1, distributionIndex) ;
    assertEquals(distributionIndex, (Double) ReflectionTestUtils
        .getField(mutation, "distributionIndex"), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldMutationConstructorFailWhenPassedANegativeProbabilityValue() {
    double mutationProbability = -0.1 ;
    new PolynomialMutation(mutationProbability, 2.0) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldMutationConstructorFailWhenPassedANegativeDistributionIndex() {
    double distributionIndex = -0.1 ;
    new PolynomialMutation(0.1, distributionIndex) ;
  }

  @Test
  public void shouldGetMutationProbabilityReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;
    assertEquals(0.1, mutation.getMutationProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetDistributionIndexReturnTheRightValue() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 30.0) ;
    assertEquals(30.0, mutation.getDistributionIndex(), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteWithNullParameterThrowAnException() {
    PolynomialMutation mutation = new PolynomialMutation(0.1, 20.0) ;

    mutation.execute(null) ;
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnTheSameSolutionIfItIsNotMutated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(1.0) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    DoubleSolution solution = problem.createSolution() ;
    DoubleSolution oldSolution = (DoubleSolution)solution.copy() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertEquals(oldSolution, solution) ;
    verify(randomGenerator, times(1)).nextDouble();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAValidSolution() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.005, 0.6) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(
        solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).nextDouble();
  }

  @Test
  public void shouldMutateASingleVariableSolutionReturnAnotherValidSolution() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    double mutationProbability = 0.1;
    double distributionIndex = 20.0 ;

    Mockito.when(randomGenerator.nextDouble()).thenReturn(0.005, 0.1) ;

    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, distributionIndex) ;
    DoubleProblem problem = new MockDoubleProblem(1) ;
    DoubleSolution solution = problem.createSolution() ;

    ReflectionTestUtils.setField(mutation, "randomGenerator", randomGenerator);

    mutation.execute(solution) ;

    assertThat(solution.getVariableValue(0), Matchers.greaterThanOrEqualTo(solution.getLowerBound(0))) ;
    assertThat(solution.getVariableValue(0), Matchers.lessThanOrEqualTo(solution.getUpperBound(0))) ;
    verify(randomGenerator, times(2)).nextDouble();
  }

  /**
   * Mock class representing a binary problem
   */
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

    @Override
    public DoubleSolution createSolution() {
      return new GenericDoubleSolution(this) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}
