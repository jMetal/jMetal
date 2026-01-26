package org.uma.jmetal.qualityindicator.impl.hypervolume;

import java.util.Objects;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.ReferencePointUtils;

/**
 * This interface represents implementations of the Hypervolume quality indicator
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public abstract class Hypervolume extends QualityIndicator {

  /**
   * Default constructor.
   */
  public Hypervolume() {}

  /**
   * Constructor with reference front.
   * @param referenceFront The reference front
   * @throws IllegalArgumentException if referenceFront is null or empty
   */
  public Hypervolume(double[][] referenceFront) {
    super(Objects.requireNonNull(referenceFront, "The reference front cannot be null"));
    if (referenceFront.length == 0) {
      throw new IllegalArgumentException("The reference front cannot be empty");
    }
  }

  /**
   * Constructor with reference point.
   * @param referencePoint The reference point
   * @throws IllegalArgumentException if referencePoint is null or empty
   */
  public Hypervolume(double[] referencePoint) {
    this(ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint));
  }
  
  /**
   * Gets the reference point used by this hypervolume indicator.
   * The reference point is derived from the reference front by taking the maximum value
   * in each objective dimension.
   *
   * @return The reference point
   * @throws IllegalStateException if no reference front has been set
   */
  public double[] getReferencePoint() {
    if (referenceFront == null || referenceFront.length == 0) {
      throw new IllegalStateException("No reference front has been set");
    }
    return ReferencePointUtils.extractReferencePointFromReferenceFront(referenceFront);
  }
  
  /**
   * Sets the reference point for this hypervolume indicator.
   * This will update the reference front accordingly.
   *
   * @param referencePoint The new reference point
   * @throws IllegalArgumentException if referencePoint is null or empty
   */
  public void setReferencePoint(double[] referencePoint) {
    this.referenceFront = ReferencePointUtils.createReferenceFrontFromReferencePoint(referencePoint);
  }

  @Override
  public String name() {
    return "HV";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false;
  }
}
