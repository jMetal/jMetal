package org.uma.jmetal.operator.impl.crossover;

import org.junit.Test;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by ajnebro on 10/6/15.
 */
public class NullCrossoverTest {

  @Test
  public void shouldExecuteReturnTwoDifferentObjectsWhichAreEquals() {
    Problem<DoubleSolution> problem = new MockProblem() ;
    List<DoubleSolution> parents = new ArrayList<>(2) ;
    parents.add((DoubleSolution) problem.createSolution()) ;
    parents.add((DoubleSolution) problem.createSolution()) ;

    CrossoverOperator<DoubleSolution> crossover;
    crossover = new NullCrossover<DoubleSolution>() ;

    List<DoubleSolution> offspring = crossover.execute(parents);
    assertNotSame(parents.get(0), offspring.get(0)) ;
    assertNotSame(parents.get(1), offspring.get(1)) ;

    assertEquals(parents.get(0), offspring.get(0)) ;
    assertEquals(parents.get(1), offspring.get(1)) ;
  }

  @SuppressWarnings("serial")
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
      return 2;
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
