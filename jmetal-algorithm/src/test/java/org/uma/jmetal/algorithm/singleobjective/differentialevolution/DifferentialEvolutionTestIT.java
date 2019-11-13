package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Antonio J. Nebro on 25/11/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class DifferentialEvolutionTestIT {
  private static final int DEFAULT_POPULATION_SIZE = 100 ;
  private static final int DEFAULT_MAX_EVALUATIONS = 25000 ;
  private DifferentialEvolution algorithm;
  private int populationSize;
  private int maxEvaluations;

  @Mock private DifferentialEvolutionCrossover crossover;

  @Mock private DifferentialEvolutionSelection selection;

  @Mock private SequentialSolutionListEvaluator<DoubleSolution> evaluator;

  @Mock private DoubleProblem problem;

  @Before public void startup() {
    populationSize = DEFAULT_POPULATION_SIZE;
    maxEvaluations = DEFAULT_MAX_EVALUATIONS;
    algorithm =
        new DifferentialEvolution(problem, maxEvaluations, populationSize, crossover, selection,
            evaluator);
  }

  @Test public void shouldGetEvaluations() {
    ReflectionTestUtils.setField(algorithm, "evaluations", 15);
    assertEquals(15, algorithm.getEvaluations()) ;
  }

  @Test public void shouldSetEvaluations() {
    algorithm.setEvaluations(15);
    Assert.assertEquals(15, ReflectionTestUtils.getField(algorithm, "evaluations"));
  }

  @Test public void shouldInitProgress() {
    //Integer expectedPopulationSize = 10;
    //ReflectionTestUtils.setField(algorithm, "populationSize", expectedPopulationSize);

    algorithm.initProgress();
    //assertEquals(expectedPopulationSize.intValue(), algorithm.getEvaluations());
    assertEquals(DEFAULT_POPULATION_SIZE, algorithm.getEvaluations()) ;
  }

  @Test public void shouldUpdateProgressWhenFirstIteration() {
    Integer evaluations = DEFAULT_POPULATION_SIZE;
    ReflectionTestUtils.setField(algorithm, "evaluations", evaluations);

    algorithm.updateProgress();
    assertEquals(DEFAULT_POPULATION_SIZE + evaluations, algorithm.getEvaluations());
  }

  @Test public void shouldUpdateProgressWhenAnyIteration() {
    Integer evaluations = 300;
    ReflectionTestUtils.setField(algorithm, "evaluations", evaluations);

    algorithm.updateProgress();
    assertEquals(DEFAULT_POPULATION_SIZE + evaluations, algorithm.getEvaluations());
  }

  @Test public void shouldIsStoppingConditionReachedWhenEvaluationsLesserThanMaxEvaluations() {
    int evaluations = maxEvaluations - 1;
    algorithm.setEvaluations(evaluations);

    assertFalse("Stopping condition reached.", algorithm.isStoppingConditionReached());
  }

  @Test public void shouldIsStoppingConditionReachedWhenEvaluationsEqualToMaxEvaluations() {
    algorithm.setEvaluations(maxEvaluations);
    assertTrue("Stopping condition not reached", algorithm.isStoppingConditionReached());
  }

  @Test public void shouldIsStoppingConditionReachedWhenEvaluationsBiggerThenMaxEvaluations() {
    algorithm.setEvaluations(maxEvaluations + 1);
    assertTrue("Stopping condition not reached", algorithm.isStoppingConditionReached());
  }

  @Test public void shouldCreateInitialPopulationWhenPopulationSizeIsZero() {
    Integer populationSize = 0;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = algorithm.createInitialPopulation();

    assertTrue("Population is not empty.", population.isEmpty());
  }

  @Test public void shouldCreateInitialPopulationWhenPopulationSizeIsBiggerThanZero() {
    int populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    DoubleSolution [] expectedSolution = new DoubleSolution[]{
        mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class)} ;

    Mockito.when(problem.createSolution()).thenReturn(expectedSolution[0], expectedSolution[1], expectedSolution[2]);
    List<DoubleSolution> population = algorithm.createInitialPopulation();
    Mockito.verify(problem, times(3)).createSolution();

    assertEquals("Population size is different from expected.", populationSize, population.size());
    //for (DoubleSolution solution : population) {
    //  Assert.assertEquals("There are null solutions.", expectedSolution, solution);
    //}
    for (int i = 0 ; i < populationSize; i++) {
      assertEquals("Solution is different", expectedSolution[i], population.get(i)) ;
    }
    //assertEquals("There are solutions which are the same.", populationSize, population.size());
  }

  @Test public void shouldEvaluatePopulation() {
    Integer populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(mock(DoubleSolution.class), mock(DoubleSolution.class),
            mock(DoubleSolution.class));
    List<DoubleSolution> expectedResult = Arrays
        .<DoubleSolution>asList(mock(DoubleSolution.class), mock(DoubleSolution.class),
            mock(DoubleSolution.class));

    Mockito.when(evaluator.evaluate(population, problem)).thenReturn(expectedResult);

    List<DoubleSolution> result = algorithm.evaluatePopulation(population);
    Mockito.verify(evaluator).evaluate(population, problem) ;
    assertEquals(expectedResult, result);
  }

  @Test public void shouldSelection() {
    List<DoubleSolution> population =
        Arrays.<DoubleSolution>asList(mock(DoubleSolution.class));

    List<DoubleSolution> offspringPopulation = algorithm.selection(population);
    assertEquals(population, offspringPopulation);
  }

  @Test public void shouldReproduction() {
    Integer populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(mock(DoubleSolution.class), mock(DoubleSolution.class),
            mock(DoubleSolution.class));
    List<DoubleSolution> parents = Arrays
        .<DoubleSolution>asList(mock(DoubleSolution.class), mock(DoubleSolution.class));

    List<DoubleSolution> children = Arrays
        .<DoubleSolution>asList(mock(DoubleSolution.class), mock(DoubleSolution.class));

    Mockito.when(selection.execute(population)).thenReturn(parents);
    Mockito.when(crossover.execute(parents)).thenReturn(children);

    List<DoubleSolution> result = algorithm.reproduction(population);

    Mockito.verify(selection).setIndex(0);
    Mockito.verify(selection).setIndex(1);
    Mockito.verify(selection).setIndex(2);
    Mockito.verify(selection,never()).setIndex(3);
    Mockito.verify(crossover).setCurrentSolution(population.get(0));
    Mockito.verify(crossover).setCurrentSolution(population.get(1));
    Mockito.verify(crossover).setCurrentSolution(population.get(2));

    assertEquals("Reproduction population size is different from expected.", population.size(),
        result.size());

    for (int i = 0; i < populationSize; i++) {
      TestCase.assertEquals(String.format("Result %d different from expected.", i), children.get(0),
          result.get(i));
    }
  }

  //Add different tests checking what happens when the indexes are different from 0



  @Test public void shouldReplacemen2t() {
    int populationSize = 2;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(2.0) ;

    DoubleSolution solution2 = mock(DoubleSolution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(1.0) ;

    DoubleSolution solution3 = mock(DoubleSolution.class) ;
    Mockito.when(solution3.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution3.getObjective(0)).thenReturn(6.0) ;

    DoubleSolution solution4 = mock(DoubleSolution.class) ;
    Mockito.when(solution4.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution4.getObjective(0)).thenReturn(0.5) ;

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(solution1, solution2);
    List<DoubleSolution> offspringPopulation = Arrays
        .<DoubleSolution>asList(solution3, solution4);

    List<DoubleSolution> expectedSolutionList = Arrays
        .<DoubleSolution>asList(solution4, solution1);

    List<DoubleSolution> result = algorithm.replacement(population, offspringPopulation);
    assertEquals("Result size different from expected.", populationSize, result.size());
    assertEquals(expectedSolutionList, result);
  }

  @Test public void shouldGetResultReturnsThenReturnTheBestIndividual() {
    int populationSize = 4;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(2.0) ;

    DoubleSolution solution2 = mock(DoubleSolution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(1.0) ;

    DoubleSolution solution3 = mock(DoubleSolution.class) ;
    Mockito.when(solution3.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution3.getObjective(0)).thenReturn(6.0) ;

    DoubleSolution solution4 = mock(DoubleSolution.class) ;
    Mockito.when(solution4.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution4.getObjective(0)).thenReturn(0.5) ;

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(solution1, solution2, solution3, solution4);

    ReflectionTestUtils.setField(algorithm, "population", population);

    DoubleSolution result = algorithm.getResult();
    assertEquals(solution4, result);
    assertEquals(0.5, result.getObjective(0), 0.0);
  }
}
