package org.uma.jmetal.util.extremevalues.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.extremevalues.ExtremeValuesFinder;
import org.uma.jmetal.util.legacy.front.Front;

/**
 * Class for finding the extreme values of front objects
 *
 * @author Antonio J. Nebro
 */
public class FrontExtremeValues implements ExtremeValuesFinder <Front, List<Double>> {

  @Override public @NotNull List<Double> findLowestValues(Front front) {

    if (front == null) {
      throw new JMetalException("The front is null") ;
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty") ;
    }

    var numberOfObjectives = front.getPointDimensions() ;

      List<Double> list = new ArrayList<>();
      for (var i1 = 0; i1 < numberOfObjectives; i1++) {
          Double positiveInfinity = Double.POSITIVE_INFINITY;
          list.add(positiveInfinity);
      }
    var minimumValue = list;

    for (var i = 0; i < front.getNumberOfPoints(); i++) {
      for (var j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getValue(j) < minimumValue.get(j)) {
          minimumValue.set(j, front.getPoint(i).getValue(j));
        }
      }
    }

    return minimumValue;
  }

  @Override public List<Double> findHighestValues(Front front) {

    if (front == null) {
      throw new JMetalException("The front is null") ;
    } else if (front.getNumberOfPoints() == 0) {
      throw new JMetalException("The front is empty") ;
    }

    var numberOfObjectives = front.getPointDimensions() ;

      List<Double> list = new ArrayList<>();
      for (var i1 = 0; i1 < numberOfObjectives; i1++) {
          Double negativeInfinity = Double.NEGATIVE_INFINITY;
          list.add(negativeInfinity);
      }
    var maximumValue = list;

    for (var i = 0; i < front.getNumberOfPoints(); i++) {
      for (var j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getValue(j) > maximumValue.get(j)) {
          maximumValue.set(j, front.getPoint(i).getValue(j));
        }
      }
    }

    return maximumValue;
  }
}
