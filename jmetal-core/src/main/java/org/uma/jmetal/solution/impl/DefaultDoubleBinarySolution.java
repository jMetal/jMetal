package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleBinaryProblem;
import org.uma.jmetal.solution.DoubleBinarySolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.BitSet;
import java.util.Map;
import java.util.function.Function;

/**
 * Description:
 *  - this solution contains an array of double value + a binary string
 *  - getNumberOfVariables() returns the number of double values + 1 (the string)
 *  - getNumberOfDoubleVariables() returns the number of double values
 *  - getNumberOfVariables() = getNumberOfDoubleVariables() + 1
 *  - the bitset is the last variable
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleBinarySolution
    extends AbstractGenericSolution<Object, DoubleBinaryProblem<?>>
    implements DoubleBinarySolution {
  private int numberOfDoubleVariables ;

  /** Constructor */
  public DefaultDoubleBinarySolution(DoubleBinaryProblem<?> problem) {
    super(problem, variableInitializer(problem, JMetalRandom.getInstance())) ;

    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;
  }

  /** Copy constructor */
  public DefaultDoubleBinarySolution(DefaultDoubleBinarySolution solution) {
    super(solution.problem, solution) ;
  }
  
  private static Function<Integer, Object> variableInitializer(DoubleBinaryProblem<?> problem,
      JMetalRandom randomGenerator) {
    return i -> {
      if (i < problem.getNumberOfDoubleVariables()) {
        Double lowerBound = (Double) problem.getLowerBound(i);
        Double upperBound = (Double) problem.getUpperBound(i);
        return randomGenerator.nextDouble(lowerBound, upperBound);
      } else {
        return createNewBitSet(problem.getNumberOfBits(), randomGenerator);
      }
    };
  }

  @Override
  public int getNumberOfDoubleVariables() {
    return numberOfDoubleVariables;
  }

  @Override
  public Double getUpperBound(int index) {
    return (Double)problem.getUpperBound(index);
  }

  @Override
  public int getNumberOfBits() {
    return problem.getNumberOfBits();
  }

  @Override
  public Double getLowerBound(int index) {
    return (Double)problem.getLowerBound(index) ;
  }

  @Override
  public DefaultDoubleBinarySolution copy() {
    return new DefaultDoubleBinarySolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }

  private static BitSet createNewBitSet(int numberOfBits, JMetalRandom randomGenerator) {
    BitSet bitSet = new BitSet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      if (randomGenerator.nextDouble() < 0.5) {
        bitSet.set(i, true);
      } else {
        bitSet.set(i, false);
      }
    }
    return bitSet ;
  }
  
  	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}
}
