/*
 * SBXCrossoverAndPolynomialMutation.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package jmetal.util.offspring;

import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SBXCrossoverAndPolynomialMutationOffspring extends Offspring {

  private double mutationProbability_ = 0.0;
  private double crossoverProbability_ = 0.9;
  private double distributionIndexForMutation_ = 20;
  private double distributionIndexForCrossover_ = 20;
  private Operator crossover_;
  private Operator mutation_;
  private Operator selection_;

  private SBXCrossoverAndPolynomialMutationOffspring(double mutationProbability,
                                                     double crossoverProbability,
                                                     double distributionIndexForMutation,
                                                     double distributionIndexForCrossover) throws JMException {
  	HashMap parameters ;
    mutationProbability_ = mutationProbability;
    crossoverProbability_ = crossoverProbability;
    distributionIndexForMutation_ = distributionIndexForMutation;
    distributionIndexForCrossover_ = distributionIndexForCrossover;

    // Crossover operator
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", distributionIndexForCrossover_) ;

    crossover_ = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", distributionIndexForMutation_) ;

    mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);
    
    id_ = "SBX_Polynomial";
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
      
    } catch (JMException ex) {
      Logger.getLogger(SBXCrossoverAndPolynomialMutationOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring

    public Solution getOffspring(SolutionSet solutionSet, SolutionSet archive) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection_.execute(solutionSet);

      if (archive.size() > 0) {
          parents[1] = (Solution)selection_.execute(archive);
      } else {
          parents[1] = (Solution)selection_.execute(solutionSet);
      }

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(SBXCrossoverAndPolynomialMutationOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring
} // SBXCrossoverAndPolynomialMutation

