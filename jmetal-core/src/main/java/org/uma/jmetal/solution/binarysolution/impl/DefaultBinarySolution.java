package org.uma.jmetal.solution.binarysolution.impl;

import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.impl.AbstractSolution;
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
  public DefaultBinarySolution(int numberOfVariables, int numberOfObjectives, List<Integer> bitsPerVariable) {
    super(numberOfVariables, numberOfObjectives) ;
    this.bitsPerVariable = bitsPerVariable ;

    initializeBinaryVariables();
  }

  /** Copy constructor */
  public DefaultBinarySolution(DefaultBinarySolution solution) {
    super(solution.getNumberOfVariables(), solution.getNumberOfObjectives()) ;

    this.bitsPerVariable = solution.bitsPerVariable ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariableValue(i, (BinarySet) solution.getVariableValue(i).clone());
    }

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  private BinarySet createNewBitSet(int numberOfBits) {
    BinarySet bitSet = new BinarySet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = JMetalRandom.getInstance().nextDouble() ;
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
    return getVariableValue(index).getBinarySetLength() ;
  }

  @Override
  public DefaultBinarySolution copy() {
    return new DefaultBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (int i = 0; i < getNumberOfVariables(); i++) {
      sum += getVariableValue(i).getBinarySetLength() ;
    }

    return sum ;
  }

  @Override
  public String getVariableValueString(int index) {
    String result = "" ;
    for (int i = 0; i < getVariableValue(index).getBinarySetLength() ; i++) {
      if (getVariableValue(index).get(i)) {
        result += "1" ;
      }
      else {
        result+= "0" ;
      }
    }
    return result ;
  }
  
  private void initializeBinaryVariables() {
    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariableValue(i, createNewBitSet(bitsPerVariable.get(i)));
    }
  }

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}
}
