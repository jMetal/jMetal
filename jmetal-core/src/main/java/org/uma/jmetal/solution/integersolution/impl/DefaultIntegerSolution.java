package org.uma.jmetal.solution.integersolution.impl;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of the {@Link IntegerSolution} interface
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerSolution extends AbstractSolution<Integer> implements IntegerSolution {
  protected List<Bounds<Integer>> bounds;

  /**
   * Constructor
   */
  public DefaultIntegerSolution(List<Bounds<Integer>> boundsList, int numberOfObjectives, int numberOfConstraints) {
    super(boundsList.size(), numberOfObjectives, numberOfConstraints);

    this.bounds = boundsList;

    int bound = bounds.size();
    for (int i = 0; i < bound; i++) {
      variables().set(
              i, JMetalRandom.getInstance().nextInt(this.bounds.get(i).getLowerBound(), this.bounds.get(i).getUpperBound()));
    }
  }

  /**
   * Copy constructor
   */
  public DefaultIntegerSolution(@NotNull DefaultIntegerSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);

    int bound2 = solution.variables().size();
    for (int i2 = 0; i2 < bound2; i2++) {
      variables().set(i2, solution.variables().get(i2));
    }
    int bound1 = solution.objectives().length;
    for (int i1 = 0; i1 < bound1; i1++) {
      objectives()[i1] = solution.objectives()[i1];
    }
    int bound = solution.constraints().length;
    for (int i = 0; i < bound; i++) {
      constraints()[i] = solution.constraints()[i];
    }

    bounds = solution.bounds;

    attributes = new HashMap<>(solution.attributes);
  }

  @Override
  public Bounds<Integer> getBounds(int index) {
    return this.bounds.get(index);
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }
}
