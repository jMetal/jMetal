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

package org.uma.jmetal.util.point.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Solution used to wrap a {@link Point} object. Only objectives are used.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class PointSolution implements Solution<Double> {
  private int numberOfObjectives ;
  private double[] objectives;
  protected Map<Object, Object> attributes ;

  /**
   * Constructor
   *
   * @param numberOfObjectives
   */
  public PointSolution(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
    objectives = new double[numberOfObjectives] ;
    attributes = new HashMap<>() ;
  }

  /**
   * Constructor
   *
   * @param point
   */
  public PointSolution(Point point) {
    this.numberOfObjectives = point.getNumberOfDimensions() ;
    objectives = new double[numberOfObjectives] ;

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives[i] = point.getDimensionValue(i) ;
    }
  }

  /**
   * Copy constructor
   *
   * @param point
   */
  public PointSolution(PointSolution point) {
    this(point.getNumberOfObjectives()) ;

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives[i] = point.getObjective(i) ;
    }
  }

  @Override public void setObjective(int index, double value) {
    objectives[index]=  value ;
  }

  @Override public double getObjective(int index) {
    return objectives[index];
  }

  @Override public Double getVariableValue(int index) {
    return null;
  }

  @Override public void setVariableValue(int index, Double value) {
  }

  @Override public String getVariableValueString(int index) {
    return null;
  }

  @Override public int getNumberOfVariables() {
    return 0;
  }

  @Override public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  @Override public PointSolution copy() {
    return new PointSolution(this);
  }

  @Override public void setAttribute(Object id, Object value) {
    attributes.put(id, value) ;
  }

  @Override public Object getAttribute(Object id) {
    return attributes.get(id);
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    PointSolution that = (PointSolution) o;

    if (numberOfObjectives != that.numberOfObjectives)
      return false;
    if (!Arrays.equals(objectives, that.objectives))
      return false;

    return true;
  }

  @Override public int hashCode() {
    return Arrays.hashCode(objectives);
  }
  
  @Override
	public String toString() {
		return Arrays.toString(objectives);
	}
}
