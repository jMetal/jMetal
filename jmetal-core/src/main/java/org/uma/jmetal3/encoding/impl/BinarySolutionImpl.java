package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.encoding.attributes.Attributes;
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
  public BinarySolutionImpl(BinaryProblem problem, Attributes attr) {
    this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables.add(createNewBitSet(problem.getNumberOfBits(i)));
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
