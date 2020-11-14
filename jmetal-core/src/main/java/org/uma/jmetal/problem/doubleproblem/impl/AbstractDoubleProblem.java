package org.uma.jmetal.problem.doubleproblem.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("serial")
public abstract class AbstractDoubleProblem extends AbstractGenericProblem<DoubleSolution>
    implements DoubleProblem {

  protected List<Bounds<Double>> bounds;

  /**
   * @deprecated Use {@link #getBoundsForVariables()} instead.
   */
  @Deprecated
  public List<Pair<Double, Double>> getVariableBounds() {
    return bounds.stream().map(Bounds<Double>::toPair).collect(Collectors.toList());
  }

  @Override
  @Deprecated
  public Double getUpperBound(int index) {
    return getBoundsForVariables().get(index).getUpperBound();
  }

  @Override
  @Deprecated
  public Double getLowerBound(int index) {
    return getBoundsForVariables().get(index).getLowerBound();
  }

  public void setVariableBounds(List<Double> lowerBounds, List<Double> upperBounds) {
    Check.isNotNull(lowerBounds);
    Check.isNotNull(upperBounds);
    Check.that(
        lowerBounds.size() == upperBounds.size(),
        "The size of the lower bound list is not equal to the size of the upper bound list");

    bounds =
        IntStream.range(0, lowerBounds.size())
            .mapToObj(i -> Bounds.create(lowerBounds.get(i), upperBounds.get(i)))
            .collect(Collectors.toList());
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(getNumberOfObjectives(), getNumberOfConstraints(), bounds);
  }

  @Override
  @Deprecated
  public List<Pair<Double, Double>> getBounds() {
    return getVariableBounds();
  }
  
  @Override
  public List<Bounds<Double>> getBoundsForVariables() {
    return bounds;
  }
}
