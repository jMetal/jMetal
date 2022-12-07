package org.uma.jmetal.problem.integerproblem.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public abstract class AbstractIntegerProblem implements IntegerProblem{
  protected List<Bounds<Integer>> bounds;
  protected int numberOfObjectives ;
  protected int numberOfConstraints;
  protected String name ;
  @Override
  public int numberOfVariables() {
    return bounds.size() ;
  }
  @Override
  public int numberOfObjectives() {
    return numberOfObjectives ;
  }

  public void numberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }
  public void numberOfConstraints(int numberOfConstraints) {
    this.numberOfConstraints = numberOfConstraints ;
  }
  @Override
  public String name() {
    return name;
  }
  public void name(String name) {
    this.name = name;
  }
  @Override
  public int numberOfConstraints() {
    return numberOfConstraints ;
  }

  public void variableBounds(List<Integer> lowerBounds, List<Integer> upperBounds) {
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
    return new DefaultIntegerSolution(variableBounds(), numberOfObjectives(), numberOfConstraints());
  }

  @Override
  public List<Bounds<Integer>> variableBounds() {
    return bounds;
  }
}
