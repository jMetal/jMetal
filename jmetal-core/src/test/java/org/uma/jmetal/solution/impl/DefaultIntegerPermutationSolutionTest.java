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

package org.uma.jmetal.solution.impl;

import org.junit.Test;
import org.uma.jmetal.problem.IntegerDoubleProblem;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.PermutationSolution;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DefaultIntegerPermutationSolutionTest {

  @Test
  public void shouldConstructorCreateAValidSolution() {
    int permutationLength = 20 ;
    AbstractIntegerPermutationProblem problem =
        new MockIntegerPermutationProblem(permutationLength) ;
    PermutationSolution<Integer> solution = problem.createSolution();

    List<Integer> values = new ArrayList<>() ;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      values.add(solution.getVariableValue(i)) ;
    }

    Collections.sort(values);

    List<Integer> expectedList = new ArrayList<>(permutationLength) ;
    for (int i = 0; i < permutationLength; i++) {
      expectedList.add(i) ;
    }

    assertArrayEquals(expectedList.toArray(), values.toArray());
  }

  /**
   * Mock class representing a integer permutation problem
   */
  private class MockIntegerPermutationProblem extends AbstractIntegerPermutationProblem {

    /**
     * Constructor
     */
    public MockIntegerPermutationProblem(int permutationLength) {
      setNumberOfVariables(permutationLength);
      setNumberOfObjectives(2);
    }

    @Override
    public void evaluate(PermutationSolution<Integer> solution) {

    }


    @Override
    public int getPermutationLength() {
      return getNumberOfVariables();
    }
  }
}