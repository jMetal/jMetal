package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 25/11/14.
 */
public class DifferentialEvolutionTest {
  private DifferentialEvolution algorithm ;
  private DoubleProblem problem ;
  private int populationSize = 100 ;
  private int maxEvaluations = 25000 ;
  private static final double EPSILON = 0.000000000000001 ;

  @Before
  public void startup() {
    problem = new DoubleProblem() {
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
    algorithm = new DifferentialEvolution(problem, maxEvaluations, populationSize,
            new DifferentialEvolutionCrossover(),
            new DifferentialEvolutionSelection(),
            new SequentialSolutionListEvaluator()) ;

  }

  @Test
  public void initProgress() {
    algorithm.initProgress();
    assertEquals(populationSize, algorithm.getEvaluations()) ;
  }

  @Test
  public void updateProgress() {
    
  }
}
