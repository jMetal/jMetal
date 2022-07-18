package org.uma.jmetal.util.extremevalues.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.extremevalues.ExtremeValuesFinder;
import org.uma.jmetal.util.legacy.front.Front;

/**
 * Class for finding the extreme values of front objects
 *
 * @author Antonio J. Nebro
 */
public class FrontExtremeValues implements ExtremeValuesFinder <Front, List<Double>> {

  @Override public List<Double> findLowestValues(Front front) {
    List<Double> minimumValue;

    if (front == null) {
      throw new JMetalException("The front is null") ;
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty") ;
    }

    int numberOfObjectives = front.getPointDimensions() ;

      List<Double> list = new ArrayList<>();
      for (int i1 = 0; i1 < numberOfObjectives; i1++) {
          Double positiveInfinity = Double.POSITIVE_INFINITY;
          list.add(positiveInfinity);
      }
      minimumValue = list;

    for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getValue(j) < minimumValue.get(j)) {
          minimumValue.set(j, front.getPoint(i).getValue(j));
        }
      }
    }

    return minimumValue;
  }

  @Override public List<Double> findHighestValues(Front front) {
    List<Double> maximumValue;

    if (front == null) {
      throw new JMetalException("The front is null") ;
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty") ;
    }

    int numberOfObjectives = front.getPointDimensions() ;

      List<Double> list = new ArrayList<>();
      for (int i1 = 0; i1 < numberOfObjectives; i1++) {
          Double negativeInfinity = Double.NEGATIVE_INFINITY;
          list.add(negativeInfinity);
      }
      maximumValue = list;

    for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getValue(j) > maximumValue.get(j)) {
          maximumValue.set(j, front.getPoint(i).getValue(j));
        }
      }
    }

    return maximumValue;
  }
}
