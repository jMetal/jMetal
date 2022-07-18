package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.ArrayList;
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
  public void setVariableBounds(List<Double> lowerBounds, List<Double> upperBounds) {
    Check.notNull(lowerBounds);
    Check.notNull(upperBounds);
    Check.that(
        lowerBounds.size() == upperBounds.size(),
        "The size of the lower bound list is not equal to the size of the upper bound list");

    List<Bounds<Double>> list = new ArrayList<>();
    int bound = lowerBounds.size();
    for (int i = 0; i < bound; i++) {
      Bounds<Double> doubleBounds = Bounds.create(lowerBounds.get(i), upperBounds.get(i));
      list.add(doubleBounds);
    }
    bounds =
            list;
  }
  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(bounds, getNumberOfObjectives(), getNumberOfConstraints());
  }
  @Override
  public List<Bounds<Double>> getVariableBounds() {
    return bounds;
  }
}
