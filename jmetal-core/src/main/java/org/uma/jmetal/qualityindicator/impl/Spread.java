package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.util.comparator.LexicographicalPointComparator;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;
import org.uma.jmetal.util.point.util.distance.PointDistance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the spread quality indicator. It must be only to two bi-objective problem.
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and
 * elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans. on Evol. Computation 6 (2002) 182-197
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class Spread <S extends Solution<?>> extends GenericIndicator<S> {

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
  public Spread(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public Spread(Front referenceParetoFront) {
    super(referenceParetoFront) ;
  }

  /**
   * Evaluate() method
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(List<S> solutionList) {
    return spread(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Calculates the Spread metric.
   *
   * @param front              The front.
   * @param referenceFront    The true pareto front.
   */
  public double spread(Front front, Front referenceFront) {
    PointDistance distance = new EuclideanDistance() ;

    // STEP 1. Sort normalizedFront and normalizedParetoFront;
    front.sort(new LexicographicalPointComparator());
    referenceFront.sort(new LexicographicalPointComparator());

    // STEP 2. Compute df and dl (See specifications in Deb's description of the metric)
    double df = distance.compute(front.getPoint(0), referenceFront.getPoint(0)) ;
    double dl = distance.compute(front.getPoint(front.getNumberOfPoints() - 1),
        referenceFront.getPoint(referenceFront.getNumberOfPoints() - 1)) ;

    double mean = 0.0;
    double diversitySum = df + dl;

    int numberOfPoints = front.getNumberOfPoints() ;

    // STEP 3. Calculate the mean of distances between points i and (i - 1).
    // (the points are in lexicografical order)
    for (int i = 0; i < (numberOfPoints - 1); i++) {
      mean += distance.compute(front.getPoint(i), front.getPoint(i + 1));
    }

    mean = mean / (double) (numberOfPoints - 1);

    // STEP 4. If there are more than a single point, continue computing the
    // metric. In other case, return the worse value (1.0, see metric's description).
    if (numberOfPoints > 1) {
      for (int i = 0; i < (numberOfPoints - 1); i++) {
        diversitySum += Math.abs(distance.compute(front.getPoint(i),
            front.getPoint(i + 1)) - mean);
      }
      return diversitySum / (df + dl + (numberOfPoints - 1) * mean);
    } else {
      return 1.0;
    }
  }

  @Override public String getName() {
    return "SPREAD" ;
  }
  @Override public String getDescription() {
    return "Spread quality indicator" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
