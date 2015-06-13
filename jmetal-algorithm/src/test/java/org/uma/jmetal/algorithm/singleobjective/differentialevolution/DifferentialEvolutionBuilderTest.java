package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by ajnebro on 25/11/14.
 */
public class DifferentialEvolutionBuilderTest {
  private DifferentialEvolutionBuilder builder;
  private DoubleProblem problem;
  private static final double EPSILON = 0.000000000000001;

  @Before public void startup() {
    problem = mock(DoubleProblem.class);
    builder = new DifferentialEvolutionBuilder(problem);
  }

  @After public void cleanup() {
    problem = null;
    builder = null;
  }

  @Test public void buildAlgorithm() {
    DifferentialEvolution algorithm = builder.build();
    assertNotNull(algorithm);
  }

  @Test public void getProblem() {
    assertEquals(problem, builder.getProblem());
  }

  @Test public void testDefaultConfiguration() {
    assertEquals(100, builder.getPopulationSize());
    assertEquals(25000, builder.getMaxEvaluations());

    DifferentialEvolutionCrossover crossover = builder.getCrossoverOperator();
    assertEquals(0.5, crossover.getCr(), EPSILON);
    assertEquals(0.5, crossover.getF(), EPSILON);
    assertEquals(0.5, crossover.getK(), EPSILON);
    assertTrue("rand/1/bin".equals(crossover.getVariant()));
  }

  @Test public void setValidPopulationSize() {
    builder.setPopulationSize(150);
    assertEquals(150, builder.getPopulationSize());
  }

  @Test(expected = JMetalException.class) public void setNegativePopulationSize() {
    builder.setPopulationSize(-1);
  }

  @Test public void setPositiveMaxNumberOfEvaluations() {
    builder.setMaxEvaluations(200);
    assertEquals(200, builder.getMaxEvaluations());
  }

  @Test(expected = JMetalException.class) public void setNegativeMaxNumberOfEvaluations() {
    builder.setMaxEvaluations(-100);
  }

  @Test public void setNewCrossoverOperator() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();
    assertNotEquals(crossover, builder.getCrossoverOperator());
    builder.setCrossover(crossover);
    assertEquals(crossover, builder.getCrossoverOperator());
  }

  @Test public void setNewSelectionOperator() {
    DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();
    assertNotEquals(selection, builder.getSelectionOperator());
    builder.setSelection(selection);
    assertEquals(selection, builder.getSelectionOperator());
  }

  @Test public void setNewEvaluator() {
    MultithreadedSolutionListEvaluator<DoubleSolution> evaluator =
        new MultithreadedSolutionListEvaluator<DoubleSolution>(2, problem);
    assertNotEquals(evaluator, builder.getSolutionListEvaluator());
    builder.setSolutionListEvaluator(evaluator);
    assertEquals(evaluator, builder.getSolutionListEvaluator());
  }
}
