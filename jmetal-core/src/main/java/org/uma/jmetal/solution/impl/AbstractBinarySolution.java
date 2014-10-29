package org.uma.jmetal.solution.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.problem.BinaryProblem;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class AbstractBinarySolution extends AbstractGenericSolution<BitSet, BinaryProblem> implements BinarySolution {
  /** Constructor */
  public AbstractBinarySolution(BinaryProblem problem) {
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
  public AbstractBinarySolution(AbstractBinarySolution solution) {
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
    return new AbstractBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (BitSet binaryString : variables) {
      sum += binaryString.length() ;
    }

    return sum ;
  }

  @Override
  public String toString() {
    String result = "" ;
    for (BitSet var : variables) {
      for (int i = 0; i < var.size() ; i++) {
        if (var.get(i)) {
          result += "1" ;
        }
        else {
          result+= "0" ;
        }
      }
      result += "\t" ;
    }

    return result ;
  }
}
