package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

import static org.junit.Assert.assertEquals;
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

}
