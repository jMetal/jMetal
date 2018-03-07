package org.uma.jmetal.algorithm;

import org.uma.jmetal.util.referencePoint.ReferencePoint;

import java.util.List;

public interface InteractiveAlgorithm<S,R> extends Algorithm<R>{
  public void updatePointOfInterest(List<Double> newReferencePoints);
}
