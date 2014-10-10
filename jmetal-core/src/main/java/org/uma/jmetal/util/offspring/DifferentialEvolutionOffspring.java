/**
 * DifferentialEvolutionOffspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solutiontype after applying DE
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
  private double cr;
  private double f;

  private Operator crossover;
  private Operator selection;

  /** Constructor */
  public DifferentialEvolutionOffspring(double CR, double F)  {
    cr = CR ;
    f = F  ;
    try {
      // Crossover operator
      HashMap<String, Object> crossoverParameters = new HashMap<String, Object>() ;
      crossoverParameters.put("CR", cr) ;
      crossoverParameters.put("F", f) ;
      crossover = new DifferentialEvolutionCrossover(crossoverParameters) ;

      // Selecion operator
      HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
      selection = SelectionFactory
        .getSelectionOperator("DifferentialEvolutionSelection", selectionParameters);
    } catch (JMetalException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    id = "DE";
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

      offSpring = (Solution) crossover.execute(new Object[] {solutionSet.get(index), parents});
    } catch (JMetalException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }

    //Create a new solutiontype, using DE
    return offSpring ;
  }

  public Solution getOffspring(Solution[] parentSolutions, Solution currentSolution) {
    Solution[] parents = new Solution[3];
    Solution offspring = null;

    try {
      parents[0] = parentSolutions[0];
      parents[1] = parentSolutions[1];
      parents[2] = currentSolution;

      offspring = (Solution) crossover.execute(new Object[] {currentSolution, parents});
    } catch (JMetalException ex) {
      Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }

    //Create a new solutiontype, using DE
    return offspring;
  }

  public String configuration() {
    String result = "-----\n" ;
    result += "Operator: " + id + "\n" ;
    result += "CR: " + cr + "\n" ;
    result += "F: " + f;

    return result;
  }
} 
