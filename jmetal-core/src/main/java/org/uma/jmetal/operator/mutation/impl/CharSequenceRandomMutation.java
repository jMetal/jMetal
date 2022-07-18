package org.uma.jmetal.operator.mutation.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;
import org.uma.jmetal.util.errorchecking.Check;
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
  private final char[] alphabet;

  /** Constructor */
  public CharSequenceRandomMutation(double mutationProbability, char[] alphabet) {
    Check.probabilityIsValid(mutationProbability);
    this.mutationProbability = mutationProbability;
    this.alphabet = alphabet;
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
    Check.notNull(solution);

    doMutation(solution);
    return solution;
  }

  /** Performs the operation */
  public void doMutation(@NotNull CharSequenceSolution solution) {
    var sequenceLength = solution.variables().size();

    for (var i = 0; i < sequenceLength; i++) {
      if (JMetalRandom.getInstance().nextDouble() < mutationProbability) {
        var positionToChange = JMetalRandom.getInstance().nextInt(0, sequenceLength - 1);
        var newCharValue = alphabet[JMetalRandom.getInstance().nextInt(0, alphabet.length - 1)];
        solution.variables().set(positionToChange, newCharValue);
      }
    }
  }
}
