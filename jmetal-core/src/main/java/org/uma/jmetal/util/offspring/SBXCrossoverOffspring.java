/*
 * SBXCrossover.java
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
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SBXCrossoverOffspring extends Offspring {

  private double crossoverProbability = 0.9;
  private double distributionIndexForCrossover = 20;
  private Operator crossover;
  private Operator selection;

  public SBXCrossoverOffspring(double crossoverProbability,
    double distributionIndexForCrossover) throws JMetalException {
    this.crossoverProbability = crossoverProbability;
    this.distributionIndexForCrossover = distributionIndexForCrossover;

    // Crossover operator
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("setProbability", this.crossoverProbability);
    crossoverParameters.put("setDistributionIndex", this.distributionIndexForCrossover);

    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", crossoverParameters);
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", null);

    id = "SBXCrossover";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection.execute(solutionSet);
      parents[1] = (Solution) selection.execute(solutionSet);

      Solution[] children = (Solution[]) crossover.execute(parents);
      offSpring = children[0];
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(SBXCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;
  }

  /**
   *
   */
  public Solution getOffspring(Solution[] parentSolutions) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = parentSolutions[0];
      parents[1] = parentSolutions[1];

      Solution[] children = (Solution[]) crossover.execute(parents);
      offSpring = children[0];
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(SBXCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
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
      //Create a new solutiontype, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(SBXCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;
  }

  public String configuration() {
    String result = "-----\n";
    result += "Operator: " + id + "\n";
    result += "Probability: " + crossoverProbability + "\n";
    result += "DistributionIndex: " + distributionIndexForCrossover;

    return result;
  }
}

