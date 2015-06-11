package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ajnebro on 11/6/15.
 */
public class ABYSSTest {

  AbstractABYSS<DoubleSolution> abyss ;
  DoubleProblem problem ;

  @Before
  public void setup() {
    problem = new MockProblem();
  }

  @Test
  public void shouldIsStoppingConditionReachedReturnTrueIfTheConditionFulfills() {
    int maxEvaluations = 100 ;
    abyss = new ABYSS(problem, maxEvaluations, 0, 0, 0, 0, null, null, null, 0) ;

    ReflectionTestUtils.setField(abyss, "evaluations", 101);

    assertTrue(abyss.isStoppingConditionReached()) ;
  }

  @Test
  public void shouldIsStoppingConditionReachedReturnFalseIfTheConditionDoesNotFulfill() {
    int maxEvaluations = 100 ;
    abyss = new ABYSS(problem, maxEvaluations, 0, 0, 0, 0, null, null, null, 0) ;

    ReflectionTestUtils.setField(abyss, "evaluations", 1);

    assertFalse(abyss.isStoppingConditionReached()) ;
  }

  @Test
  public void shouldInitializationPhaseLeadToAPopulationFilledWithEvaluatedSolutions() {
    int populationSize = 20 ;
    int numberOfSubRanges = 4 ;
    DoubleProblem problem = new MockProblem() ;
    abyss = new ABYSS(problem, 0, populationSize, 0, 0, 0, null, null, null, numberOfSubRanges) ;

    abyss.initializationPhase() ;
    assertEquals(populationSize, abyss.getPopulation().size()) ;
    assertEquals(populationSize, abyss.evaluations);
  }



  private class MockProblem extends AbstractDoubleProblem {
    private JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

    public MockProblem() {
      setNumberOfVariables(3);
      setNumberOfObjectives(2);
      setNumberOfConstraints(0);
      setName("Fonseca");

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override public int getNumberOfVariables() {
      return 3;
    }

    @Override public int getNumberOfObjectives() {
      return 2;
    }

    @Override public int getNumberOfConstraints() {
      return 0;
    }

    @Override public String getName() {
      return null;
    }

    @Override public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, randomGenerator.nextDouble());
      solution.setObjective(1, randomGenerator.nextDouble());
    }

    @Override public DoubleSolution createSolution() {
      return new DefaultDoubleSolution(this);
    }

    @Override public Double getLowerBound(int index) {
      return super.getUpperBound(index);
    }

    @Override public Double getUpperBound(int index) {
      return super.getUpperBound(index);
    }
  }
}
