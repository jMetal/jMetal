package org.uma.jmetal.solution.doublesolution.impl;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of the {@Link DoubleSolution} interface
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
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

    for (var i = 0; i < boundsList.size(); i++) {
      variables().set(i, JMetalRandom.getInstance().nextDouble(bounds.get(i).getLowerBound(), bounds.get(i).getUpperBound()));
    }
  }

  /**
   * Copy constructor
   */
  public DefaultDoubleSolution(@NotNull DefaultDoubleSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);

    var bound2 = solution.variables().size();
    for (var i2 = 0; i2 < bound2; i2++) {
      variables().set(i2, solution.variables().get(i2));
    }
    var bound1 = solution.objectives().length;
    for (var i1 = 0; i1 < bound1; i1++) {
      objectives()[i1] = solution.objectives()[i1];
    }
    var bound = solution.constraints().length;
    for (var i = 0; i < bound; i++) {
      constraints()[i] = solution.constraints()[i];
    }

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
