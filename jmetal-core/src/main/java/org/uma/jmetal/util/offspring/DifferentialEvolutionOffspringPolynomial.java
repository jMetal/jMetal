/*
 * DifferentialEvolutionOffspringPolynomial.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package org.uma.jmetal.util.offspring;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.logging.Level;

public class DifferentialEvolutionOffspringPolynomial extends Offspring {

  double crossoverProbability = 0.9;
  double distributionIndexForCrossover = 20;
  private double mutationProbability = 0.0;
  private double distributionIndexForMutation = 20;
  private Operator mutation;
  private Operator selection;

  private DifferentialEvolutionOffspringPolynomial(double mutationProbability,
    double distributionIndexForMutation
  ) throws JMetalException {
    this.mutationProbability = mutationProbability;
    this.distributionIndexForMutation = distributionIndexForMutation;

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", this.mutationProbability);
    mutationParameters.put("distributionIndex", this.distributionIndexForMutation);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);

    selection = SelectionFactory.getSelectionOperator("BinaryTournament", null);

    id = "Polynomial";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      offSpring = new Solution((Solution) selection.execute(solutionSet));


      mutation.execute(offSpring);
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      java.util.logging.Logger.getLogger(DifferentialEvolutionOffspringPolynomial.class.getName())
        .log(Level.SEVERE, null, ex);
    }
    return offSpring;
  }

  public Solution getOffspring(SolutionSet solutionSet, SolutionSet archive) {
    Solution[] parents = null;
    Solution offSpring = null;

    try {
      // FIXME: this will return an exception
      parents[0] = (Solution) selection.execute(solutionSet);

      if (archive.size() > 0) {
        parents[1] = (Solution) selection.execute(archive);
      } else {
        parents[1] = (Solution) selection.execute(solutionSet);
      }

      offSpring = new Solution(new Solution((Solution) selection.execute(solutionSet)));


      mutation.execute(offSpring);
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      java.util.logging.Logger.getLogger(DifferentialEvolutionOffspringPolynomial.class.getName())
        .log(Level.SEVERE, null, ex);
    }
    return offSpring;
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
} 

