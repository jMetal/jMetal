package org.uma.jmetal.solution.integersolution.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;

/**
 * Defines an implementation of an integer solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerSolution extends AbstractSolution<Integer> implements IntegerSolution {
  protected List<Pair<Integer, Integer>> bounds;

  /** Constructor */
  public DefaultIntegerSolution(List<Pair<Integer, Integer>> bounds, int numberOfObjectives) {
    super(bounds.size(), numberOfObjectives);

    for (int i = 0; i < bounds.size(); i++) {
      setVariable(
          i, JMetalRandom.getInstance().nextInt(bounds.get(i).getLeft(), bounds.get(i).getRight()));
    }
  }

  /** Copy constructor */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives());

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      setVariable(i, solution.getVariable(i));
    }

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i));
    }

    bounds = solution.bounds;

    attributes = new HashMap<>(solution.attributes);
  }

  @Override
  public Integer getLowerBound(int index) {
    return this.bounds.get(index).getLeft();
  }

  @Override
  public Integer getUpperBound(int index) {
    return this.bounds.get(index).getRight();
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }
  }
