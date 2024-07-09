package org.uma.jmetal.algorithm;

import java.util.List;

public interface InteractiveAlgorithm<S,R> extends Algorithm<R> {
  void updatePointsOfInterest(List<Double> newReferencePoints);
}
