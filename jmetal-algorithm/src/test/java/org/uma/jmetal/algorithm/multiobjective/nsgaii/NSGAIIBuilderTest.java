package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

/**
 * Created by Antonio J. Nebro on 25/11/14.
 */
public class NSGAIIBuilderTest {
  private NSGAIIBuilder<DoubleSolution> builder;
  private Problem<DoubleSolution> problem;
  private static final double EPSILON = 0.000000000000001;
  private static final int NUMBER_OF_VARIABLES_OF_THE_MOCKED_PROBLEM = 20;
  private CrossoverOperator<DoubleSolution> crossover;
  private MutationOperator<DoubleSolution> mutation;

  @SuppressWarnings("unchecked")
  @BeforeEach public void startup() {
    problem = mock(Problem.class);
    when(problem.numberOfVariables()).thenReturn(NUMBER_OF_VARIABLES_OF_THE_MOCKED_PROBLEM);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.numberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    int populationSize  = 100 ;
    builder = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize);
  }

  @AfterEach public void cleanup() {
    problem = null;
    builder = null;
  }

  @Test public void buildAlgorithm() {
    NSGAII<DoubleSolution> algorithm = builder.build();
    assertNotNull(algorithm);
  }

  @Test public void getProblem() {
    assertEquals(problem, builder.getProblem());
  }

  @Test public void testDefaultConfiguration() {
    assertEquals(100, builder.getPopulationSize());
    assertEquals(25000, builder.getMaxEvaluations());

    SBXCrossover crossover = (SBXCrossover) builder.getCrossoverOperator();
    assertEquals(0.9, crossover.crossoverProbability(), EPSILON);
    assertEquals(20.0, crossover.distributionIndex(), EPSILON);

    PolynomialMutation mutation = (PolynomialMutation) builder.getMutationOperator();
    assertEquals(1.0 / NUMBER_OF_VARIABLES_OF_THE_MOCKED_PROBLEM, mutation.mutationProbability(),
        EPSILON);
    assertEquals(20.0, mutation.getDistributionIndex(), EPSILON);
  }

  //@Test(expected = JMetalException.class)
  //public void setNegativePopulationSize() {
  //  builder.setPopulationSize(-1);
  //}

  @Test public void setPositiveMaxNumberOfIterations() {
    builder.setMaxEvaluations(20000);
    assertEquals(20000, builder.getMaxEvaluations());
  }

  @Test public void setNegativeMaxNumberOfIterations() {
    assertThrows(JMetalException.class, () -> builder.setMaxEvaluations(-100));
  }

  @Test public void setNewSelectionOperator() {
    @SuppressWarnings("unchecked")
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = mock(SelectionOperator.class);
    assertNotEquals(selection, builder.getSelectionOperator());
    builder.setSelectionOperator(selection);
    assertEquals(selection, builder.getSelectionOperator());
  }

  @Test public void setNullSelectionOperator() {
    assertThrows(JMetalException.class, () -> builder.setSelectionOperator(null));
  }

  @Test public void setNewEvaluator() {
    MultiThreadedSolutionListEvaluator<DoubleSolution> evaluator =
        new MultiThreadedSolutionListEvaluator<DoubleSolution>(2);
    assertNotEquals(evaluator, builder.getSolutionListEvaluator());
    builder.setSolutionListEvaluator(evaluator);
    assertEquals(evaluator, builder.getSolutionListEvaluator());
  }

  @Test public void setNullEvaluator() {
    assertThrows(JMetalException.class, () -> builder.setSolutionListEvaluator(null));
  }
}
