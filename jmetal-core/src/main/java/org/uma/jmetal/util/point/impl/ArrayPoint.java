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

package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import java.util.Arrays;

/**
 * Class representing a point (i.e, an array of double values)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayPoint implements Point {
  private double[] point;

  /**
   * Constructor
   *
   * @param dimensions Dimensions of the point

   */
  public ArrayPoint(int dimensions) {
    point = new double[dimensions];

    for (int i = 0; i < dimensions; i++) {
      point[i] = 0.0;
    }
  }


  /**
   * Copy constructor
   *
   * @param point
   */
  public ArrayPoint(Point point) {
    if (point == null) {
      throw new JMetalException("The point is null") ;
    }

    this.point = new double[point.getNumberOfDimensions()];

    for (int i = 0; i < point.getNumberOfDimensions(); i++) {
      this.point[i] = point.getDimensionValue(i);
    }
  }

  /**
   * Constructor from a solution
   *
   * @param solution
   */
  public ArrayPoint(Solution solution) {
    if (solution == null) {
      throw new JMetalException("The solution is null") ;
    }

    int dimensions = solution.getNumberOfObjectives();
    point = new double[dimensions];

    for (int i = 0; i < dimensions; i++) {
      point[i] = solution.getObjective(i);
    }
  }

  /**
   * Constructor from an array of double values
   *
   * @param point
   */
  public ArrayPoint(double[] point) {
    if (point == null) {
      throw new JMetalException("The array of values is null") ;
    }

    this.point = new double[point.length];
    System.arraycopy(point, 0, this.point, 0, point.length);
  }

  @Override
  public int getNumberOfDimensions() {
    return point.length;
  }

  @Override
  public double[] getValues() {
    return point;
  }

  @Override
  public double getDimensionValue(int index) {
    if ((index < 0) || (index >= point.length)) {
      throw new JMetalException("Index value invalid: " + index +
          ". The point length is: " + point.length) ;
    }
    return point[index] ;
  }

  @Override
  public void setDimensionValue(int index, double value) {
    if ((index < 0) || (index >= point.length)) {
      throw new JMetalException("Index value invalid: " + index +
          ". The point length is: " + point.length) ;
    }
    point[index] = value ;
  }

  @Override
  public String toString() {
    String result = "";
    for (double anObjectives_ : point) {
      result += anObjectives_ + " ";
    }

    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ArrayPoint that = (ArrayPoint) o;

    if (!Arrays.equals(point, that.point))
      return false;

    return true;
  }

  @Override public int hashCode() {
    return Arrays.hashCode(point);
  }
}
