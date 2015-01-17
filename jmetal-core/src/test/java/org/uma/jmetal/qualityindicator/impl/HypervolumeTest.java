//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class HypervolumeTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front approximation object is null"));

    List<DoubleSolution> paretoFront = new ArrayList<>() ;

    Hypervolume hypervolume = new Hypervolume() ;
    hypervolume.execute(null, paretoFront) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front object is null"));

    List<DoubleSolution> front = new ArrayList<>() ;

    Hypervolume hypervolume = new Hypervolume() ;
    hypervolume.execute(front, null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFsfdasfrontIl() {
    int numberOfObjectives = 3 ;
    DoubleProblem problem = new MockDoubleProblem(numberOfObjectives) ;

    List<DoubleSolution> front1 = Arrays.asList(problem.createSolution(), problem.createSolution()) ;
    List<DoubleSolution> front2 = Arrays.asList(problem.createSolution(), problem.createSolution()) ;

    Hypervolume hypervolume = new Hypervolume() ;
    hypervolume.execute(front1, front2) ;
  }

  private class MockDoubleProblem extends AbstractDoubleProblem {
    /**
     * Constructor
     */
    public MockDoubleProblem(Integer numberOfObjectives) {
      setNumberOfVariables(1);
      setNumberOfObjectives(numberOfObjectives);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    /**
     * Evaluate() method
     */
    @Override public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }

  private class MockIntegerProblem extends AbstractIntegerProblem {
    /**
     * Constructor
     */
    public MockIntegerProblem(Integer numberOfObjectives) {
      setNumberOfVariables(1);
      setNumberOfObjectives(numberOfObjectives);

      List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4);
        upperLimit.add(4);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    /**
     * Evaluate() method
     */
    @Override public void evaluate(IntegerSolution solution) {
      solution.setObjective(0, 0);
      solution.setObjective(1, 1);
    }
  }
}
