package org.uma.jmetal.solution.doublesolution.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines an implementation of a double solution. Each variable is given by a pair <lower bound, upper bound>.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleSolution extends AbstractSolution<Double> implements DoubleSolution {
  protected List<Bounds<Double>> bounds ;

  /**
   * Constructor
   * 
   * @deprecated Use {@link #DefaultDoubleSolution(int, int, List)} instead.
   */
  @Deprecated
  public DefaultDoubleSolution(
      List<Pair<Double, Double>> bounds,
      int numberOfObjectives,
      int numberOfConstraints) {
    this(numberOfObjectives, numberOfConstraints, bounds.stream().map(Bounds::fromPair).collect(Collectors.toList())) ;
  }

  /** Constructor */
  public DefaultDoubleSolution(
      int numberOfObjectives,
      int numberOfConstraints,
      List<Bounds<Double>> boundsList) {
    super(boundsList.size(), numberOfObjectives, numberOfConstraints) ;

    this.bounds = boundsList ;

    for (int i = 0 ; i < boundsList.size(); i++) {
      Bounds<Double> bounds = boundsList.get(i);
      setVariable(i, JMetalRandom.getInstance().nextDouble(bounds.getLowerBound(), bounds.getUpperBound())); ;
    }
  }

  /**
   * Constructor
   * 
   * @deprecated Use {@link #DefaultDoubleSolution(int, List)} instead.
   */
  @Deprecated
  public DefaultDoubleSolution(
      List<Pair<Double, Double>> bounds,
      int numberOfObjectives) {
    this(bounds, numberOfObjectives, 0) ;
  }

  /** Constructor */
  public DefaultDoubleSolution(
      int numberOfObjectives,
      List<Bounds<Double>> boundsList) {
    this(numberOfObjectives, 0, boundsList) ;
  }

  /** Copy constructor */
  public DefaultDoubleSolution(DefaultDoubleSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length) ;

    for (int i = 0; i < solution.variables().size(); i++) {
      setVariable(i, solution.getVariable(i));
    }

    for (int i = 0; i < solution.objectives().length; i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    for (int i = 0; i < solution.constraints().length; i++) {
      setConstraint(i, solution.constraints()[i]);
    }

    bounds = solution.bounds ;
    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Deprecated
  @Override
  public Double getLowerBound(int index) {
    return this.bounds.get(index).getLowerBound() ;
  }

  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getUpperBound()
   *             .getUpperBound()} instead.
   */
  @Deprecated
  @Override
  public Double getUpperBound(int index) {
    return this.bounds.get(index).getUpperBound() ;
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
