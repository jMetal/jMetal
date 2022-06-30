package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

@SuppressWarnings("serial")
public abstract class AbstractDoubleProblem extends AbstractGenericProblem<DoubleSolution>
    implements DoubleProblem {

  protected List<Bounds<Double>> bounds;

  public void setVariableBounds(List<Double> lowerBounds, List<Double> upperBounds) {
    Check.notNull(lowerBounds);
    Check.notNull(upperBounds);
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
  public List<Bounds<Double>> getBoundsForVariables() {
    return bounds;
  }
}
