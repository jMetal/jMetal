package org.uma.jmetal.solution.doublesolution.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.util.DoubleVariableGenerator;
import org.uma.jmetal.solution.doublesolution.impl.util.impl.RandomDoubleVariableGenerator;

import java.util.HashMap;
import java.util.List;

/**
 * Defines an implementation of a double solution. Each variable is given by a pair <lower bound, upper bound>.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleSolution extends AbstractSolution<Double> implements DoubleSolution {
  protected List<Pair<Double, Double>> bounds ;

  /** Constructor */
  public DefaultDoubleSolution(
      List<Pair<Double, Double>> bounds,
      int numberOfObjectives) {
    this(bounds, numberOfObjectives, new RandomDoubleVariableGenerator()) ;
  }

  /** Constructor */
  public DefaultDoubleSolution(
      List<Pair<Double, Double>> bounds,
      int numberOfObjectives,
      DoubleVariableGenerator generator) {
    super(bounds.size(), numberOfObjectives) ;

    this.bounds = bounds ;

    generator.configure(bounds);

    List<Double> vars = generator.generate() ;
    for (int i = 0 ; i < bounds.size(); i++) {
      setVariableValue(i, vars.get(i)); ;
    }
  }

  /** Copy constructor */
  public DefaultDoubleSolution(DefaultDoubleSolution solution) {
    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives()) ;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    bounds = solution.bounds ;

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Double getLowerBound(int index) {
    return this.bounds.get(index).getLeft() ;
  }

  @Override
  public Double getUpperBound(int index) {
    return this.bounds.get(index).getRight() ;
  }

  @Override
  public DefaultDoubleSolution copy() {
    return new DefaultDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
}
