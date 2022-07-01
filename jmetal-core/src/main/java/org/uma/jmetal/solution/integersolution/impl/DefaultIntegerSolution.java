package org.uma.jmetal.solution.integersolution.impl;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
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

    IntStream.range(0, bounds.size()).forEach(i -> variables().set(
            i, JMetalRandom.getInstance().nextInt(this.bounds.get(i).getLowerBound(), this.bounds.get(i).getUpperBound())));
  }

  /**
   * Copy constructor
   */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);

    IntStream.range(0, solution.variables().size()).forEach(i -> variables().set(i, solution.variables().get(i)));
    IntStream.range(0, solution.objectives().length).forEach(i -> objectives()[i] = solution.objectives()[i]);
    IntStream.range(0, solution.constraints().length).forEach(i -> constraints()[i] = solution.constraints()[i]);

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
