package org.uma.jmetal.problem.integerproblem.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
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
  public int getNumberOfVariables() {
    return bounds.size() ;
  }
  @Override
  public int getNumberOfObjectives() {
    return numberOfObjectives ;
  }

  public void setNumberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }
  public void setNumberOfConstraints(int numberOfConstraints) {
    this.numberOfConstraints = numberOfConstraints ;
  }
  @Override
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  @Override
  public int getNumberOfConstraints() {
    return numberOfConstraints ;
  }

  public void setVariableBounds(List<Integer> lowerBounds, @NotNull List<Integer> upperBounds) {
    Check.notNull(lowerBounds);
    Check.notNull(upperBounds);
    Check.that(
        lowerBounds.size() == upperBounds.size(),
        "The size of the lower bound list is not equal to the size of the upper bound list");

    List<Bounds<Integer>> list = new ArrayList<>();
    var bound = lowerBounds.size();
    for (var i = 0; i < bound; i++) {
      @NotNull Bounds<Integer> integerBounds = Bounds.create(lowerBounds.get(i), upperBounds.get(i));
      list.add(integerBounds);
    }
    bounds =
            list;
  }

  @Override
  public @NotNull IntegerSolution createSolution() {
    return new DefaultIntegerSolution(getVariableBounds(), getNumberOfObjectives(), getNumberOfConstraints());
  }

  @Override
  public List<Bounds<Integer>> getVariableBounds() {
    return bounds;
  }
}
