package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Class representing problem OneMax. The problem consist of maximizing the
 * number of '1's in a binary string.
 */
@SuppressWarnings("serial")
public class OneMax extends AbstractBinaryProblem {
	private int bits ;
	
  /** Constructor */
  public OneMax() {
    this(256);
  }

  /** Constructor */
  public OneMax(Integer numberOfBits) {
    setNumberOfVariables(1);
    setNumberOfObjectives(1);
    setName("OneMax");

    bits = numberOfBits ;
  }

  @Override
  public int getBitsFromVariable(int index) {
  	if (index != 0) {
  		throw new JMetalException("Problem OneMax has only a variable. Index = " + index) ;
  	}
  	return bits ;
  }

  @Override
  public List<Integer> getListOfBitsPerVariable() {
    return Arrays.asList(bits) ;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(BinarySolution solution) {
    int counterOnes;

    counterOnes = 0;

    BitSet bitset = solution.getVariable(0) ;

    for (int i = 0; i < bitset.length(); i++) {
      if (bitset.get(i)) {
        counterOnes++;
      }
    }

    // OneMax is a maximization problem: multiply by -1 to minimize
    solution.setObjective(0, -1.0 * counterOnes);
  }
}


