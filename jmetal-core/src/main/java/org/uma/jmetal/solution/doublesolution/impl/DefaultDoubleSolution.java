package org.uma.jmetal.solution.doublesolution.impl;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of the {@link DoubleSolution} interface
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class DefaultDoubleSolution extends AbstractSolution<Double> implements DoubleSolution {
  protected List<Bounds<Double>> bounds;

  /**
   * Constructor
   */
  public DefaultDoubleSolution(List<Bounds<Double>> boundsList,
          int numberOfObjectives,
          int numberOfConstraints) {
    super(boundsList.size(), numberOfObjectives, numberOfConstraints);

    this.bounds = boundsList;

    for (int i = 0; i < boundsList.size(); i++) {
      variables().set(i, JMetalRandom.getInstance().nextDouble(bounds.get(i).getLowerBound(), bounds.get(i).getUpperBound()));
    }
  }

  /**
   * Copy constructor
   */
  public DefaultDoubleSolution(DefaultDoubleSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);

    IntStream.range(0, solution.variables().size()).forEach(i -> variables().set(i, solution.variables().get(i)));
    IntStream.range(0, solution.objectives().length).forEach(i -> objectives()[i] = solution.objectives()[i]);
    IntStream.range(0, solution.constraints().length).forEach(i -> constraints()[i] = solution.constraints()[i]);

    bounds = solution.bounds;
    attributes = new HashMap<>(solution.attributes);
  }

  @Override
  public Bounds<Double> getBounds(int index) {
    return this.bounds.get(index);
  }

  @Override
  public DefaultDoubleSolution copy() {
    return new DefaultDoubleSolution(this);
  }
}
