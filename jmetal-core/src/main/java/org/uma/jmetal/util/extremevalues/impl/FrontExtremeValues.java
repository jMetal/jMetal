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
package org.uma.jmetal.util.extremevalues.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.extremevalues.ExtremeValuesFinder;
import org.uma.jmetal.util.front.Front;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for finding the extreme values of front objects
 *
 * @author Antonio J. Nebro
 */
public class FrontExtremeValues implements ExtremeValuesFinder <Front, List<Double>> {

  @Override public List<Double> findLowestValues(Front front) {
    List<Double> minimumValue = new ArrayList<>() ;

    if (front == null) {
      throw new JMetalException("The front is null") ;
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty") ;
    }

    int numberOfObjectives = front.getPointDimensions() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      minimumValue.add(Double.POSITIVE_INFINITY);
    }

    for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getDimensionValue(j) < minimumValue.get(j)) {
          minimumValue.set(j, front.getPoint(i).getDimensionValue(j));
        }
      }
    }

    return minimumValue;
  }

  @Override public List<Double> findHighestValues(Front front) {
    List<Double> maximumValue = new ArrayList<>() ;

    if (front == null) {
      throw new JMetalException("The front is null") ;
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty") ;
    }

    int numberOfObjectives = front.getPointDimensions() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      maximumValue.add(Double.NEGATIVE_INFINITY);
    }

    for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getDimensionValue(j) > maximumValue.get(j)) {
          maximumValue.set(j, front.getPoint(i).getDimensionValue(j));
        }
      }
    }

    return maximumValue;
  }
}
