package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.comparator.LexicographicalVectorComparator;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * This class implements the spread quality indicator. It must be only to two bi-objective problem.
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and
 * elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans. on Evol. Computation 6 (2002) 182-197
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class Spread extends QualityIndicator {

  /**
   * Default constructor
   */
  public Spread() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public Spread(String referenceParetoFrontFile) {
    super(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public Spread(double[][] referenceFront) {
    super(referenceFront) ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[][] front) {
    return spread(front, referenceFront);
  }

  /**
   * Calculates the Spread metric.
   *
   * @param front              The front.
   * @param referenceFront    The true pareto front.
   */
  public double spread(double[][] front, double[][] referenceFront) {
    var distance = new EuclideanDistanceBetweenVectors() ;

    // STEP 1. Sort normalizedFront and normalizedParetoFront;
    Arrays.sort(front, 0, front.length, new LexicographicalVectorComparator()) ;
    Arrays.sort(referenceFront, 0, referenceFront.length, new LexicographicalVectorComparator()) ;

    // STEP 2. Compute df and dl (See specifications in Deb's description of the metric)
    double df = distance.compute(front[0], referenceFront[0]) ;
    double dl = distance.compute(front[front.length - 1],
            referenceFront[referenceFront.length - 1]) ;

    double mean = 0.0;
    double diversitySum = df + dl;

    int numberOfPoints = front.length ;

    // STEP 3. Calculate the mean of distances between points i and (i - 1).
    // (the points are in lexicografical order)
    for (int i = 0; i < (numberOfPoints - 1); i++) {
      mean += distance.compute(front[i], front[i + 1]);
    }

    mean = mean / (double) (numberOfPoints - 1);

    // STEP 4. If there are more than a single point, continue computing the
    // metric. In other case, return the worse value (1.0, see metric's description).
    if (numberOfPoints > 1) {
      for (int i = 0; i < (numberOfPoints - 1); i++) {
        diversitySum += Math.abs(distance.compute(front[i],
                front[i + 1]) - mean);
      }
      return diversitySum / (df + dl + (numberOfPoints - 1) * mean);
    } else {
      return 1.0;
    }
  }

  @Override public String getName() {
    return "SP" ;
  }

  @Override public String getDescription() {
    return "Spread quality indicator" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
