package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.problem.BinaryProblem;
import org.uma.jmetal3.problem.ContinuousProblem;
import org.uma.jmetal3.problem.impl.GenericProblemImpl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class BinarySolutionImpl extends GenericSolutionImpl<BitSet, BinaryProblem> implements BinarySolution {

  /** Constructor */
  public BinarySolutionImpl(BinaryProblem problem) {
    this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfVariables()) ;
    variables = new ArrayList<>(problem.getNumberOfObjectives()) ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables.add(createNewBitSet(problem.getNumberOfBits(i)));
    }
  }

  private BitSet createNewBitSet(int numberOfBits) {
    BitSet bitSet = new BitSet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      if (PseudoRandom.randDouble() < 0.5) {
        bitSet.set(i, true);
      } else {
        bitSet.set(i, false);
      }
    }

    return bitSet ;
  }

  @Override
  public int getNumberOfBits(int index) {
    return variables.get(index).length() ;
  }

  @Override
  public Solution<?> copy() {
    return new BinarySolutionImpl(problem);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (BitSet binaryString : variables) {
      sum += binaryString.length() ;
    }

    return sum ;
  }
}
