package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Abstract class to be extended by implementations of interface {@link DoubleProblem>}, which must
 * implement the {@link #evaluate} method.
 *
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public abstract class AbstractDoubleProblem implements DoubleProblem {
  protected List<Bounds<Double>> bounds;
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

  @Override
  public int numberOfConstraints() {
    return numberOfConstraints ;
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

  public void variableBounds(List<Double> lowerBounds, List<Double> upperBounds) {
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
    return new DefaultDoubleSolution(bounds, numberOfObjectives(), numberOfConstraints());
  }
  @Override
  public List<Bounds<Double>> variableBounds() {
    return bounds;
  }
}
