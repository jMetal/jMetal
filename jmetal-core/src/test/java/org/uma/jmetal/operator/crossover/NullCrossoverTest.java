package org.uma.jmetal.operator.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.NullCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Created by ajnebro on 10/6/15.
 */
public class NullCrossoverTest {

  @Test (expected = NullParameterException.class)
  public void shouldExecuteRaiseAnExceptionIfTheParameterListIsNull() {
    new NullCrossover<DoubleSolution>().execute(null) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldExecuteRaiseAnExceptionIfTheParameterListHasNotTwoElements() {
    new NullCrossover<DoubleSolution>().execute(new ArrayList<>()) ;
  }

  @Test
  public void shouldExecuteReturnTwoDifferentObjectsWhichAreEquals() {
    Problem<DoubleSolution> problem = new DummyDoubleProblem() ;
    List<DoubleSolution> parents = new ArrayList<>(2) ;
    parents.add(problem.createSolution()) ;
    parents.add(problem.createSolution()) ;

    CrossoverOperator<DoubleSolution> crossover;
    crossover = new NullCrossover<>() ;

    List<DoubleSolution> offspring = crossover.execute(parents);
    assertNotSame(parents.get(0), offspring.get(0)) ;
    assertNotSame(parents.get(1), offspring.get(1)) ;

    assertEquals(parents.get(0), offspring.get(0)) ;
    assertEquals(parents.get(1), offspring.get(1)) ;
  }
}
