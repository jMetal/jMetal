/**
 * DifferentialEvolutionOffspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying DE
 */

package jmetal.util.offspring;

import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DifferentialEvolutionOffspring extends Offspring {
  private double CR_ ;
  private double F_  ;

  private Operator crossover_ ;
  private Operator selection_ ;
  Operator mutation_;

  private DifferentialEvolutionOffspring() {

  }
  /**
   * Constructor
   * @param CR
   * @param F
   */
  public DifferentialEvolutionOffspring(double CR, double F)  {
    HashMap parameters = null ;
    CR_ = CR ;
    F_  = F  ;
    try {
      // Crossover operator
      parameters = new HashMap() ;
      parameters.put("CR", CR_) ;
      parameters.put("F", F_) ;      
      crossover_ = new DifferentialEvolutionCrossover(parameters) ;

      // Selecion operator
      parameters = null ;
      selection_ = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection", parameters);
    } catch (JMException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    id_ = "DE" ;
  }

  public Solution getOffspring(SolutionSet solutionSet, int index) {
    Solution[] parents = new Solution[3] ;
    Solution offSpring = null ;

    try {
      int r1, r2 ;
      do {
        r1 = PseudoRandom.randInt(0,solutionSet.size()-1);
      } while( r1==index );
      do {
        r2 = PseudoRandom.randInt(0,solutionSet.size()-1);
      } while( r2==index || r2==r1);

      parents[0] = solutionSet.get(r1) ;
      parents[1] = solutionSet.get(r2) ;
      parents[2] = solutionSet.get(index) ;

      offSpring = (Solution) crossover_.execute(new Object[]{solutionSet.get(index), parents});
    } catch (JMException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }

    //Create a new solution, using DE
    return offSpring ;
  } // getOffpring

  /**
   * 
   */
  public Solution getOffspring(Solution[] parentSolutions, Solution currentSolution) {
    Solution[] parents = new Solution[3] ;
    Solution offspring = null ;

    try {
      parents[0] = parentSolutions[0] ;
      parents[1] = parentSolutions[1] ;
      parents[2] = currentSolution ;

      offspring = (Solution) crossover_.execute(new Object[]{currentSolution, parents});
    } catch (JMException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }

    //Create a new solution, using DE
    return offspring ;
  } // getOffpring
  
  public String configuration() {
    String result = "-----\n" ;
    result += "Operator: " + id_ + "\n" ;
    result += "CR: " + CR_ + "\n" ;
    result += "F: " + F_ ;

    return result ;
  }
} // DifferentialEvolutionOffspring

