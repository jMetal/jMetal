package org.uma.jmetal.qualityindicator.impl.hypervolume.impl;

import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class implements the hypervolume indicator. The code is the a Java version of the original
 * metric implementation by Eckart Zitzler. Reference: E. Zitzler and L. Thiele Multiobjective
 * Evolutionary Algorithms: A Comparative Case Study and the Strength Pareto Approach, IEEE
 * Transactions on Evolutionary Computation, vol. 3, no. 4, pp. 257-271, 1999.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class PISAHypervolume extends Hypervolume {

  private static final double DEFAULT_OFFSET = 100.0;
  private double offset = DEFAULT_OFFSET;

  /**
   * Default constructor
   */
  public PISAHypervolume() {
  }

  /**
   * Constructor with reference point
   *
   * @param referencePoint
   */
  public PISAHypervolume(double[] referencePoint) {
    super(referencePoint);
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public PISAHypervolume(double[][] referenceFront) {
    super(referenceFront);
  }

  /**
   * Evaluate() method
   *
   * @param front
   * @return
   */
  @Override
  public double compute(double[][] front) {
    Check.isNotNull(front);

    return hypervolume(front, referenceFront);
  }

  @Override
  public String getDescription() {
    return "PISA Hypervolume quality indicator" ;
  }

  /*
  returns true if 'point1' dominates 'points2' with respect to the
  to the first 'noObjectives' objectives
  */
  private boolean dominates(double point1[], double point2[], int noObjectives) {
    int i;
    int betterInAnyObjective;

    betterInAnyObjective = 0;
    for (i = 0; i < noObjectives && point1[i] >= point2[i]; i++) {
      if (point1[i] > point2[i]) {
        betterInAnyObjective = 1;
      }
    }

    return ((i >= noObjectives) && (betterInAnyObjective > 0));
  }

  private void swap(double[][] front, int i, int j) {
    double[] temp;

    temp = front[i];
    front[i] = front[j];
    front[j] = temp;
  }

  /* all nondominated points regarding the first 'noObjectives' dimensions
  are collected; the points referenced by 'front[0..noPoints-1]' are
  considered; 'front' is resorted, such that 'front[0..n-1]' contains
  the nondominated points; n is returned */
  private int filterNondominatedSet(double[][] front, int noPoints, int noObjectives) {
    int i, j;
    int n;

    n = noPoints;
    i = 0;
    while (i < n) {
      j = i + 1;
      while (j < n) {
        if (dominates(front[i], front[j], noObjectives)) {
          /* remove point 'j' */
          n--;
          swap(front, j, n);
        } else if (dominates(front[j], front[i], noObjectives)) {
          /* remove point 'i'; ensure that the point copied to index 'i'
          is considered in the next outer loop (thus, decrement i) */
          n--;
          swap(front, i, n);
          i--;
          break;
        } else {
          j++;
        }
      }
      i++;
    }
    return n;
  }

  /* calculate next value regarding dimension 'objective'; consider
  points referenced in 'front[0..noPoints-1]' */
  private double surfaceUnchangedTo(double[][] front, int noPoints, int objective) {
    int i;
    double minValue, value;

    if (noPoints < 1) {
      new JMetalException("run-time error");
    }

    minValue = front[0][objective];
    for (i = 1; i < noPoints; i++) {
      value = front[i][objective];
      if (value < minValue) {
        minValue = value;
      }
    }
    return minValue;
  }

  /* remove all points which have a value <= 'threshold' regarding the
  dimension 'objective'; the points referenced by
  'front[0..noPoints-1]' are considered; 'front' is resorted, such that
  'front[0..n-1]' contains the remaining points; 'n' is returned */
  private int reduceNondominatedSet(
          double[][] front, int noPoints, int objective, double threshold) {
    int n;
    int i;

    n = noPoints;
    for (i = 0; i < n; i++) {
      if (front[i][objective] <= threshold) {
        n--;
        swap(front, i, n);
      }
    }

    return n;
  }

  public double calculateHypervolume(double[][] front, int noPoints, int noObjectives) {
    int n;
    double volume, distance;

    volume = 0;
    distance = 0;
    n = noPoints;
    while (n > 0) {
      int nonDominatedPoints;
      double tempVolume, tempDistance;

      nonDominatedPoints = filterNondominatedSet(front, n, noObjectives - 1);
      if (noObjectives < 3) {
        if (nonDominatedPoints < 1) {
          new JMetalException("run-time error");
        }

        tempVolume = front[0][0];
      } else {
        tempVolume = calculateHypervolume(front, nonDominatedPoints, noObjectives - 1);
      }

      tempDistance = surfaceUnchangedTo(front, n, noObjectives - 1);
      volume += tempVolume * (tempDistance - distance);
      distance = tempDistance;
      n = reduceNondominatedSet(front, n, noObjectives - 1, distance);
    }
    return volume;
  }

  /**
   * Returns the hypervolume value of a front of points
   *
   * @param front          The front
   * @param referenceFront The true pareto front
   */
  private double hypervolume(double[][] front, double[][] referenceFront) {
    double[][] invertedFront;
    invertedFront = VectorUtils.getInvertedFront(front);

    int numberOfObjectives = referenceFront[0].length;

    // STEP4. The hypervolume (control is passed to the Java version of Zitzler code)
    return this.calculateHypervolume(invertedFront, invertedFront.length, numberOfObjectives);
  }

  @Override
  public String getName() {
    return "PISA implementation of the hypervolume quality indicator";
  }
}
