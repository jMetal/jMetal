/*
 * SBXCrossoverAndPolynomialMutation.java
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
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BLXAlphaCrossoverAndPolynomialMutation extends Offspring {

  private double mutationProbability = 0.0;
  private double crossoverProbability = 0.9;
  private double distributionIndexForMutation = 20;
  private double alpha = 0.5;
  private Operator crossover;
  private Operator mutation;
  private Operator selection;

  private BLXAlphaCrossoverAndPolynomialMutation(double mutationProbability,
    double crossoverProbability,
    double distributionIndexForMutation,
    double alpha) throws JMetalException {
    this.mutationProbability = mutationProbability;
    this.crossoverProbability = crossoverProbability;
    this.distributionIndexForMutation = distributionIndexForMutation;
    this.alpha = alpha;

    // Crossover operator
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("setProbability", this.crossoverProbability);
    crossoverParameters.put("alpha", this.alpha);

    crossover = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", crossoverParameters);

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("setProbability", this.mutationProbability);
    mutationParameters.put("setDistributionIndex", this.distributionIndexForMutation);

    mutation = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", null);

    id = "BLXAlpha_Polynomial";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection.execute(solutionSet);
      parents[1] = (Solution) selection.execute(solutionSet);

      Solution[] children = (Solution[]) crossover.execute(parents);
      offSpring = children[0];
      mutation.execute(offSpring);
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(BLXAlphaCrossoverAndPolynomialMutation.class.getName())
        .log(Level.SEVERE, null, ex);
    }
    return offSpring;
  }

  public Solution getOffspring(SolutionSet solutionSet, SolutionSet archive) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection.execute(solutionSet);

      if (archive.size() > 0) {
        parents[1] = (Solution) selection.execute(archive);
      } else {
        parents[1] = (Solution) selection.execute(solutionSet);
      }

      Solution[] children = (Solution[]) crossover.execute(parents);
      offSpring = children[0];
      mutation.execute(offSpring);
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(BLXAlphaCrossoverAndPolynomialMutation.class.getName())
        .log(Level.SEVERE, null, ex);
    }
    return offSpring;
  }
}

