package org.uma.jmetal.problem.integerproblem.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("serial")
public abstract class AbstractIntegerProblem extends AbstractGenericProblem<IntegerSolution>
  implements IntegerProblem {

  protected List<Bounds<Integer>> bounds;

  /**
   * @deprecated USe {@link #getBoundsForVariables()} instead.
   */
  @Deprecated
  public List<Pair<Integer, Integer>> getVariableBounds() {
    return bounds.stream().map(Bounds<Integer>::toPair).collect(Collectors.toList());
  }

  @Override
  @Deprecated
  public Integer getUpperBound(int index) {
    return getBoundsForVariables().get(index).getUpperBound();
  }

  @Override
  @Deprecated
  public Integer getLowerBound(int index) {
    return getBoundsForVariables().get(index).getLowerBound();
  }

  public void setVariableBounds(List<Integer> lowerBounds, List<Integer> upperBounds) {
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
  public IntegerSolution createSolution() {
    return new DefaultIntegerSolution(getNumberOfObjectives(), getBoundsForVariables());
  }

  @Override
  @Deprecated
  public List<Pair<Integer, Integer>> getBounds() {
    return getVariableBounds() ;
  }
  
  @Override
  public List<Bounds<Integer>> getBoundsForVariables() {
    return bounds;
  }
}
