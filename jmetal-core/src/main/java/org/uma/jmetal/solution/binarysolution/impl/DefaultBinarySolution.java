package org.uma.jmetal.solution.binarysolution.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

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
  public DefaultBinarySolution(@NotNull List<Integer> bitsPerVariable, int numberOfObjectives) {
    this(bitsPerVariable, numberOfObjectives, 0);
  }

  /**
   * Constructor
   */
  public DefaultBinarySolution(@NotNull List<Integer> bitsPerVariable, int numberOfObjectives, int numberOfConstraints) {
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
      variables().set(i, (BinarySet) solution.variables().get(i).clone());
    }

    Arrays.setAll(objectives(), i -> solution.objectives()[i]);
    Arrays.setAll(constraints(), i -> solution.constraints()[i]);

    attributes = new HashMap<>(solution.attributes);
  }

  private static @NotNull BinarySet createNewBinarySet(int numberOfBits, JMetalRandom randomGenerator) {
    @NotNull BinarySet bitSet = new BinarySet(numberOfBits);

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
    return variables().get(index).getBinarySetLength();
  }

  @Override
  public @NotNull DefaultBinarySolution copy() {
    return new DefaultBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0;
    for (@NotNull BinarySet binarySet : variables()) {
      int binarySetLength = binarySet.getBinarySetLength();
      sum += binarySetLength;
    }

    return sum;
  }

  private void initializeBinaryVariables(JMetalRandom randomGenerator) {
    for (int i = 0; i < variables().size(); i++) {
      variables().set(i, createNewBinarySet(bitsPerVariable.get(i), randomGenerator));
    }
  }
}
