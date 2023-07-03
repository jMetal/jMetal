package org.uma.jmetal.problem.multiobjective;

import java.util.BitSet;
import java.util.List;
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
    bits = numberOfBits ;
  }

  @Override
  public int numberOfVariables() {
    return 1 ;
  }
  @Override
  public int numberOfObjectives() {
    return 2 ;
  }
  @Override
  public int numberOfConstraints() {
    return 0 ;
  }

  @Override
  public String name() {
    return "OneZeroMax" ;
  }

  @Override
  public List<Integer> bitsPerVariable() {
    return List.of(bits);
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(bitsPerVariable(), numberOfObjectives()) ;
  }

  /** Evaluate() method */
  @Override
    public BinarySolution evaluate(BinarySolution solution) {
    int counterOnes;
    int counterZeroes;

    counterOnes = 0;
    counterZeroes = 0;

    BitSet bitset = solution.variables().get(0) ;

    for (int i = 0; i < bitset.length(); i++) {
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
