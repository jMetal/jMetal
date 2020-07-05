package org.uma.jmetal.util.metadata.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.metadata.Metadata;

public class DoubleUpperBoundsMetadata<S extends Solution<Double>> implements Metadata.RW<S, List<Double>> {

  @SuppressWarnings("unchecked")
  @Override
  public List<Double> read(S solution) {
    if (solution instanceof DoubleSolution) {
      DoubleSolution doubleSolution = (DoubleSolution) solution;
      return IntStream.range(0, doubleSolution.getNumberOfVariables())//
          .mapToObj(i -> doubleSolution.getUpperBound(i))//
          .collect(Collectors.toList());
    } else {
      List<Double> bounds = (List<Double>) solution.getAttribute(this);
      if (bounds == null) {
        throw new IllegalStateException("No upper bounds defined");
      }
      return bounds;
    }
  }

  @Override
  public void write(S solution, List<Double> bounds) {
    solution.setAttribute(this, bounds);
  }
}
