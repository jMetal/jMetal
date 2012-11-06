/*
 * SBXCrossoverAndPolynomialMutation.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package jmetal.util.offspring;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.*;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;

public class SBXOffspring extends Offspring {

  double crossoverProbability_ = 0.9;
  double distributionIndexForCrossover_ = 20;
  Operator crossover_;
  Operator mutation_;
  Operator selection_;

  public SBXOffspring(double crossoverProbability,
                     double distributionIndexForCrossover) throws JMException {
  	HashMap parameters ;
    crossoverProbability_ = crossoverProbability;
    distributionIndexForCrossover_ = distributionIndexForCrossover;

    // Crossover operator
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", distributionIndexForCrossover_) ;

    crossover_ = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);
    
    id_ = "SBX";
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
    } catch (JMException ex) {
      Logger.getLogger(SBXOffspring.class.getName()).log(Level.SEVERE, null, ex);
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
    } catch (JMException ex) {
      Logger.getLogger(SBXOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring
} // SBXOffspring

