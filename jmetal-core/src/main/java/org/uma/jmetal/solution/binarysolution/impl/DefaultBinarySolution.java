package org.uma.jmetal.solution.binarysolution.impl;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This defines an implementation of a binary solution. These solutions are composed of a number
 * of variables containing {@link BinarySet} objects.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultBinarySolution
        extends AbstractSolution<BinarySet>
        implements BinarySolution {

  protected List<Integer> bitsPerVariable;

  /**
   * Constructor
   */
  public DefaultBinarySolution(List<Integer> bitsPerVariable, int numberOfObjectives) {
    this(bitsPerVariable, numberOfObjectives, 0);
  }

  /**
   * Constructor
   */
  public DefaultBinarySolution(List<Integer> bitsPerVariable, int numberOfObjectives, int numberOfConstraints) {
    super(bitsPerVariable.size(), numberOfObjectives, numberOfConstraints);
    this.bitsPerVariable = bitsPerVariable;

    initializeBinaryVariables(JMetalRandom.getInstance());
  }

  /**
   * Copy constructor
   */
  public DefaultBinarySolution(DefaultBinarySolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);

    this.bitsPerVariable = solution.bitsPerVariable;

    for (int i = 0; i < variables().size(); i++) {
      setVariable(i, (BinarySet) solution.getVariable(i).clone());
    }

    for (int i = 0; i < objectives().length; i++) {
      setObjective(i, solution.objectives()[i]);
    }

    for (int i = 0; i < constraints().length; i++) {
      constraints()[i] =  solution.constraints()[i];
    }

    attributes = new HashMap<>(solution.attributes);
  }

  private static BinarySet createNewBinarySet(int numberOfBits, JMetalRandom randomGenerator) {
    BinarySet bitSet = new BinarySet(numberOfBits);

    for (int i = 0; i < numberOfBits; i++) {
      double rnd = randomGenerator.nextDouble();
      if (rnd < 0.5) {
        bitSet.set(i);
      } else {
        bitSet.clear(i);
      }
    }
    return bitSet;
  }

  @Override
  public int getNumberOfBits(int index) {
    return getVariable(index).getBinarySetLength();
  }

  @Override
  public DefaultBinarySolution copy() {
    return new DefaultBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0;
    for (int i = 0; i < variables().size(); i++) {
      sum += getVariable(i).getBinarySetLength();
    }

    return sum;
  }

  private void initializeBinaryVariables(JMetalRandom randomGenerator) {
    for (int i = 0; i < variables().size(); i++) {
      setVariable(i, createNewBinarySet(bitsPerVariable.get(i), randomGenerator));
    }
  }
}
