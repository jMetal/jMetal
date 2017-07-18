package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the hypervolume indicator. The code is the a Java version
 * of the original metric implementation by Eckart Zitzler.
 * Reference: E. Zitzler and L. Thiele
 * Multiobjective Evolutionary Algorithms: A Comparative Case Study and the Strength Pareto Approach,
 * IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
 * pp. 257-271, 1999.

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class PISAHypervolume<S extends Solution<?>> extends Hypervolume<S> {

  private static final double DEFAULT_OFFSET = 100.0 ;
  private double offset = DEFAULT_OFFSET ;

  /**
   * Default constructor
   */
  public PISAHypervolume() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public PISAHypervolume(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public PISAHypervolume(Front referenceParetoFront) {
    super(referenceParetoFront) ;
  }

  /**
   * Evaluate() method
   * @param paretoFrontApproximation
   * @return
   */
  @Override public Double evaluate(List<S> paretoFrontApproximation) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation is null") ;
    }

    return hypervolume(new ArrayFront(paretoFrontApproximation), referenceParetoFront);  }

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
   * @param referenceFront    The true pareto front
   */
  private double hypervolume(Front front, Front referenceFront) {

    Front invertedFront;
    invertedFront = FrontUtils.getInvertedFront(front);

    int numberOfObjectives = referenceFront.getPoint(0).getNumberOfDimensions() ;

    // STEP4. The hypervolume (control is passed to the Java version of Zitzler code)
    return this.calculateHypervolume(FrontUtils.convertFrontToArray(invertedFront),
        invertedFront.getNumberOfPoints(), numberOfObjectives);
  }

  @Override public String getDescription() {
    return "PISA implementation of the hypervolume quality indicator" ;
  }

  @Override
  public void setOffset (double offset) {
    this.offset = offset ;
  }

  @Override
  public List<S> computeHypervolumeContribution(List<S> solutionList, List<S> referenceFrontList) {
    if (solutionList.size() > 1) {
      Front front = new ArrayFront(solutionList) ;
      Front referenceFront = new ArrayFront(referenceFrontList) ;

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] maximumValues = FrontUtils.getMaximumValues(referenceFront) ;
      double[] minimumValues = FrontUtils.getMinimumValues(referenceFront) ;

      // STEP 2. Get the normalized front
      FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues) ;
      Front normalizedFront = frontNormalizer.normalize(front) ;

      // compute offsets for reference point in normalized space
      double[] offsets = new double[maximumValues.length];
      for (int i = 0; i < maximumValues.length; i++) {
        offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
      }
      // STEP 3. Inverse the pareto front. This is needed because the original
      // metric by Zitzler is for maximization problem
      Front invertedFront = FrontUtils.getInvertedFront(normalizedFront);

      // shift away from origin, so that boundary points also get a contribution > 0
      for (int i = 0; i < invertedFront.getNumberOfPoints(); i++) {
        Point point = invertedFront.getPoint(i) ;

        for (int j = 0; j < point.getNumberOfDimensions(); j++) {
          point.setDimensionValue(j, point.getDimensionValue(j)+ offsets[j]);
        }
      }

      HypervolumeContributionAttribute<S> hvContribution = new HypervolumeContributionAttribute<>() ;

      // calculate contributions and sort
      double[] contributions = hvContributions(FrontUtils.convertFrontToArray(invertedFront));
      for (int i = 0; i < contributions.length; i++) {
        hvContribution.setAttribute(solutionList.get(i), contributions[i]);
      }

      Collections.sort(solutionList, new HypervolumeContributionComparator<S>());

    }
    return solutionList ;
  }

  @Override

  public double getOffset() {
    return offset ;
  }

  /**
   * Calculates how much hypervolume each point dominates exclusively. The points
   * have to be transformed beforehand, to accommodate the assumptions of Zitzler's
   * hypervolume code.
   *
   * @param front transformed objective values
   * @return HV contributions
   */
  private double[] hvContributions(double[][] front) {

    int numberOfObjectives = front[0].length ;
    double[] contributions = new double[front.length];
    double[][] frontSubset = new double[front.length - 1][front[0].length];
    LinkedList<double[]> frontCopy = new LinkedList<double[]>();
    Collections.addAll(frontCopy, front);
    double[][] totalFront = frontCopy.toArray(frontSubset);
    double totalVolume =
        this.calculateHypervolume(totalFront, totalFront.length, numberOfObjectives);
    for (int i = 0; i < front.length; i++) {
      double[] evaluatedPoint = frontCopy.remove(i);
      frontSubset = frontCopy.toArray(frontSubset);
      // STEP4. The hypervolume (control is passed to java version of Zitzler code)
      double hv = this.calculateHypervolume(frontSubset, frontSubset.length, numberOfObjectives);
      double contribution = totalVolume - hv;
      contributions[i] = contribution;
      // put point back
      frontCopy.add(i, evaluatedPoint);
    }
    return contributions;
  }

}
