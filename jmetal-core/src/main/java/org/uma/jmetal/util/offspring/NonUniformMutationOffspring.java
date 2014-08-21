/*
 * NonUniformGenerator.java
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
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.util.HashMap;
import java.util.logging.Level;

public class NonUniformMutationOffspring extends Offspring {
  public Operator mutation;
  private Operator selection;

  private double mutationProbability;
  private double perturbation;
  private int maxIterations;

  public NonUniformMutationOffspring(double mutationProbability,
    double perturbation,
    int maxIterations
  ) throws JMetalException {
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", this.mutationProbability = mutationProbability);
    mutationParameters.put("perturbation", this.perturbation = perturbation);
    mutationParameters.put("maxIterations", this.maxIterations = maxIterations);
    mutation = MutationFactory.getMutationOperator("NonUniformMutation", mutationParameters);
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", null);
    id = "NonUniformMutation";
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
    result += "MaxIterations: " + maxIterations + "\n";
    result += "Perturbation: " + perturbation;

    return result;
  }
} // PolynomialOffspringGenerator


