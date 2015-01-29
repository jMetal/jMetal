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

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionAtBoundsTest {
  private static final double EPSILON = 0.0000000000001 ;
  private RepairDoubleSolution repair ;

  @Before
  public void setup() {
    repair = new RepairDoubleSolutionAtBounds() ;
  }

  @Test (expected = JMetalException.class)
  public void shouldRRepairDoubleSolutionAtBoundsRaiseAnExceptionIfTheBoundsAreIncorrect() {
    repair.repairSolutionVariableValue(0.0, 1.0, -1.0) ;
  }

  @Test
  public void shouldRRepairDoubleSolutionAtBoundsAssignTheLowerBoundIfValueIsLessThanIt() {
    assertEquals(-1.0, repair.repairSolutionVariableValue(-3, -1.0, 1.0), EPSILON) ;
  }

  @Test
  public void shouldRRepairDoubleSolutionAtBoundsAssignTheUpperBoundIfValueIsGreaterThanIt() {
    assertEquals(1.0, repair.repairSolutionVariableValue(4, -1.0, 1.0), EPSILON) ;
  }
}
