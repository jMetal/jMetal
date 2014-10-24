package org.uma.jmetal.solution.impl;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.problem.BinaryProblem;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class BinarySolutionImpl extends GenericSolutionImpl<BitSet, BinaryProblem> implements BinarySolution {
  /** Constructor */
  public BinarySolutionImpl(BinaryProblem problem) {
    this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables.add(createNewBitSet(problem.getNumberOfBits(i)));
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
  }

  /** Copy constructor */
  public BinarySolutionImpl(BinarySolutionImpl solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (Double obj : solution.objectives) {
      objectives.add(new Double(obj)) ;
    }
    variables = new ArrayList<>() ;
    for (BitSet var : solution.variables) {
      variables.add((BitSet)var.clone()) ;
    }
    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    attributes = new HashMap(solution.attributes) ;
  }

  private BitSet createNewBitSet(int numberOfBits) {
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
  public int getNumberOfBits(int index) {
    return variables.get(index).length() ;
  }

  @Override
  public Solution<?> copy() {
    return new BinarySolutionImpl(this);
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
