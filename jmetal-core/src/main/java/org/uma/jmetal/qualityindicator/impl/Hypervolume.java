//  Hypervolume.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the hypervolume indicator. The code is the a Java version
 * of the original metric implementation by Eckart Zitzler.
 * Reference: E. Zitzler and L. Thiele
 * Multiobjective Evolutionary Algorithms: A Comparative Case Study and the Strength Pareto Approach,
 * IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
 * pp. 257-271, 1999.
 */
public class Hypervolume extends SimpleDescribedEntity implements QualityIndicator<List<? extends Solution<?>>, Double> {
  private Front referenceParetoFront ;

  public Hypervolume() {
    super("HV", "Hypervolume quality indicator") ;
  }
  private boolean normalize ;

  /**
   * Constructor
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public Hypervolume(String referenceParetoFrontFile) throws FileNotFoundException {
    this() ;
    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;
    normalize = true ;
  }

  /**
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public Hypervolume(Front referenceParetoFront) {
    this() ;
    if (referenceParetoFront == null) {
      throw new JMetalException("The pareto front is null");
    }

    this.referenceParetoFront = referenceParetoFront ;
  }

  /**
   * Set normalization of the fronts
   * @param normalize
   * @return
   */
  public Hypervolume setNormalize(boolean normalize) {
    this.normalize = normalize ;

    return this ;
  }

  /**
   * Return true if the fronts are normalized before computing the indicator
   * @return
   */
  public boolean frontsNormalized() {
    return normalize ;
  }

  /**
   * Evaluate method
   * @param paretoFrontApproximation
   * @return
   */
  @Override public Double evaluate(List<? extends Solution<?>> paretoFrontApproximation) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation is null") ;
    }

    return hypervolume(new ArrayFront(paretoFrontApproximation), referenceParetoFront);  }

  @Override
  public String getName() {
    return super.getName() ;
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
  private int reduceNondominatedSet(double[][] front, int noPoints, int objective,
      double threshold) {
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
   * @param front        The front
   * @param trueParetoFront    The true pareto front
   */
  private double hypervolume(Front front, Front trueParetoFront) {
    Front normalizedFront;

    if (normalize) {
      double[] maximumValue;
      double[] minimumValue;

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      maximumValue = FrontUtils.getMaximumValues(trueParetoFront);
      minimumValue = FrontUtils.getMinimumValues(trueParetoFront);

      // STEP 2. Get the normalized front and true Pareto fronts
      normalizedFront = FrontUtils.getNormalizedFront(front, maximumValue, minimumValue);

    } else {
      normalizedFront = front ;
    }

    // STEP 3. Inverse the pareto front. This is needed because of the original
    //metric by Zitzler is for maximization problem
    Front invertedFront;
    invertedFront = FrontUtils.getInvertedFront(normalizedFront);

    int numberOfObjectives = trueParetoFront.getPoint(0).getNumberOfDimensions() ;

    // STEP4. The hypervolume (control is passed to the Java version of Zitzler code)
    return this.calculateHypervolume(FrontUtils.convertFrontToArray(invertedFront),
        invertedFront.getNumberOfPoints(), numberOfObjectives);
  }
}
