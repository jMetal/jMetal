//  AdaptiveGrid.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.util;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;

/**
 * This class defines an adaptive grid over a SolutionSet as the one used the
 * algorithm PAES.
 */
public class AdaptiveGrid {
   
  /**
   * Number of bi-divisions of the objective space
   */
  private int bisections_    ;
  
  /**
   * Objectives of the problem
   */
  private int objectives_    ;
  
  /**
   * Number of solutions into a specific hypercube in the adaptative grid
   */
  private int [] hypercubes_ ;
  
  /**
   * 
   * Grid lower bounds
   */
  private double [] lowerLimits_  ;
  
  /**
   * Grid upper bounds
   */
  private double [] upperLimits_  ; 
  
  /**
   * Size of hypercube for each dimension
   */
  private double [] divisionSize_ ;
  
  /**
   * Hypercube with maximum number of solutions
   */
  private int mostPopulated_      ; 
  
  /**
   * Hndicates when an hypercube has solutions
   */
  private int [] occupied_   ;
           
  /**
  * Constructor.
  * Creates an instance of AdaptativeGrid.
  * @param bisections Number of bi-divisions of the objective space.
  * @param objetives Number of objectives of the problem.
  */
  public AdaptiveGrid(int bisections, int objetives) {
    bisections_ = bisections;
    objectives_  = objetives ;
    lowerLimits_ = new double[objectives_];
    upperLimits_ = new double[objectives_];
    divisionSize_ = new double[objectives_];    
    hypercubes_ = new int[(int)Math.pow(2.0,bisections_*objectives_)];
    
    for (int i = 0; i < hypercubes_.length;i++)
      hypercubes_[i] = 0;
  } //AdaptativeGrid
   
  
  /** 
  *  Updates the grid limits considering the solutions contained in a 
  *  <code>SolutionSet</code>.
  *  @param solutionSet The <code>SolutionSet</code> considered.
  */
  private void updateLimits(SolutionSet solutionSet){          
    //Init the lower and upper limits 
    for (int obj = 0; obj < objectives_; obj++){
      //Set the lower limits to the max real
      lowerLimits_[obj] = Double.MAX_VALUE;
      //Set the upper limits to the min real
      upperLimits_[obj] = Double.MIN_VALUE;
    } // for
	   
    //Find the max and min limits of objetives into the population
    for (int ind = 0; ind < solutionSet.size(); ind++){
      Solution tmpIndividual = solutionSet.get(ind);
      for (int obj = 0; obj < objectives_; obj++) {
        if (tmpIndividual.getObjective(obj) < lowerLimits_[obj]) {
          lowerLimits_[obj] = tmpIndividual.getObjective(obj);
        }
        if (tmpIndividual.getObjective(obj) > upperLimits_[obj]) {
          upperLimits_[obj] = tmpIndividual.getObjective(obj);
        }
      } // for
    } // for   
 } //updateLimits
   
  /** 
   * Updates the grid adding solutions contained in a specific 
   * <code>SolutionSet</code>.
   * <b>REQUIRE</b> The grid limits must have been previously calculated.
   * @param solutionSet The <code>SolutionSet</code> considered.
   */
  private void addSolutionSet(SolutionSet solutionSet){
    //Calculate the location of all individuals and update the grid
    mostPopulated_ = 0;
    int location;              
    
    for (int ind = 0; ind < solutionSet.size();ind++){
      location = location(solutionSet.get(ind));                      
      hypercubes_[location]++;
      if (hypercubes_[location]>hypercubes_[mostPopulated_])
        mostPopulated_ = location;
    } // for  
       
    //The grid has been updated, so also update ocuppied's hypercubes
    calculateOccupied();
 } // addSolutionSet
   
   
  /** 
   * Updates the grid limits and the grid content adding the solutions contained
   * in a specific <code>SolutionSet</code>.
   * @param solutionSet The <code>SolutionSet</code>.
   */
  public void updateGrid(SolutionSet solutionSet){	   
    //Update lower and upper limits
    updateLimits(solutionSet);

    //Calculate the division size
    for (int obj = 0; obj < objectives_; obj++){
      divisionSize_[obj] = upperLimits_[obj] - lowerLimits_[obj];		   
    } // for
	   
    //Clean the hypercubes
    for (int i = 0; i < hypercubes_.length;i++) {
      hypercubes_[i] = 0;	   
    }
    
    //Add the population
    addSolutionSet(solutionSet);
 } //updateGrid
   
   
  /** 
   * Updates the grid limits and the grid content adding a new 
   * <code>Solution</code>.
   * If the solution falls out of the grid bounds, the limits and content of the
   * grid must be re-calculated.
   * @param solution <code>Solution</code> considered to update the grid.
   * @param solutionSet <code>SolutionSet</code> used to update the grid.
   */
  public void updateGrid(Solution solution, SolutionSet solutionSet){	   
    
    int location = location(solution);
    if (location == -1) {//Re-build the Adaptative-Grid
      //Update lower and upper limits
      updateLimits(solutionSet);
      
      //Actualize the lower and upper limits whit the individual      
      for (int obj = 0; obj < objectives_; obj++){                    
        if (solution.getObjective(obj) < lowerLimits_[obj])
          lowerLimits_[obj] = solution.getObjective(obj);
        if (solution.getObjective(obj) > upperLimits_[obj])
          upperLimits_[obj] = solution.getObjective(obj);                    
      } // for
               
      //Calculate the division size
      for (int obj = 0; obj < objectives_; obj++){        
        divisionSize_[obj] = upperLimits_[obj] - lowerLimits_[obj];
      }
	   
      //Clean the hypercube
      for (int i = 0; i < hypercubes_.length; i++) {
        hypercubes_[i] = 0;
      }
	   
      //add the population
      addSolutionSet(solutionSet);
    } // if                                          
 } //updateGrid

   
  /** 
   * Calculates the hypercube of a solution.
   * @param solution The <code>Solution</code>.
   */
  public int location(Solution solution){                 
    //Create a int [] to store the range of each objetive
    int [] position = new int[objectives_];

    //Calculate the position for each objetive
    for (int obj = 0; obj < objectives_; obj++) {           
      
      if ((solution.getObjective(obj) > upperLimits_[obj])
          || (solution.getObjective(obj) < lowerLimits_[obj]))
        return -1;      
      else if (solution.getObjective(obj) ==lowerLimits_[obj])
        position[obj] = 0;           
      else if (solution.getObjective(obj) ==upperLimits_[obj])
        position[obj] = ((int)Math.pow(2.0,bisections_))-1;    	              
      else {
        double tmpSize = divisionSize_[obj];               
        double value   = solution.getObjective(obj);
        double account = lowerLimits_[obj];
        int ranges     = (int)Math.pow(2.0,bisections_);               
        for (int b = 0; b < bisections_; b++){
          tmpSize /= 2.0;
          ranges /= 2;
          if (value > (account + tmpSize)){                       
            position[obj] += ranges;
            account += tmpSize;
          } // if
        } // for
      } // if
    }
     
    //Calcualate the location into the hypercubes
    int location = 0;
    for (int obj = 0; obj < objectives_; obj++) {
      location += position[obj] * Math.pow(2.0,obj * bisections_); 
    }                                        
    return location;
  } //location
   
  /**
  * Returns the value of the most populated hypercube.
  * @return The hypercube with the maximum number of solutions.
  */
  public int getMostPopulated(){
    return mostPopulated_;
  } // getMostPopulated
   
  /**
  * Returns the number of solutions into a specific hypercube.
  * @param location Number of the hypercube.
  * @return The number of solutions into a specific hypercube.
  */
  public int getLocationDensity(int location){
       return hypercubes_[location];
  } //getLocationDensity
   
  /** 
  * Decreases the number of solutions into a specific hypercube.
  * @param location Number of hypercube.
  */
  public void removeSolution(int location) {       
    //Decrease the solutions in the location specified.
    hypercubes_[location]--;
        
    //Update the most poblated hypercube
    if (location == mostPopulated_)               
      for (int i = 0; i < hypercubes_.length;i++)
        if (hypercubes_[i] > hypercubes_[mostPopulated_])
          mostPopulated_ = i;                    
       
    //If hypercubes[location] now becomes to zero, then update ocupped hypercubes
    if (hypercubes_[location]==0)
      this.calculateOccupied();
  } //removeSolution
   
 /**
  * Increases the number of solutions into a specific hypercube.
  * @param location Number of hypercube.
  */
  public void addSolution(int location) {
    //Increase the solutions in the location specified.
    hypercubes_[location]++;
    
    //Update the most poblated hypercube
    if (hypercubes_[location] > hypercubes_[mostPopulated_])
      mostPopulated_ = location;       
       
    //if hypercubes[location] becomes to one, then recalculate 
    //the occupied hypercubes
    if (hypercubes_[location] == 1)
      this.calculateOccupied();
  } //addSolution

  /**
  * Returns the number of bi-divisions performed in each objective.
  * @return the number of bi-divisions.
  */
  public int getBisections() {
    return bisections_;
  } //getBisections
          
  /** 
   * Retunrns a String representing the grid.
   * @return The String.
   */
  public String toString() {
    String result = "Grid\n";
    for (int obj = 0; obj < objectives_; obj++){
      result += "Objective " + obj + " " + lowerLimits_[obj] + " " 
                                         + upperLimits_[obj]+"\n";
    } // for                
    return result;
  } // toString
    
  /** 
   * Returns a random hypercube using a rouleteWheel method.  
  *  @return the number of the selected hypercube.
  */
  public int rouletteWheel(){
    //Calculate the inverse sum
    double inverseSum = 0.0;
    for (int aHypercubes_ : hypercubes_) {
      if (aHypercubes_ > 0) {
        inverseSum += 1.0 / (double) aHypercubes_;
      }
    }
        
    //Calculate a random value between 0 and sumaInversa
    double random = PseudoRandom.randDouble(0.0,inverseSum);
    int hypercube = 0;
    double accumulatedSum = 0.0;
    while (hypercube < hypercubes_.length){
      if (hypercubes_[hypercube] > 0) {
        accumulatedSum += 1.0 / (double)hypercubes_[hypercube];
      } // if
            
      if (accumulatedSum > random) {
        return hypercube;
      } // if
        
      hypercube++;
    } // while
    
    return hypercube;        
  } //rouletteWheel
    
  /**
  * Calculates the number of hypercubes having one or more solutions.
  * return the number of hypercubes with more than zero solutions.
  */
  public int calculateOccupied(){
    int total = 0;
    for (int aHypercubes_ : hypercubes_) {
      if (aHypercubes_ > 0) {
        total++;
      } // if
    } // for
        
    occupied_ = new int[total];        
    int base = 0;
    for (int i = 0; i < hypercubes_.length;i++){
      if (hypercubes_[i] > 0){             
        occupied_[base] = i;
        base++;
      } // if
    } // for        
        
    return total;
  } //calculateOcuppied
    
  /** 
   * Returns the number of hypercubes with more than zero solutions.
   * @return the number of hypercubes with more than zero solutions.
   */
  public int occupiedHypercubes(){
    return occupied_.length;
  } // occupiedHypercubes
    
  
  /**
   * Returns a random hypercube that has more than zero solutions.
   * @return The hypercube.
   */
  public int randomOccupiedHypercube(){
    int rand = PseudoRandom.randInt(0,occupied_.length-1);
    return occupied_[rand];
  } //randomOccupiedHypercube
} //AdaptativeGrid

