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

import org.uma.jmetal.util.JMetalException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionAtBounds implements RepairDoubleSolution {
  /**
   * Checks if the value is between its bounds; if not, the lower or upper bound is returned
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is in the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) {
    if (lowerBound > upperBound) {
      throw new JMetalException("The lower bound (" + lowerBound + ") is greater than the "
          + "upper bound (" + upperBound+")") ;
    }

    double result = value ;
    if (value < lowerBound) {
      result = lowerBound ;
    }
    if (value > upperBound) {
      result = upperBound ;
    }

    return result ;
  }
}
