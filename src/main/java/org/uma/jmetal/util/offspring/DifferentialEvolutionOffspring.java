/**
 * DifferentialEvolutionOffspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying DE
 */

package org.uma.jmetal.util.offspring;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DifferentialEvolutionOffspring extends Offspring {
  private double cr_ ;
  private double f_  ;

  private Operator crossover_ ;
  private Operator selection_ ;
  private Operator mutation_;

  /**
   * Constructor
   *
   * @param CR
   * @param F
   */
  public DifferentialEvolutionOffspring(double CR, double F)  {
    cr_ = CR ;
    f_  = F  ;
    try {
      // Crossover operator
      HashMap<String, Object> crossoverParameters = new HashMap<String, Object>() ;
      crossoverParameters.put("CR", cr_) ;
      crossoverParameters.put("F", f_) ;      
      crossover_ = new DifferentialEvolutionCrossover(crossoverParameters) ;

      // Selecion operator
      HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
      selection_ = SelectionFactory
        .getSelectionOperator("DifferentialEvolutionSelection", selectionParameters);
    } catch (JMetalException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    id_ = "DE";
  }

  public Solution getOffspring(SolutionSet solutionSet, int index) {
    Solution[] parents = new Solution[3];
    Solution offSpring = null;

    try {
      int r1, r2;
      do {
        r1 = PseudoRandom.randInt(0, solutionSet.size() - 1);
      } while (r1 == index);
      do {
        r2 = PseudoRandom.randInt(0, solutionSet.size() - 1);
      } while (r2 == index || r2 == r1);

      parents[0] = solutionSet.get(r1);
      parents[1] = solutionSet.get(r2);
      parents[2] = solutionSet.get(index);

      offSpring = (Solution) crossover_.execute(new Object[] {solutionSet.get(index), parents});
    } catch (JMetalException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }

    //Create a new solution, using DE
    return offSpring ;
  }

  /**
   *
   */
  public Solution getOffspring(Solution[] parentSolutions, Solution currentSolution) {
    Solution[] parents = new Solution[3];
    Solution offspring = null;

    try {
      parents[0] = parentSolutions[0];
      parents[1] = parentSolutions[1];
      parents[2] = currentSolution;

      offspring = (Solution) crossover_.execute(new Object[] {currentSolution, parents});
    } catch (JMetalException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }

    //Create a new solution, using DE
    return offspring;
  } // getOffpring

  public String configuration() {
    String result = "-----\n" ;
    result += "Operator: " + id_ + "\n" ;
    result += "CR: " + cr_ + "\n" ;
    result += "F: " + f_ ;

    return result;
  }
} 
