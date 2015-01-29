package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Antonio J. Nebro on 25/11/14.
 */
public class NSGAIIBuilderTest {
  private NSGAIIBuilder builder;
  private Problem problem;
  private static final double EPSILON = 0.000000000000001;
  private static final int NUMBER_OF_VARIABLES_OF_THE_MOCKED_PROBLEM = 20;

  @Before public void startup() {
    problem = mock(Problem.class);
    when(problem.getNumberOfVariables()).thenReturn(NUMBER_OF_VARIABLES_OF_THE_MOCKED_PROBLEM);

    builder = new NSGAIIBuilder(problem);
  }

  @After public void cleanup() {
    problem = null;
    builder = null;
  }

  @Test public void buildAlgorithm() {
    NSGAII algorithm = (NSGAII) builder.build();
    assertNotNull(algorithm);
  }

  @Test public void getProblem() {
    assertEquals(problem, builder.getProblem());
  }

  @Test public void testDefaultConfiguration() {
    assertEquals(100, builder.getPopulationSize());
    assertEquals(250, builder.getMaxIterations());

    SBXCrossover crossover = (SBXCrossover) builder.getCrossoverOperator();
    assertEquals(0.9, crossover.getCrossoverProbability(), EPSILON);
    assertEquals(20.0, crossover.getDistributionIndex(), EPSILON);

    PolynomialMutation mutation = (PolynomialMutation) builder.getMutationOperator();
    assertEquals(1.0 / NUMBER_OF_VARIABLES_OF_THE_MOCKED_PROBLEM, mutation.getMutationProbability(),
        EPSILON);
    assertEquals(20.0, mutation.getDistributionIndex(), EPSILON);
  }

  @Test public void setValidPopulationSize() {
    builder.setPopulationSize(150);
    assertEquals(150, builder.getPopulationSize());
  }

  @Test(expected = JMetalException.class) public void setNegativePopulationSize() {
    builder.setPopulationSize(-1);
  }

  @Test public void setPositiveMaxNumberOfIterations() {
    builder.setMaxIterations(200);
    assertEquals(200, builder.getMaxIterations());
  }

  @Test(expected = JMetalException.class) public void setNegativeMaxNumberOfIterations() {
    builder.setMaxIterations(-100);
  }

  @Test public void setNewCrossoverOperator() {
    CrossoverOperator crossover = new SBXCrossover(0.9, 20.0);
    assertNotEquals(crossover, builder.getCrossoverOperator());
    builder.setCrossoverOperator(crossover);
    assertEquals(crossover, builder.getCrossoverOperator());
  }

  @Test(expected = JMetalException.class) public void setNullCrossoverOperator() {
    builder.setCrossoverOperator(null);
  }

  @Test public void setNewMutationOperator() {
    MutationOperator mutation = new PolynomialMutation(0.9, 20.0);
    assertNotEquals(mutation, builder.getMutationOperator());
    builder.setMutationOperator(mutation);
    assertEquals(mutation, builder.getMutationOperator());
  }

  @Test(expected = JMetalException.class) public void setNullMutationOperator() {
    builder.setMutationOperator(null);
  }

  @Test public void setNewSelectionOperator() {
    SelectionOperator selection = mock(SelectionOperator.class);
    assertNotEquals(selection, builder.getSelectionOperator());
    builder.setSelectionOperator(selection);
    assertEquals(selection, builder.getSelectionOperator());
  }

  @Test(expected = JMetalException.class) public void setNullSelectionOperator() {
    builder.setSelectionOperator(null);
  }

  @Test public void setNewEvaluator() {
    MultithreadedSolutionListEvaluator evaluator =
        new MultithreadedSolutionListEvaluator(2, problem);
    assertNotEquals(evaluator, builder.getSolutionListEvaluator());
    builder.setSolutionListEvaluator(evaluator);
    assertEquals(evaluator, builder.getSolutionListEvaluator());
  }

  @Test(expected = JMetalException.class) public void setNullEvaluator() {
    builder.setSolutionListEvaluator(null);
  }
}
