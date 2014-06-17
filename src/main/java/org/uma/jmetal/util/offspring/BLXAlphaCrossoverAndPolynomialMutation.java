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

  private double mutationProbability_ = 0.0;
  private double crossoverProbability_ = 0.9;
  private double distributionIndexForMutation_ = 20;
  private double alpha_ = 0.5;
  private Operator crossover_;
  private Operator mutation_;
  private Operator selection_;

  private BLXAlphaCrossoverAndPolynomialMutation(double mutationProbability,
    double crossoverProbability,
    double distributionIndexForMutation,
    double alpha) throws JMetalException {
    mutationProbability_ = mutationProbability;
    crossoverProbability_ = crossoverProbability;
    distributionIndexForMutation_ = distributionIndexForMutation;
    alpha_ = alpha;

    // Crossover operator
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", crossoverProbability_);
    crossoverParameters.put("alpha", alpha_);

    crossover_ = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", crossoverParameters);

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", mutationProbability_);
    mutationParameters.put("distributionIndex", distributionIndexForMutation_);

    mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);
    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);

    id_ = "BLXAlpha_Polynomial";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection_.execute(solutionSet);
      parents[1] = (Solution) selection_.execute(solutionSet);

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      mutation_.execute(offSpring);
      //Create a new solution, using DE
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
      parents[0] = (Solution) selection_.execute(solutionSet);

      if (archive.size() > 0) {
        parents[1] = (Solution) selection_.execute(archive);
      } else {
        parents[1] = (Solution) selection_.execute(solutionSet);
      }

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(BLXAlphaCrossoverAndPolynomialMutation.class.getName())
        .log(Level.SEVERE, null, ex);
    }
    return offSpring;
  }
}

