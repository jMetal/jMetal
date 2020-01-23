package org.uma.jmetal.solution.binarysolution.impl;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines an implementation of a binary solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultBinarySolution
    extends AbstractSolution<BinarySet>
    implements BinarySolution {

  protected List<Integer> bitsPerVariable ;

  /** Constructor */
  public DefaultBinarySolution(List<Integer> bitsPerVariable, int numberOfObjectives) {
    super(bitsPerVariable.size(), numberOfObjectives) ;
    this.bitsPerVariable = bitsPerVariable ;

    initializeBinaryVariables(JMetalRandom.getInstance());
  }

  /** Copy constructor */
  public DefaultBinarySolution(DefaultBinarySolution solution) {
    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives()) ;

    this.bitsPerVariable = solution.bitsPerVariable ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariable(i, (BinarySet) solution.getVariable(i).clone());
    }

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      setConstraint(i, solution.getConstraint(i));
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  private static BinarySet createNewBitSet(int numberOfBits, JMetalRandom randomGenerator) {
    BinarySet bitSet = new BinarySet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = randomGenerator.nextDouble() ;
      if (rnd < 0.5) {
        bitSet.set(i);
      } else {
        bitSet.clear(i);
      }
    }
    return bitSet ;
  }

  @Override
  public int getNumberOfBits(int index) {
    return getVariable(index).getBinarySetLength() ;
  }

  @Override
  public DefaultBinarySolution copy() {
    return new DefaultBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (int i = 0; i < getNumberOfVariables(); i++) {
      sum += getVariable(i).getBinarySetLength() ;
    }

    return sum ;
  }
  
  private void initializeBinaryVariables(JMetalRandom randomGenerator) {
    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariable(i, createNewBitSet(bitsPerVariable.get(i), randomGenerator));
    }
  }

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}
}
