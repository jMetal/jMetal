package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleBinaryProblem;
import org.uma.jmetal.solution.DoubleBinarySolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 * Description:
 *  - this solution contains an array of double value + a binary string
 *  - getNumberOfVariables() returns the number of double values + 1 (the string)
 *  - getNumberOfDoubleVariables() returns the number of double values
 *  - getNumberOfVariables() = getNumberOfDoubleVariables() + 1
 *  - the bitset is the last variable
 */
public class GenericDoubleBinarySolution
        extends AbstractGenericSolution<Object, DoubleBinaryProblem>
        implements DoubleBinarySolution {
  private int numberOfDoubleVariables ;

  /** Constructor */
  public GenericDoubleBinarySolution(DoubleBinaryProblem problem) {
    this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;
    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    initializeDoubleVariables();
    initializeBitSet() ;
    initializeObjectiveValues();
  }

  /** Copy constructor */
  public GenericDoubleBinarySolution(GenericDoubleBinarySolution solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (Double obj : solution.objectives) {
      objectives.add(new Double(obj)) ;
    }
    copyDoubleVariables(solution);
    copyBitSet(solution);

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;

    attributes = new HashMap(solution.attributes) ;
  }

  private void initializeDoubleVariables() {
    for (int i = 0 ; i < numberOfDoubleVariables; i++) {
      Double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i)) ;
      variables.add(value) ;
    }
  }

  private void initializeBitSet() {
    BitSet bitset = createNewBitSet(problem.getNumberOfBits()) ;
    variables.add(bitset) ;
  }

  private void copyDoubleVariables(GenericDoubleBinarySolution solution) {
    variables = new ArrayList<>() ;
    for (int i = 0 ; i < numberOfDoubleVariables; i++) {
      variables.add(new Double((Double) solution.getVariableValue(i))) ;
    }
  }

  private void copyBitSet(GenericDoubleBinarySolution solution) {
    BitSet bitset = (BitSet)solution.getVariableValue(solution.getNumberOfVariables()-1) ;
    variables.add(bitset.clone()) ;
  }

  @Override
  public int getNumberOfDoubleVariables() {
    return numberOfDoubleVariables;
  }

  @Override
  public Double getUpperBound(int index) {
    return (Double)problem.getUpperBound(index);
  }

  @Override
  public int getNumberOfBits() {
    return problem.getNumberOfBits();
  }

  @Override
  public Double getLowerBound(int index) {
    return (Double)problem.getLowerBound(index) ;
  }

  @Override
  public Solution copy() {
    return new GenericDoubleBinarySolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return variables.get(index).toString() ;
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
}
