package org.uma.jmetal.solution.integersolution.impl;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.util.DoubleVariableGenerator;
import org.uma.jmetal.solution.doublesolution.impl.util.impl.RandomDoubleVariableGenerator;
import org.uma.jmetal.solution.impl.AbstractSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.util.IntegerVariableGenerator;
import org.uma.jmetal.solution.integersolution.impl.util.impl.RandomIntegerVariableGenerator;
import org.uma.jmetal.solution.util.VariableGenerator;
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

  protected List<Integer> lowerBounds ;
  protected List<Integer> upperBounds ;

  /** Constructor */
  public DefaultIntegerSolution(
      int numberOfVariables,
      int numberOfObjectives,
      List<Integer> lowerBounds,
      List<Integer> upperBounds) {
    this(numberOfVariables, numberOfObjectives, lowerBounds, upperBounds, new RandomIntegerVariableGenerator()) ;
  }

  /** Constructor */
  public DefaultIntegerSolution(
      int numberOfVariables,
      int numberOfObjectives,
      List<Integer> lowerBounds,
      List<Integer> upperBounds,
      IntegerVariableGenerator generator) {
    super(numberOfVariables, numberOfObjectives) ;

    if (numberOfVariables != lowerBounds.size()) {
      throw new JMetalException("The number of lower bounds is not equal to the number of variables: " +
          lowerBounds.size() + " -> " +  numberOfVariables) ;
    } else if (numberOfVariables != upperBounds.size()) {
      throw new JMetalException("The number of upper bounds is not equal to the number of variables: " +
          upperBounds.size() + " -> " +  numberOfVariables) ;
    }

    this.lowerBounds = lowerBounds ;
    this.upperBounds = upperBounds ;

    generator.configure(numberOfVariables, lowerBounds, upperBounds);

    List<Integer> vars = generator.generate() ;
    for (int i = 0 ; i < numberOfVariables; i++) {
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

    lowerBounds = solution.lowerBounds ;
    upperBounds = solution.upperBounds ;

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Integer getUpperBound(int index) {
    return getUpperBound(index);
  }

  @Override
  public Integer getLowerBound(int index) {
    return getLowerBound(index) ;
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private void initializeIntegerVariables() {
    for (int i = 0 ; i < getNumberOfVariables(); i++) {
      Integer value = JMetalRandom.getInstance().nextInt(getLowerBound(i), getUpperBound(i));
      setVariableValue(i, value) ;
    }
  }
  
  
	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}
}
