package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.parallel.impl.MultithreadedEvaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ajnebro on 25/11/14.
 */
public class DifferentialEvolutionBuilderTest {
  private DifferentialEvolutionBuilder builder ;
  private DoubleProblem problem ;
  private static final double EPSILON = 0.000000000000001 ;

  @Before
  public void startup() {
    problem =  new DoubleProblem() {
      @Override
      public Double getLowerBound(int index) {
        return null;
      }

      @Override
      public Double getUpperBound(int index) {
        return null;
      }

      @Override
      public DoubleSolution createSolution() {
        return null;
      }

      @Override
      public int getNumberOfVariables() {
        return 0;
      }

      @Override
      public int getNumberOfObjectives() {
        return 0;
      }

      @Override
      public int getNumberOfConstraints() {
        return 0;
      }

      @Override
      public String getName() {
        return null;
      }

      @Override
      public void evaluate(DoubleSolution solution) {

      }
    } ;
    builder = new DifferentialEvolutionBuilder(problem) ;
  }

  @Test
  public void testDefaultConfiguration() {
    assertEquals(100, builder.getPopulationSize()) ;
    assertEquals(25000, builder.getMaxEvaluations()) ;

    DifferentialEvolutionCrossover crossover = builder.getCrossoverOperator() ;
    assertEquals(0.5, crossover.getCr(), EPSILON) ;
    assertEquals(0.5, crossover.getF(), EPSILON) ;
    assertEquals(0.5, crossover.getK(), EPSILON) ;
    assertTrue("rand/1/bin".equals(crossover.getVariant())) ;
  }

  @Test
  public void setValidPopulationSize() {
    builder.setPopulationSize(150) ;
    assertEquals(150, builder.getPopulationSize());
  }

  @Test (expected = JMetalException.class)
  public void setNegativePopulationSize() {
    builder.setPopulationSize(-1) ;
  }

  @Test
  public void setPositiveMaxNumberOfEvaluations() {
    builder.setMaxEvaluations(200) ;
    assertEquals(200, builder.getMaxEvaluations());
  }

  @Test (expected = JMetalException.class)
  public void setNegativeMaxNumberOfEvaluations() {
    builder.setMaxEvaluations(-100) ;
  }

  @Test
  public void setNewCrossoverOperator() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover() ;
    assertNotEquals(crossover, builder.getCrossoverOperator());
    builder.setCrossover(crossover) ;
    assertEquals(crossover, builder.getCrossoverOperator()) ;
  }

  @Test
  public void setNewSelectionOperator() {
    DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection() ;
    assertNotEquals(selection, builder.getSelectionOperator());
    builder.setSelection(selection) ;
    assertEquals(selection, builder.getSelectionOperator()) ;
  }

  @Test
  public void setNewEvaluator() {
    MultithreadedSolutionListEvaluator evaluator = new MultithreadedSolutionListEvaluator(2, problem) ;
    assertNotEquals(evaluator, builder.getEvaluator());
    builder.setEvaluator(evaluator) ;
    assertEquals(evaluator, builder.getEvaluator()) ;
  }
}
