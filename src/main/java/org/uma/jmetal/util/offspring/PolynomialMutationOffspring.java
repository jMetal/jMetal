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
import org.uma.jmetal.operators.mutation.MutationFactory;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.logging.Level;

public class PolynomialMutationOffspring extends Offspring {

  private Operator mutation_;

  private double mutationProbability_;
  private double distributionIndex_;

  public PolynomialMutationOffspring(double mutationProbability,
    double distributionIndexForMutation
  ) throws JMetalException {
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", mutationProbability_ = mutationProbability);
    mutationParameters.put("distributionIndex", distributionIndex_ = distributionIndexForMutation);
    mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);

    id_ = "PolynomialMutation";
  }

  public Solution getOffspring(Solution solution) {
    Solution res = new Solution(solution);
    try {
      mutation_.execute(res);
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    }
    return res;
  }

  public String configuration() {
    String result = "-----\n";
    result += "Operator: " + id_ + "\n";
    result += "Probability: " + mutationProbability_ + "\n";
    result += "DistributionIndex: " + distributionIndex_;

    return result;
  }
}


