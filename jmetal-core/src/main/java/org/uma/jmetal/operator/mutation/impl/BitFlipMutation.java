package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 *
 *  This class implements a bit flip mutation operator by extending the {@link GenericBitFlipMutation}
 *  class with a {@link BinarySolution} in the generic.
 */
@SuppressWarnings("serial")
public class BitFlipMutation extends GenericBitFlipMutation<BinarySolution> {
  public BitFlipMutation(double mutationProbability) {
    super(mutationProbability);
  }

  public BitFlipMutation(double mutationProbability, RandomGenerator randomGenerator) {
    super(mutationProbability, randomGenerator);
  }
}

