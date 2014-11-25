package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.GenericDoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
        return new GenericDoubleSolution(this) ;
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
  public void updateProgressFirstIteration() {
    algorithm.initProgress();
    algorithm.updateProgress();
    assertEquals(populationSize * 2, algorithm.getEvaluations());
  }

  @Test
  public void updateProgressSecondIteration() {
    algorithm.initProgress();
    algorithm.updateProgress();
    assertEquals(populationSize * 2, algorithm.getEvaluations());
    algorithm.updateProgress();
    assertEquals(populationSize * 3, algorithm.getEvaluations());

    algorithm.setEvaluations(20000);
    algorithm.updateProgress();
    assertEquals(20000+populationSize, algorithm.getEvaluations());

  }

  @Test
  public void updateProgress() {
    algorithm.setEvaluations(20000);
    algorithm.updateProgress();
    assertEquals(20000+populationSize, algorithm.getEvaluations());

  }

  @Test
  public void stoppingConditionNotReachedByOneEvaluation() {
    int value = maxEvaluations - 1 ;
    algorithm.setEvaluations(value);
    assertFalse(algorithm.isStoppingConditionReached());
  }

  @Test
  public void stoppingConditionReached() {
    algorithm.setEvaluations(maxEvaluations);
    assertTrue(algorithm.isStoppingConditionReached());

    algorithm.setEvaluations(maxEvaluations++);
    assertTrue(algorithm.isStoppingConditionReached());
  }

  @Test
  public void theInitialPopulationHasTheRightSize() {
    List<DoubleSolution> population = algorithm.createInitialPopulation() ;

    assertEquals(population.size(), populationSize) ;
  }

  @Test
  public void theInitialPopulationHasInstantiatedSolutions() {
    List<DoubleSolution> population = algorithm.createInitialPopulation() ;

    assertNotNull(population.get(0)) ;
    assertNotNull(population.get(populationSize-1)) ;
  }

  @Test
  public void evaluationOfTheInitialPopulation() {
    algorithm.createInitialPopulation() ;
    List<DoubleSolution> population = algorithm.evaluatePopulation(algorithm.getPopulation()) ;
  }

  /*

    @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    List<DoubleSolution> pop = evaluator.evaluate(population, problem) ;

    return pop ;
  }
   */
}
