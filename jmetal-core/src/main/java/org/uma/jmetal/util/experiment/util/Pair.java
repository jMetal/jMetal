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

package org.uma.jmetal.util.experiment.util;

/**
 * Created by ajnebro on 18/12/15.
 */
public class Pair implements Comparable {
  public double index;
  public double value;

  public Pair(double index, double value) {
    this.index = index ;
    this.value = value ;
  }

  /**
   * Comparison using absolute values
   * @param o1
   * @return The result of the comparison
   */
  public int compareTo (Object o1) {
    if (Math.abs(this.value) > Math.abs(((Pair)o1).value)){
      return 1;
    } else if (Math.abs(this.value) < Math.abs(((Pair)o1).value)) {
      return -1;
    } else {
      return 0;
    }
  }
}