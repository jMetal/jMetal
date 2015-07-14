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
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.util.ArrayList;
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
    exception.expectMessage(containsString("The pareto front approximation is null"));

    Front front = new ArrayFront(0, 0) ;

    Hypervolume hypervolume = new Hypervolume(front) ;
    hypervolume.evaluate(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front is null"));

    Front front = null ;

    Hypervolume hypervolume = new Hypervolume(front) ;;
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
