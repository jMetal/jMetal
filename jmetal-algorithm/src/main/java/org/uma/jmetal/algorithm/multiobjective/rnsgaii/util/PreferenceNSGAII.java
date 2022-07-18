package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;

public class PreferenceNSGAII<S extends Solution<?>> {
  private List<Double> interestPoint;
  private List<Double> upperBounds = null;
  private List<Double> lowerBounds = null;
  private @Nullable List<Double> weights = null;

  public PreferenceNSGAII(List<Double> weights) {
    this.weights = weights;
  }

  public void updatePointOfInterest(List<Double> newInterestPoint) {
    interestPoint = newInterestPoint;
  }

  public @NotNull Double evaluate(S solution) {

      @NotNull List<Double> objectiveValues = new ArrayList<>(solution.objectives().length);
      for (double v : solution.objectives()) {
          Double aDouble = v;
          objectiveValues.add(aDouble);
      }

      double normalizeDiff = 0.0D;
    double distance = 0.0D;
    for (int i = 0; i < solution.objectives().length; i++) {
      if (this.upperBounds != null && this.lowerBounds != null) {
        normalizeDiff =
            (solution.objectives()[i] - this.interestPoint.get(i))
                / (this.upperBounds.get(i) - this.lowerBounds.get(i));
      } else {
        normalizeDiff = solution.objectives()[i] - this.interestPoint.get(i);
      }
      distance += weights.get(i) * Math.pow(normalizeDiff, 2.0D);
    }

    return Math.sqrt(distance);
  }

  public int getSize() {
    return this.weights.size();
  }

  public void setUpperBounds(List<Double> upperBounds) {
    this.upperBounds = upperBounds;
  }

  public void setLowerBounds(List<Double> lowerBounds) {
    this.lowerBounds = lowerBounds;
  }
}
