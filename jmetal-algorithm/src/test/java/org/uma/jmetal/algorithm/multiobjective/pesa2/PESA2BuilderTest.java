package org.uma.jmetal.algorithm.multiobjective.pesa2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;

/**
 * Created by Antonio J. Nebro on 5/07/19.
 */
public class PESA2BuilderTest {
  private PESA2Builder<DoubleSolution> builder;
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

    builder = new PESA2Builder<DoubleSolution>(problem, crossover, mutation);
  }

  @AfterEach public void cleanup() {
    problem = null;
    builder = null;
  }

  @Test public void buildAlgorithm() {
    PESA2<DoubleSolution> algorithm = builder.build();
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

  @Test public void setNegativeMaxNumberOfIterations() {
    assertThrows(JMetalException.class, () -> builder.setMaxEvaluations(-100));
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
