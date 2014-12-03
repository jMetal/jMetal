package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.GenericDoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by Antonio J. Nebro on 25/11/14.
 */
@RunWith(MockitoJUnitRunner.class) public class DifferentialEvolutionTest {
  private static final int DEFAULT_POPULATION_SIZE = 100 ;
  private static final int DEFAULT_MAX_EVALUATIONS = 25000 ;
  private DifferentialEvolution algorithm;
  private int populationSize;
  private int maxEvaluations;

  @Mock private DifferentialEvolutionCrossover crossover;

  @Mock private DifferentialEvolutionSelection selection;

  @Mock private SequentialSolutionListEvaluator evaluator;

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

  @Test public void shouldCreateInitialPopulationWhenPopulationSizeIs0() {
    Integer populationSize = 0;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = algorithm.createInitialPopulation();

    assertTrue("Population is not empty.", population.isEmpty());
  }

  @Test public void shouldCreateInitialPopulationWhenPopulationSizeIsBiggerThan0() {
    int populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    DoubleSolution [] expectedSolution = new DoubleSolution[]{
        new MockDoubleSolution(), new MockDoubleSolution(), new MockDoubleSolution()} ;

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
        .<DoubleSolution>asList(new MockDoubleSolution(), new MockDoubleSolution(),
            new MockDoubleSolution());
    List<DoubleSolution> expectedResult = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(), new MockDoubleSolution(),
            new MockDoubleSolution());

    Mockito.when(evaluator.evaluate(population, problem)).thenReturn(expectedResult);

    List<DoubleSolution> result = algorithm.evaluatePopulation(population);
    Mockito.verify(evaluator).evaluate(population, problem) ;
    assertEquals(expectedResult, result);
  }

  @Test public void shouldSelection() {
    List<DoubleSolution> population =
        Arrays.<DoubleSolution>asList(new GenericDoubleSolution(new MockProblem()));

    List<DoubleSolution> offspringPopulation = algorithm.selection(population);
    assertEquals(population, offspringPopulation);
  }






  @Test public void shouldReproduction() {
    Integer populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(), new MockDoubleSolution(),
            new MockDoubleSolution());
    List<DoubleSolution> parents = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(), new MockDoubleSolution());

    List<DoubleSolution> children = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(), new MockDoubleSolution());

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

  @Test public void shouldReplacement() {
    Integer populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(0, 2D), new MockDoubleSolution(0, 3D),
            new MockDoubleSolution(0, 1D));
    List<DoubleSolution> offspringPopulation = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(0, 1D), new MockDoubleSolution(0, 3D),
            new MockDoubleSolution(0, 2D));

    List<DoubleSolution> result = algorithm.replacement(population, offspringPopulation);
    assertEquals("Result size different from expected.", populationSize.intValue(), result.size());
    assertEquals(1D, result.get(0).getObjective(0));
    assertEquals(1D, result.get(1).getObjective(0));
    assertEquals(3D, result.get(2).getObjective(0));
  }

  @Test public void shouldGetResultReturnsThenReturnTheBestIndividual() {

    Integer populationSize = 3;
    ReflectionTestUtils.setField(algorithm, "populationSize", populationSize);

    List<DoubleSolution> population = Arrays
        .<DoubleSolution>asList(new MockDoubleSolution(0, 2D), new MockDoubleSolution(0, 3D),
            new MockDoubleSolution(0, 1D));
    ReflectionTestUtils.setField(algorithm, "population", population);

    DoubleSolution result = algorithm.getResult();
    assertEquals(1D, result.getObjective(0));
  }

  private class MockProblem extends AbstractDoubleProblem {

    @Override public void evaluate(DoubleSolution solution) {
      // Does nothing.
    }
  }


  private class MockDoubleSolution implements DoubleSolution {

    private Double value;

    private Integer index;

    public MockDoubleSolution() {
    }

    public MockDoubleSolution(Integer index, Double value) {
      setObjective(index, value);
    }

    @Override public Double getLowerBound(int index) {
      return null;
    }

    @Override public Double getUpperBound(int index) {
      return null;
    }

    @Override public void setObjective(int index, double value) {
      this.index = index;
      this.value = value;
    }

    @Override public double getObjective(int index) {
      if (index == this.index) {
        return value;
      }
      return 0;
    }

    @Override public Double getVariableValue(int index) {
      return null;
    }

    @Override public void setVariableValue(int index, Double value) {

    }

    @Override public String getVariableValueString(int index) {
      return null;
    }

    @Override public int getNumberOfVariables() {
      return 0;
    }

    @Override public int getNumberOfObjectives() {
      return 0;
    }

    @Override public double getOverallConstraintViolationDegree() {
      return 0;
    }

    @Override public void setOverallConstraintViolationDegree(double violationDegree) {

    }

    @Override public Solution copy() {
      return null;
    }

    @Override public void setAttribute(Object id, Object value) {

    }

    @Override public Object getAttribute(Object id) {
      return null;
    }
  }
}
