package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ReferencePointUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class providing an implementation of the normalized hypervolume, which is calculated as follows:
 * relative hypervolume = 1 - (HV of the front / HV of the reference front).
 *
 * <p>As the hypervolume of {@link PISAHypervolume}, it assumes that the fronts are normalized, with
 * the reference point (1, ..., 1). The hypervolume of the reference front must be positive,
 * otherwise the indicator is not defined and {@link #compute(double[][])} throws an
 * {@link org.uma.jmetal.util.errorchecking.exception.InvalidConditionException}. The value is
 * negative if the front has more hypervolume than the reference front.
 *
 * <p>The indicator can also be built from a reference point instead of a reference front: the
 * reference is then the box between the origin (the ideal point of a normalized front) and the
 * point, so the indicator is the fraction of that box that the front does not dominate.
 *
 * @author Antonio J. Nebro
 */
public class NormalizedHypervolume extends QualityIndicator {
  private Hypervolume hypervolume;
  private double referenceFrontHypervolume;

  /** The reference point, or null if the indicator was built from a reference front. */
  private double[] referencePoint;

  public NormalizedHypervolume() {
  }

  /**
   * Creates the indicator from a reference point. A front is scaled by the coordinates of the
   * point before computing its hypervolume, so the indicator is 1 minus the fraction of the box
   * {@code [0, referencePoint]} that the front dominates, and the reference front that the
   * indicator returns is the one that {@link PISAHypervolume} derives from the point.
   *
   * @param referencePoint the reference point, whose coordinates must be positive and finite
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if the point is null
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if the point is
   *     empty or has a coordinate that is not positive and finite
   */
  public NormalizedHypervolume(double[] referencePoint) {
    Check.notNull(referencePoint, "referencePoint");
    Check.that(referencePoint.length > 0, "The reference point cannot be empty");
    for (int i = 0; i < referencePoint.length; i++) {
      int coordinate = i;
      Check.that(
          Double.isFinite(referencePoint[i]) && referencePoint[i] > 0.0,
          () -> "The coordinate " + coordinate + " of the reference point is not positive and "
              + "finite: " + referencePoint[coordinate]);
    }

    this.referencePoint = referencePoint.clone();
    this.referenceFront =
        ReferencePointUtils.createReferenceFrontFromReferencePoint(this.referencePoint);
    this.hypervolume = new PISAHypervolume(this.referencePoint);
    this.referenceFrontHypervolume = 1.0;
  }

  public NormalizedHypervolume(double[][] referenceFront) {
    super(referenceFront);
    useReferenceFront(referenceFront);
  }

  @Override
  public QualityIndicator newInstance() {
    return new NormalizedHypervolume();
  }

  @Override
  public void referenceFront(double[][] referenceFront) {
    super.referenceFront(referenceFront);
    useReferenceFront(referenceFront);
  }

  private void useReferenceFront(double[][] referenceFront) {
    referencePoint = null;
    hypervolume = new PISAHypervolume(referenceFront);
    referenceFrontHypervolume = hypervolume.compute(referenceFront);
  }

  @Override
  public String name() {
    return "NHV";
  }

  @Override
  public String description() {
    return "Normalized hypervolume";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }

  /**
   * Computes the indicator.
   *
   * @param front the normalized front
   * @return 1 - (HV of the front / HV of the reference front), which is 0 for the reference front
   *     and 1 for an empty front
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if the indicator
   *     has no reference front, the hypervolume of the reference front is not positive, or the
   *     points of the front do not have as many objectives as the reference point
   */
  @Override
  public double compute(double[][] front) {
    Check.notNull(front, "front");
    Check.that(
        hypervolume != null,
        "The normalized hypervolume has no reference front: set it before computing the indicator");
    Check.that(
        referenceFrontHypervolume > 0.0,
        () -> "The hypervolume of the reference front is not positive ("
            + referenceFrontHypervolume + "), so the normalized hypervolume is not defined");

    double hypervolumeValue = hypervolume.compute(scaled(front));
    double result = 1 - (hypervolumeValue / referenceFrontHypervolume);
    if (result < 0) {
      JMetalLogger.logger.info("The normalized hypervolume value is negative: ");
      JMetalLogger.logger.info("- HV reference front: " + referenceFrontHypervolume);
      JMetalLogger.logger.info("- HV current front  : " + hypervolumeValue);
    }

    return result;
  }

  /** The front divided by the reference point, if the indicator was built from one. */
  private double[][] scaled(double[][] front) {
    if (referencePoint == null) {
      return front;
    }

    double[][] result = new double[front.length][];
    for (int i = 0; i < front.length; i++) {
      Check.notNull(front[i], "a point of the front");
      Check.that(
          front[i].length == referencePoint.length,
          "The points of the front must have as many objectives as the reference point");
      result[i] = new double[referencePoint.length];
      for (int j = 0; j < referencePoint.length; j++) {
        result[i][j] = front[i][j] / referencePoint[j];
      }
    }

    return result;
  }
}
