/*
 * PolynomialOffspringGenerator.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package org.uma.jmetal.util.offspring;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.util.HashMap;
import java.util.logging.Level;

public class PolynomialMutationOffspring extends Offspring {

  private Operator mutation;

  private double mutationProbability;
  private double distributionIndex;

  public PolynomialMutationOffspring(double mutationProbability,
    double distributionIndexForMutation
  ) throws JMetalException {
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("setProbability", this.mutationProbability = mutationProbability);
    mutationParameters.put("setDistributionIndex", distributionIndex = distributionIndexForMutation);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);

    id = "PolynomialMutation";
  }

  public Solution getOffspring(Solution solution) {
    Solution res = new Solution(solution);
    try {
      mutation.execute(res);
    } catch (JMetalException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
    return res;
  }

  public String configuration() {
    String result = "-----\n";
    result += "Operator: " + id + "\n";
    result += "Probability: " + mutationProbability + "\n";
    result += "DistributionIndex: " + distributionIndex;

    return result;
  }
}


