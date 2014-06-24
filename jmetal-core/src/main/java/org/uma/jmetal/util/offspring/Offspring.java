/*
 * Offspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This is the base class to define offspring classes, which create one offspring
 * solution from a solution set
 */


package org.uma.jmetal.util.offspring;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.Selection;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Offspring {
  String id_;

  /**
   * Constructor
   */
  public Offspring() {
    id_ = null;
  }

  public String id() {
    return id_;
  }


  /**
   * Return on offspring from a solutiontype set, indicating the selection operator
   *
   */
  public Solution getOffspring(SolutionSet solutionSet, int numberOfParents, int index,
    Selection selectionOperator) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }


  /**
   * Return on offspring from a solutiontype set
   *
   * @param solutionSet
   * @return the offspring
   */
  public Solution getOffspring(SolutionSet solutionSet) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  /**
   * Return on offspring from a solutiontype set and a given solutiontype
   *
   * @param solutionSet
   * @param solution
   * @return the offspring
   */
  public Solution getOffspring(SolutionSet solutionSet, Solution solution) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  /**
   * Return on offspring from a solutiontype set and a given solutiontype
   *
   * @param solutionSet
   * @param solution
   * @return the offspring
   */
  public Solution getOffspring(Solution[] solutionSet, Solution solution) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }


  /**
   * Return on offspring from two solutiontype sets
   *
   */
  public Solution getOffspring(SolutionSet solutionSet1, SolutionSet archive2) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  public Solution getOffspring(Solution[] solutions) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  /**
   * Return on offspring from a solutiontype set and the index of the current individual
   *
   * @return the offspring
   */
  public Solution getOffspring(SolutionSet solutionSet1, int index) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  public Solution getOffspring(SolutionSet solutionSet1, SolutionSet solutionSet2, int index) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  public Solution getOffspring(SolutionSet solutionSet, Solution solution, int index) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  public Solution getOffspring(SolutionSet solutionSet, SolutionSet archive, Solution solution,
    int index) {
    Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, "method not implemented");
    return null;
  }

  public String configuration() {
    return null;
  }
} 


