package org.uma.jmetal.problem.singleobjective;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class representing problem OneMax. The problem consist of maximizing the number of '1's in a
 * binary string.
 */
@SuppressWarnings("serial")
public class OneMax extends AbstractBinaryProblem {
  private int bits;

  /** Constructor */
  public OneMax() {
    this(256);
  }

  /** Constructor */
  public OneMax(Integer numberOfBits) {
    setNumberOfVariables(1);
    setNumberOfObjectives(1);
    setName("OneMax");

    bits = numberOfBits;
  }

  @Override
  public int getBitsFromVariable(int index) {
    Check.that(index == 0, "Problem OneMax has only a variable. Index =" + index) ;
    return bits;
  }

  @Override
  public List<Integer> getListOfBitsPerVariable() {
    return Arrays.asList(bits);
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives());
  }

  /** Evaluate() method */
  @Override
  public BinarySolution evaluate(BinarySolution solution) {
    int counterOnes;

    counterOnes = 0;

    BitSet bitset = solution.variables().get(0);

      long count = 0L;
      int bound = bitset.length();
      for (int i = 0; i < bound; i++) {
          if (bitset.get(i)) {
              count++;
          }
      }
      counterOnes += count;

    // OneMax is a maximization problem: multiply by -1 to minimize
    solution.objectives()[0] = -1.0 * counterOnes;

    return solution;
  }
}
