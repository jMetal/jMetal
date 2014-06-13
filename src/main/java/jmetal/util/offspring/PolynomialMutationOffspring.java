/*
 * PolynomialOffspringGenerator.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package jmetal.util.offspring;

import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.operators.mutation.MutationFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.logging.Level;

public class PolynomialMutationOffspring extends Offspring {

  private Operator mutation_;

  private double mutationProbability_;
  private double distributionIndex_;

  public PolynomialMutationOffspring(double mutationProbability,
    double distributionIndexForMutation
  ) throws JMException {
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
    } catch (JMException e) {
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


