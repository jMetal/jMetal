package org.uma.jmetal.problem.doubleproblem.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.checking.Check;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("serial")
public abstract class AbstractDoubleProblem extends AbstractGenericProblem<DoubleSolution>
    implements DoubleProblem {

  protected List<Pair<Double, Double>> bounds;

  public List<Pair<Double, Double>> getVariableBounds() {
    return bounds;
  }

  @Override
  public Double getUpperBound(int index) {
    return getVariableBounds().get(index).getRight();
  }

  @Override
  public Double getLowerBound(int index) {
    return getVariableBounds().get(index).getLeft();
  }

  public void setVariableBounds(List<Double> lowerBounds, List<Double> upperBounds) {
    Check.isNotNull(lowerBounds);
    Check.isNotNull(upperBounds);
    Check.that(
        lowerBounds.size() == upperBounds.size(),
        "The size of the lower bound list is not equal to the size of the upper bound list");

    bounds =
        IntStream.range(0, lowerBounds.size())
            .mapToObj(i -> new ImmutablePair<>(lowerBounds.get(i), upperBounds.get(i)))
            .collect(Collectors.toList());
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(bounds, getNumberOfObjectives(), getNumberOfConstraints());
  }

  @Override
  public List<Pair<Double, Double>> getBounds() {
    return bounds;
  }
}
