package org.uma.jmetal.qualityindicator.impl;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.LexicographicalVectorComparator;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 * Combining model-based and genetics-based offspring generation for
 * multi-objective optimization using a convergence criterion,
 * 2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GeneralizedSpread extends QualityIndicator {

  /**
   * Default constructor
   */
  public GeneralizedSpread() {
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public GeneralizedSpread(double[][] referenceFront) {
    super(referenceFront) ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[] @NotNull [] front) {
    Check.notNull(front);

    return generalizedSpread(front, referenceFront);
  }

  /**
   *  Calculates the generalized spread metric. Given the 
   *  pareto front, the true pareto front as <code>double []</code>
   *  and the number of objectives, the method return the value for the
   *  metric.
   *  @param front The front.
   *  @param referenceFront The reference pareto front.
   *  @return the value of the generalized spread metric
   **/
  public double generalizedSpread(double[][] front, double[] @NotNull [] referenceFront) {
    int numberOfObjectives = front[0].length ;

    double[] @NotNull [] extremeValues = new double[numberOfObjectives][] ;
    for (int i = 0; i < numberOfObjectives; i++) {
      //Arrays.sort(referenceFront, new VectorPositionComparator(i));
      int finalI = i;
      Arrays.sort(referenceFront, Comparator.comparingDouble(x -> x[finalI])) ;
        double[] newPoint = new double[10];
        int count = 0;
        double[] array = referenceFront[referenceFront.length - 1];
        for (int j = 0; j < numberOfObjectives; j++) {
            double v = array[j];
            if (newPoint.length == count) newPoint = Arrays.copyOf(newPoint, count * 2);
            newPoint[count++] = v;
        }
        newPoint = Arrays.copyOfRange(newPoint, 0, count);
        extremeValues[i] = newPoint ;
    }

    int numberOfPoints = front.length;

    Arrays.sort(front, new LexicographicalVectorComparator());
    // front.sort(new LexicographicalPointComparator());

    if (new EuclideanDistanceBetweenVectors().compute(front[0], front[front.length - 1]) == 0.0) {
      return 1.0;
    } else {
        double dmean = 0.0;
        for (double[] doubles : front) {
            double distanceToNearestVector = VectorUtils.distanceToNearestVector(doubles, front);
            dmean += distanceToNearestVector;
        }

        dmean = dmean / (numberOfPoints);

        double dExtrems = 0.0;
        for (double[] extremeValue : extremeValues) {
            double v = VectorUtils.distanceToClosestVector(extremeValue, front);
            dExtrems += v;
        }

        double mean = 0.0;
      for (int i = 0; i < front.length; i++) {
        mean += Math.abs(VectorUtils.distanceToNearestVector(front[i], front) - dmean);
      }

      return (dExtrems + mean) / (dExtrems + (numberOfPoints*dmean));
    }
  }

  @Override public String getDescription() {
    return "Generalized Spread quality indicator" ;
  }

  @Override public @NotNull String getName() {
    return "GSPREAD" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}

