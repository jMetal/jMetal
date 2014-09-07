package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.problem.BinaryProblem;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class BinarySolutionImpl implements BinarySolution {
  private double [] objectives;
  private List<BitSet> variables;
  private BinaryProblem problem ;
  private double overallConstraintViolationDegree ;

  /** Constructor */
  public BinarySolutionImpl(BinaryProblem problem) {
    this.problem = problem ;
    objectives = new double[problem.getNumberOfVariables()] ;
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
  public void setObjective(int index, double value) {
    objectives[index] = value ;
  }

  @Override
  public double getObjective(int index) {
    return objectives[index];
  }

  @Override
  public BitSet getVariableValue(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariableValue(int index, BitSet value) {
    variables.set(index, value) ;
  }


  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.length;
  }

  @Override
  public double getOverallConstraintViolationDegree() {
    return overallConstraintViolationDegree ;
  }

  @Override
  public int getNumberOfBits(int index) {
    return variables.get(index).length() ;
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
