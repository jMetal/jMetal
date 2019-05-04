package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines an implementation of a binary solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultBinarySolution
    extends AbstractGenericSolution<BinarySet, BinaryProblem>
    implements BinarySolution {

  /** Constructor */
  public DefaultBinarySolution(BinaryProblem problem) {
    super(variablesInitializer(problem, JMetalRandom.getInstance()), problem.getNumberOfObjectives()) ;
  }

  /** Copy constructor */
  public DefaultBinarySolution(DefaultBinarySolution solution) {
    super(solution.getVariables().stream().map(s -> (BinarySet) s.clone()).collect(Collectors.toList()),
        Arrays.copyOf(solution.getObjectives(), solution.getObjectives().length),
        new HashMap<>(solution.getAttributes()));
  }

  private static BinarySet createNewBitSet(int numberOfBits, JMetalRandom randomGenerator) {
    BinarySet bitSet = new BinarySet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = randomGenerator.nextDouble() ;
      if (rnd < 0.5) {
        bitSet.set(i);
      } else {
        bitSet.clear(i);
      }
    }
    return bitSet ;
  }

  @Override
  public int getNumberOfBits(int index) {
    return getVariableValue(index).getBinarySetLength() ;
  }

  @Override
  public DefaultBinarySolution copy() {
    return new DefaultBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (int i = 0; i < getNumberOfVariables(); i++) {
      sum += getVariableValue(i).getBinarySetLength() ;
    }

    return sum ;
  }

  @Override
  public String getVariableValueString(int index) {
    String result = "" ;
    for (int i = 0; i < getVariableValue(index).getBinarySetLength() ; i++) {
      if (getVariableValue(index).get(i)) {
        result += "1" ;
      }
      else {
        result+= "0" ;
      }
    }
    return result ;
  }
  
  private static List<BinarySet> variablesInitializer(BinaryProblem problem, JMetalRandom randomGenerator) {
    int numberOfVariables = problem.getNumberOfVariables();
    List<BinarySet> variables = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      variables.add(createNewBitSet(problem.getNumberOfBits(i), randomGenerator));
    }
    return variables;
  }
}
