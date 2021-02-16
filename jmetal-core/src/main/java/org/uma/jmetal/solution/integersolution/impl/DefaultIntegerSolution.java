package org.uma.jmetal.solution.integersolution.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines an implementation of an integer solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerSolution extends AbstractSolution<Integer> implements IntegerSolution {
  protected List<Bounds<Integer>> bounds;

  /**
   * Constructor
   * 
   * @deprecated Use {@link #DefaultIntegerSolution(int, int, List)} instead.
   */
  @Deprecated
  public DefaultIntegerSolution(List<Pair<Integer, Integer>> bounds, int numberOfObjectives, int numberOfConstraints) {
    this(numberOfObjectives, numberOfConstraints, bounds.stream().map(Bounds::fromPair).collect(Collectors.toList()));
  }

  /** Constructor */
  public DefaultIntegerSolution(int numberOfObjectives, int numberOfConstraints, List<Bounds<Integer>> boundsList) {
    super(boundsList.size(), numberOfObjectives, numberOfConstraints);

    this.bounds = boundsList ;

    for (int i = 0; i < boundsList.size(); i++) {
      Bounds<Integer> bounds = boundsList.get(i);
      variables().set(
          i, JMetalRandom.getInstance().nextInt(bounds.getLowerBound(), bounds.getUpperBound()));
    }
  }

  /**
   * Constructor
   * 
   * @deprecated Use {@link #DefaultIntegerSolution(int, List)} instead.
   */
  @Deprecated
  public DefaultIntegerSolution(
      List<Pair<Integer, Integer>> bounds,
      int numberOfObjectives) {
    this(bounds, numberOfObjectives, 0) ;
  }

  /** Constructor */
  public DefaultIntegerSolution(
      int numberOfObjectives,
      List<Bounds<Integer>> bounds) {
    this(numberOfObjectives, 0, bounds) ;
  }

  /** Copy constructor */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);

    for (int i = 0; i < solution.variables().size(); i++) {
      variables().set(i, solution.variables().get(i));
    }

    for (int i = 0; i < solution.objectives().length; i++) {
      setObjective(i, solution.objectives()[i]) ;
    }

    for (int i = 0; i < solution.constraints().length; i++) {
      constraints()[i] =  solution.constraints()[i];
    }

    bounds = solution.bounds;

    attributes = new HashMap<>(solution.attributes);
  }

  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Override
  @Deprecated
  public Integer getLowerBound(int index) {
    return this.bounds.get(index).getLowerBound();
  }

  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getUpperBound()
   *             .getUpperBound()} instead.
   */
  @Override
  @Deprecated
  public Integer getUpperBound(int index) {
    return this.bounds.get(index).getUpperBound();
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
