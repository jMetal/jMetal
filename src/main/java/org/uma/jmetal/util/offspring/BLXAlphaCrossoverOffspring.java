/*
 * BLXAlphaCrossoverOffspring.java
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

public class BLXAlphaCrossoverOffspring extends Offspring {

  Operator mutation_;
  private double crossoverProbability_ = 0.9;
  private double alpha_ = 0.5;
  private Operator crossover_;
  private Operator selection_;

  private BLXAlphaCrossoverOffspring(
    double crossoverProbability,
    double alpha) throws JMetalException {
    crossoverProbability_ = crossoverProbability;
    alpha_ = alpha;

    // Crossover operator
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", crossoverProbability_);
    crossoverParameters.put("alpha", alpha_);

    crossover_ = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", crossoverParameters);
    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);

    id_ = "BLXAlphaCrossover";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection_.execute(solutionSet);
      parents[1] = (Solution) selection_.execute(solutionSet);

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      //Create a new solution, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(BLXAlphaCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring

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
      //Create a new solution, using DE
    } catch (JMetalException ex) {
      Logger.getLogger(BLXAlphaCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring

  public String configuration() {
    String result = "-----\n";
    result += "Operator: " + id_ + "\n";
    result += "Probability: " + crossoverProbability_ + "\n";
    result += "Alpha: " + alpha_;

    return result;
  }
} // BLXAlphaCrossoverOffspring

