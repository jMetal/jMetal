package org.uma.jmetal.algorithm.dynamic;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 * A {@link StepwiseAlgorithm} guided by one or more reference points of a decision maker, which can
 * change during the run.
 *
 * @param <S> the type of the solutions
 */
public interface InteractiveStepwiseAlgorithm<S extends Solution<?>>
    extends StepwiseAlgorithm<S> {
  /**
   * Sets the reference points; each one has a value per objective. It is called between
   * iterations.
   */
  void updateReferencePoints(List<List<Double>> referencePoints);

  /** The current reference points. */
  List<List<Double>> referencePoints();

  /** Whether the algorithm accepts the given number of reference points. */
  boolean acceptsNumberOfReferencePoints(int numberOfReferencePoints);
}
