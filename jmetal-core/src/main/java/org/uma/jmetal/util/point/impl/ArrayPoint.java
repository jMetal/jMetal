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
//

//  CREDIT
//  This class is based on the code of the wfg group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.

package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

/**
 * Class representing a point (i.e., an array of double values)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayPoint implements Point{
  private double[] point;

  /**
   * Constructor
   *
   * @param dimensions
   */
  public ArrayPoint(int dimensions) {
    point = new double[dimensions];

    for (int i = 0; i < dimensions; i++) {
      point[i] = 0.0;
    }
  }


  /**
   * Constructor
   *
   * @param point
   */
  public ArrayPoint(Point point) {
    this.point = new double[point.getNumberOfDimensions()];

    for (int i = 0; i < point.getNumberOfDimensions(); i++) {
      this.point[i] = point.getDimensionValue(i);
    }
  }

  /**
   * Constructor
   *
   * @param solution
   */
  public ArrayPoint(Solution solution) {
    int dimensions = solution.getNumberOfObjectives();
    point = new double[dimensions];

    for (int i = 0; i < dimensions; i++) {
      point[i] = solution.getObjective(i);
    }
  }

  /**
   * Constructor
   *
   * @param point
   */
  public ArrayPoint(double[] point) {
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
}
