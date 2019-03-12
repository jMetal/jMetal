package org.uma.jmetal.solution.integersolution.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.util.impl.RandomDoubleVariableGenerator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.util.IntegerVariableGenerator;
import org.uma.jmetal.solution.integersolution.impl.util.impl.RandomIntegerVariableGenerator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines an implementation of an integer solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerSolution
    extends AbstractSolution<Integer>
    implements IntegerSolution {
  protected List<Pair<Integer, Integer>> bounds ;

  /** Constructor */
  public DefaultIntegerSolution(
      List<Pair<Integer, Integer>> bounds,
      int numberOfObjectives) {
    this(bounds, numberOfObjectives, new RandomIntegerVariableGenerator()) ;
  }

  /** Constructor */
  public DefaultIntegerSolution(
      List<Pair<Integer, Integer>> bounds,
      int numberOfObjectives,
      IntegerVariableGenerator generator) {
    super(bounds.size(), numberOfObjectives) ;

    generator.configure(bounds);

    List<Integer> vars = generator.generate() ;
    for (int i = 0 ; i < bounds.size(); i++) {
      setVariableValue(i, vars.get(i)); ;
    }
  }

  /** Copy constructor */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives()) ;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    bounds = solution.bounds ;

    attributes = new HashMap<>(solution.attributes) ;
  }

  @Override
  public Integer getLowerBound(int index) {
    return this.bounds.get(index).getLeft() ;
  }

  @Override
  public Integer getUpperBound(int index) {
    return this.bounds.get(index).getRight() ;
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
}
