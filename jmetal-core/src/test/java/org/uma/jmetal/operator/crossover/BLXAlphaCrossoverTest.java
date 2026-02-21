package org.uma.jmetal.operator.crossover;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

/**
 * Note: this class does check that the BLX-aplha crossover operator does not return invalid
 * values, but not that it works properly (@see BLXAlphaCrossoverWorkingTest)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class BLXAlphaCrossoverTest {
  private static final double EPSILON = 0.00000000000001 ;

  @Test
  public void shouldConstructorAssignTheCorrectProbabilityValue() {
    double crossoverProbability = 0.1 ;
    double alpha = 0.5 ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha) ;
    assertEquals(crossoverProbability, crossover.crossoverProbability(), EPSILON) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectDistributionIndex() {
    double alpha = 0.5 ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, alpha) ;
    assertEquals(alpha, crossover.alpha(), EPSILON) ;
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
    double crossoverProbability = -1.1 ;
    assertThrows(InvalidProbabilityValueException.class, () -> new BLXAlphaCrossover(crossoverProbability, 1.0));
  }

  @Test
  public void shouldConstructorFailWhenPassedANegativeAlphaValue() {
    double alpha = -0.1 ;
    assertThrows(InvalidConditionException.class, () -> new BLXAlphaCrossover(0.1, alpha));
  }

  @Test
  public void shouldGetProbabilityReturnTheRightValue() {
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, 0.5) ;
    assertEquals(0.1, crossover.crossoverProbability(), EPSILON) ;
  }

  @Test
  public void shouldGetAlphaReturnTheRightValue() {
    double alpha = 0.75 ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, alpha) ;
    assertEquals(alpha, crossover.alpha(), EPSILON) ;
  }

  @Test
  public void shouldExecuteWithNullParameterThrowAnException() {
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, 20.0) ;
    assertThrows(NullParameterException.class, () -> crossover.execute(null));
  }

  @Test
  public void shouldExecuteWithInvalidSolutionListSizeThrowAnException() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(0.1, 0.1) ;

    assertThrows(InvalidConditionException.class,
        () -> crossover.execute(
            Arrays.asList(problem.createSolution(), problem.createSolution(), problem.createSolution())));
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfProbabilityIsZero() {
    double crossoverProbability = 0.0;
    double alpha = 0.25 ;

    RandomGenerator<Double> randomGenerator = () -> 1.0 ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), newSolutions.get(0)) ;
    assertEquals(solutions.get(1), newSolutions.get(1)) ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfNotCrossoverIsApplied() {
    @SuppressWarnings("unchecked")
    RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;

    double crossoverProbability = 0.9;
    double alpha = 0.3 ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(1.0) ;

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0), newSolutions.get(0)) ;
    assertEquals(solutions.get(1), newSolutions.get(1)) ;
    verify(randomGenerator).getRandomValue() ;
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsReturnValidSolutions() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double alpha = 0.35 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2, 0.6) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
      problem.createSolution()) ;

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    Bounds<Double> bounds0 = solutions.get(0).getBounds(0);
    Bounds<Double> bounds1 = solutions.get(1).getBounds(0);
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds0.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds1.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds0.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds1.getLowerBound())) ;
    verify(randomGenerator, times(3)).getRandomValue();
  }

  @Test
  public void shouldCrossingTwoSingleVariableSolutionsWithSimilarValueReturnTheSameVariables() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double alpha = 0.45;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2, 0.3) ;
    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0) ;
    List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;
    solutions.get(0).variables().set(0, 1.0);
    solutions.get(1).variables().set(0, 1.0);

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    assertEquals(solutions.get(0).variables().get(0), newSolutions.get(0).variables().get(0), EPSILON) ;
    assertEquals(solutions.get(1).variables().get(0), newSolutions.get(1).variables().get(0), EPSILON) ;
    verify(randomGenerator, times(3)).getRandomValue();
  }

  @Test
  public void shouldCrossingTwoDoubleVariableSolutionsReturnValidSolutions() {
    @SuppressWarnings("unchecked")
	RandomGenerator<Double> randomGenerator = mock(RandomGenerator.class) ;
    double crossoverProbability = 0.9;
    double alpha = 0.35 ;

    Mockito.when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2, 0.8, 0.3, 0.2) ;

    BLXAlphaCrossover crossover = new BLXAlphaCrossover(crossoverProbability, alpha, new RepairDoubleSolutionWithBoundValue(), randomGenerator) ;
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.variables().set(0, 1.0);
    solution1.variables().set(1, 2.0);
    solution2.variables().set(0, 2.0);
    solution2.variables().set(1, 1.0);
    List<DoubleSolution> solutions = Arrays.asList(solution1, solution2) ;

    List<DoubleSolution> newSolutions = crossover.execute(solutions) ;

    Bounds<Double> bounds0 = solutions.get(0).getBounds(0);
    Bounds<Double> bounds1 = solutions.get(1).getBounds(0);
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds0.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds1.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .lessThanOrEqualTo(bounds0.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(0), Matchers
        .greaterThanOrEqualTo(bounds1.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(1), Matchers
        .greaterThanOrEqualTo(bounds0.getLowerBound())) ;
    assertThat(newSolutions.get(0).variables().get(1), Matchers
        .lessThanOrEqualTo(bounds1.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(1), Matchers
        .lessThanOrEqualTo(bounds0.getUpperBound())) ;
    assertThat(newSolutions.get(1).variables().get(1), Matchers
        .greaterThanOrEqualTo(bounds1.getLowerBound())) ;
    verify(randomGenerator, times(5)).getRandomValue();
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double crossoverProbability = 0.1;
		int alpha = 20;
		RepairDoubleSolutionWithBoundValue solutionRepair = new RepairDoubleSolutionWithBoundValue();

    List<Bounds<Double>> bounds = List.of(Bounds.create(0.0, 1.0)) ;

		List<DoubleSolution> solutions = new LinkedList<>();
		solutions.add(new DefaultDoubleSolution(bounds, 2, 0));
		solutions.add(new DefaultDoubleSolution(bounds, 2, 0));

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

    new BLXAlphaCrossover(crossoverProbability, alpha, solutionRepair).execute(solutions);
    assertTrue(defaultUses[0] > 0, "No use of the default generator");

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
    new BLXAlphaCrossover(crossoverProbability, alpha, solutionRepair, () -> {
        customUses[0]++;
        return new Random().nextDouble();
      }).execute(solutions);
    assertTrue(defaultUses[0] == 0, "Default random generator used");
    assertTrue(customUses[0] > 0, "No use of the custom generator");
	}
}
