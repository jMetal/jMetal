package org.uma.jmetal.problem.integerproblem.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Abstract class to be extended by implementations of interface {@link IntegerProblem >}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public abstract class AbstractIntegerProblem extends AbstractGenericProblem<IntegerSolution>
    implements IntegerProblem {

  protected List<Bounds<Integer>> bounds;

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
    return new DefaultIntegerSolution(getNumberOfObjectives(), getNumberOfConstraints(),
        getVariableBounds());
  }

  @Override
  public List<Bounds<Integer>> getVariableBounds() {
    return bounds;
  }
}
