package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class offers different methods to manipulate reference points.
 * A reference point is a vector containing a value for each component of an objective function.
 *
 * @author Rubén Saborido Infantes
 * Modified by: Antonio J. Nebro
 */
public class ReferencePoint extends ArrayPoint {

  public enum ReferencePointType {
    ACHIEVABLE, UNACHIEVABLE
  };

  /**
   * Construct a reference point from a vector
   * @param referencePoint Vector defining a reference point
   */
  public ReferencePoint(double[] referencePoint) {
    super(referencePoint) ;
  }

  /**
   * Construct an empty reference point with a dimension given
   * @param numberOfObjectives The number of components
   */
  public ReferencePoint(int numberOfObjectives) {
    super(numberOfObjectives) ;
  }

  /**
   * Construct a reference point reading it from a file
   * @param referencePointFileName File containing a reference point
   */
  public ReferencePoint(String referencePointFileName) throws IOException {
    super(referencePointFileName) ;
  }

  /**
   * Construct a random reference point from a Pareto front file
   * @param type The type of the created reference point
   * @param paretoFrontFileName A Pareto front in a file
   */

  public ReferencePoint(ReferencePointType type, String paretoFrontFileName)
          throws JMetalException, FileNotFoundException {
    int randomIndexPoint;
    double[] minimumValues, maximumValues;
    int index, numberOfObjectives;

    Front front = new ArrayFront(paretoFrontFileName) ;

    numberOfObjectives = front.getPointDimensions() ;

    minimumValues = FrontUtils.getMinimumValues(front);
    maximumValues = FrontUtils.getMaximumValues(front);

    randomIndexPoint = JMetalRandom.getInstance().nextInt(0, front.getNumberOfPoints());

    List<Double>referencePoint = new ArrayList<>();

    switch (type) {
      case ACHIEVABLE:
        for (index = 0; index < numberOfObjectives; index++) {
          referencePoint.add(JMetalRandom.getInstance().nextDouble(
                  front.getPoint(randomIndexPoint).getDimensionValue(index), maximumValues[index]));
        }
        break;

      case UNACHIEVABLE:
        for (index = 0; index < numberOfObjectives; index++) {
          referencePoint.add(JMetalRandom.getInstance().nextDouble(
                  minimumValues[index], front.getPoint(randomIndexPoint).getDimensionValue(index)));
        }
        break;
    }

    point = new double[referencePoint.size()] ;
    for (int i = 0; i < referencePoint.size(); i++) {
      point[i] = referencePoint.get(i) ;
    }
  }
}
