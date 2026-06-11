package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

/**
 * Inverted Penalty Boundary Intersection (IPBI) scalarizing function. Unlike the standard PBI,
 * which measures distances from the ideal point and is minimized, IPBI measures the displacement
 * of a solution from the nadir point and is naturally maximized. To keep the common
 * "smaller is better" convention used by the decomposition-based replacement, this implementation
 * returns the negated value {@code theta * d2 - d1}.
 *
 * <p>Given the displacement from the nadir point u = z^nad - f(x):
 * <ul>
 *   <li>d1 = |u . w| / ||w|| (projection of u onto the weight vector)</li>
 *   <li>d2 = || u - d1 * (w / ||w||) || (perpendicular distance of u to the weight vector)</li>
 * </ul>
 * IPBI maximizes {@code d1 - theta * d2}; this method therefore returns {@code theta * d2 - d1}.
 *
 * <p>Because the nadir point is required, this function is intended to be used with objective
 * normalization enabled, so that the nadir point estimation is tracked during the search.
 *
 * <p>Reference:
 * <ul>
 *   <li>Sato, H. (2014). Inverted PBI in MOEA/D and its impact on the search performance on multi
 *   and many-objective optimization. Proceedings of the 2014 Annual Conference on Genetic and
 *   Evolutionary Computation (GECCO '14), 645-652. https://doi.org/10.1145/2576768.2598297</li>
 * </ul>
 */
public class InvertedPenaltyBoundaryIntersection implements AggregationFunction {
  private double epsilon = 0.000001;
  private final boolean normalizeObjectives;
  private final double theta;

  public InvertedPenaltyBoundaryIntersection() {
    this(0.1, false);
  }

  public InvertedPenaltyBoundaryIntersection(double theta, boolean normalizeObjectives) {
    this.theta = theta;
    this.normalizeObjectives = normalizeObjectives;
  }

  @Override
  public double compute(double[] vector, double[] weightVector, IdealPoint idealPoint,
      NadirPoint nadirPoint) {
    double d1;
    double d2 = 0.0;
    double nl = 0.0;
    double projection = 0.0;

    for (int i = 0; i < vector.length; i++) {
      double value;
      if (normalizeObjectives) {
        value = (nadirPoint.value(i) - vector[i])
            / (nadirPoint.value(i) - idealPoint.value(i) + epsilon);
      } else {
        value = nadirPoint.value(i) - vector[i];
      }
      projection += value * weightVector[i];
      nl += Math.pow(weightVector[i], 2.0);
    }
    nl = Math.sqrt(nl);
    d1 = Math.abs(projection) / nl;

    for (int i = 0; i < vector.length; i++) {
      double value;
      if (normalizeObjectives) {
        value = (nadirPoint.value(i) - vector[i])
            / (nadirPoint.value(i) - idealPoint.value(i) + epsilon);
      } else {
        value = nadirPoint.value(i) - vector[i];
      }
      d2 += Math.pow(value - d1 * (weightVector[i] / nl), 2.0);
    }
    d2 = Math.sqrt(d2);

    return theta * d2 - d1;
  }

  @Override
  public void epsilon(double epsilon) {
    this.epsilon = epsilon;
  }

  @Override
  public boolean normalizeObjectives() {
    return this.normalizeObjectives;
  }
}
