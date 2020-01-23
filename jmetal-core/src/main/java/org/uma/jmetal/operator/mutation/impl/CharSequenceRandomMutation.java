package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a swap mutation. The solution type of the solution must be Permutation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class CharSequenceRandomMutation implements MutationOperator<CharSequenceSolution> {
  private double mutationProbability;

  /** Constructor */
  public CharSequenceRandomMutation(double mutationProbability) {
    Check.probabilityIsValid(mutationProbability);
    this.mutationProbability = mutationProbability;
  }

  /* Getters */
  public double getMutationProbability() {
    return mutationProbability;
  }

  /* Setters */
  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /* Execute() method */
  @Override
  public CharSequenceSolution execute(CharSequenceSolution solution) {
    Check.isNotNull(solution);

    doMutation(solution);
    return solution;
  }

  /** Performs the operation */
  public void doMutation(CharSequenceSolution solution) {
    int sequenceLength = solution.getNumberOfVariables();

    for (int i = 0 ; i < sequenceLength; i++) {
      if (JMetalRandom.getInstance().nextDouble() < mutationProbability) {
        int positionToChange = JMetalRandom.getInstance().nextInt(0, sequenceLength-1);
        char newCharValue =
                CharSequenceSolution.ALPHABET[JMetalRandom.getInstance().nextInt(0, CharSequenceSolution.ALPHABET.length-1)];
        solution.setVariable(positionToChange, newCharValue);
      }
    }
  }
}
