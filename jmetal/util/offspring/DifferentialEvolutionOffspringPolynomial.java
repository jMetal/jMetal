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
import jmetal.core.* ;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;

public class DifferentialEvolutionOffspringPolynomial extends Offspring {

  double mutationProbability_ = 0.0;
  double crossoverProbability_ = 0.9;
  double distributionIndexForMutation_ = 20;
  double distributionIndexForCrossover_ = 20;
  Operator mutation_;
  Operator selection_;

  public DifferentialEvolutionOffspringPolynomial(double mutationProbability,    
    double distributionIndexForMutation
    ) throws JMException {
  	HashMap parameters ;
    mutationProbability_ = mutationProbability;    
    distributionIndexForMutation_ = distributionIndexForMutation;
    
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", distributionIndexForMutation_) ;
    mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);


    id_ = "Polynomial";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      offSpring = new Solution((Solution) selection_.execute(solutionSet));


      mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(DifferentialEvolutionOffspringPolynomial.class.getName()).log(Level.SEVERE, null, ex);
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

      offSpring = new Solution( new Solution((Solution) selection_.execute(solutionSet)));


      mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(DifferentialEvolutionOffspringPolynomial.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring
    
    public Solution getOffspring(Solution solution) {
    	Solution res = new Solution(solution);
    	try {
			mutation_.execute(res);
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
    	
    }
    
    
} // DifferentialEvolutionOffspring

