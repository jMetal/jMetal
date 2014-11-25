package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    problem = mock(DoubleProblem.class) ;
    /*
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
    */
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
    DoubleProblem problem = mock(DoubleProblem.class) ;
    algorithm = new DifferentialEvolution(problem, maxEvaluations, populationSize,
            new DifferentialEvolutionCrossover(),
            new DifferentialEvolutionSelection(),
            new SequentialSolutionListEvaluator()) ;

    when(problem.createSolution()).thenReturn(mock(DoubleSolution.class)) ;

    List<DoubleSolution> population = algorithm.createInitialPopulation() ;

    assertNotNull(population.get(0)) ;
    assertNotNull(population.get(populationSize - 1)) ;
    assertNotEquals(population.get(0), population.get(populationSize-1));
  }

  @Test
  public void evaluationOfTheInitialPopulation() {
    DoubleProblem problem = mock(DoubleProblem.class) ;
    SolutionListEvaluator evaluator = mock(SolutionListEvaluator.class) ;
    algorithm = new DifferentialEvolution(problem, maxEvaluations, populationSize,
            new DifferentialEvolutionCrossover(),
            new DifferentialEvolutionSelection(),
            evaluator) ;

    //when(problem.evaluate(any(DoubleSolution.class))).thenReturn(mock(DoubleSolution.class)) ;

    /*
    when(problem.createSolution()).thenReturn(new Answer<DoubleSolution>() {
      DoubleSolution sol = new GenericDoubleSolution(problem) ; ;

      public DoubleSolution answer(InvocationOnMock invocation) throws Throwable {
        return sol;
      }
    }) ;
    */

   /*
    Answer<DoubleSolution> answer = new Answer<DoubleSolution>() {
      private DoubleSolution solution = new GenericDoubleSolution(problem) ;
      @Override
      public DoubleSolution answer(InvocationOnMock invocationOnMock) throws Throwable {
        return mock(DoubleSolution.class);
      }
    } ;

    when(problem.createSolution()).thenReturn(answer) ;
    */

    when(problem.createSolution()).thenReturn(mock(DoubleSolution.class)) ;
    //when(evaluator.evaluate(Matchers.anyListOf(Solution.class),problem)).thenReturn(DoubleSolution.class);

    List<DoubleSolution> population1 = algorithm.createInitialPopulation() ;
    List<DoubleSolution> population2 = algorithm.evaluatePopulation(population1) ;
    assertEquals(population1.size(), population2.size());

    assertEquals(population1.get(0), population2.get(0));
    assertNotEquals(population1.get(0), population2.get(1));
    assertEquals(population1.get(populationSize-1), population2.get(populationSize-1));
  }

  @Test
  public void selection() {
    //List<DoubleSolution> population = algorithm.selection(algorithm.getPopulation()) ;
    //assertEquals(population, algorithm.getPopulation()) ;
  }
}
