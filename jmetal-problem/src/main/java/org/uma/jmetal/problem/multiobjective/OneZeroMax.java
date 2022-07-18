package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem OneZeroMax. The problem consist of maximizing the
 * number of '1's and '0's in a binary string.
 */
@SuppressWarnings("serial")
public class OneZeroMax extends AbstractBinaryProblem {
  private int bits ;
  /** Constructor */
  public OneZeroMax() throws JMetalException {
    this(512);
  }

  /** Constructor */
  public OneZeroMax(Integer numberOfBits) throws JMetalException {
    setNumberOfVariables(1);
    setNumberOfObjectives(2);
    setName("OneZeroMax");

    bits = numberOfBits ;
  }

  @Override
  public @NotNull List<Integer> getListOfBitsPerVariable() {
    return Arrays.asList(bits);
  }

  @Override
  public int getBitsFromVariable(int index) {
  	if (index != 0) {
  		throw new JMetalException("Problem OneZeroMax has only a variable. Index = " + index) ;
  	}
  	return bits ;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
  }

  /** Evaluate() method */
  @Override
    public BinarySolution evaluate(@NotNull BinarySolution solution) {

    var counterOnes = 0;
    var counterZeroes = 0;

    BitSet bitset = solution.variables().get(0) ;

    for (var i = 0; i < bitset.length(); i++) {
      if (bitset.get(i)) {
        counterOnes++;
      } else {
        counterZeroes++;
      }
    }

    // OneZeroMax is a maximization problem: multiply by -1 to minimize
    solution.objectives()[0] = -1.0 * counterOnes ;
    solution.objectives()[1] = -1.0 * counterZeroes ;

    return solution ;
  }
}
