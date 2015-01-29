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

package org.uma.jmetal.solution.util;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionAtRandomTest {
  private static final double EPSILON = 0.0000000000001;
  private RepairDoubleSolution repair;

  @Before public void setup() {
    repair = new RepairDoubleSolutionAtRandom();
  }

  @Test(expected = JMetalException.class)
  public void shouldRRepairDoubleSolutionAtRandomRaiseAnExceptionIfTheBoundsAreIncorrect() {
    repair.repairSolutionVariableValue(0.0, 1.0, -1.0);
  }

  @Test
  public void shouldRRepairDoubleSolutionAtRandomAssignARandomValueIfValueIsLessThanTheLowerBound() {
    double lowerBound = -1.0;
    double upperBound = 1.0;
    assertThat(repair.repairSolutionVariableValue(-3, lowerBound, upperBound),
        Matchers.lessThanOrEqualTo(upperBound));

    assertThat(repair.repairSolutionVariableValue(-3, lowerBound, upperBound),
        Matchers.greaterThanOrEqualTo(lowerBound));
  }

  @Test
  public void shouldRRepairDoubleSolutionAtRandomAssignARandomValueIfValueIsGreaterThanTheUpperBound() {
    double lowerBound = -1.0;
    double upperBound = 1.0;
    assertThat(repair.repairSolutionVariableValue(4, lowerBound, upperBound),
        Matchers.lessThanOrEqualTo(upperBound));

    assertThat(repair.repairSolutionVariableValue(4, lowerBound, upperBound),
        Matchers.greaterThanOrEqualTo(lowerBound));
  }
}
