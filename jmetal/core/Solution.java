//  Solution.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Description: 
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

package jmetal.core;

import jmetal.encodings.variable.Binary;

import java.io.Serializable;

/**
 * Class representing a solution for a problem.
 */
public class Solution implements Serializable {  
	/**
	 * Stores the problem 
	 */
  private Problem problem_ ;
	
  /**
   * Stores the type of the encodings.variable
   */	
  private SolutionType type_ ; 

  /**
   * Stores the decision variables of the solution.
   */
  private Variable[] variable_ ;

  /**
   * Stores the objectives values of the solution.
   */
  private final double [] objective_ ;

  /**
   * Stores the number of objective values of the solution
   */
  private int numberOfObjectives_ ;

  /**
   * Stores the so called fitness value. Used in some metaheuristics
   */
  private double fitness_ ;

  /**
   * Used in algorithm AbYSS, this field is intended to be used to know
   * when a <code>Solution</code> is marked.
   */
  private boolean marked_ ;

  /**
   * Stores the so called rank of the solution. Used in NSGA-II
   */
  private int rank_ ;

  /**
   * Stores the overall constraint violation of the solution.
   */
  private double  overallConstraintViolation_ ;

  /**
   * Stores the number of constraints violated by the solution.
   */
  private int  numberOfViolatedConstraints_ ;

  /**
   * This field is intended to be used to know the location of
   * a solution into a <code>SolutionSet</code>. Used in MOCell
   */
  private int location_ ;

  /**
   * Stores the distance to his k-nearest neighbor into a 
   * <code>SolutionSet</code>. Used in SPEA2.
   */
  private double kDistance_ ; 

  /**
   * Stores the crowding distance of the the solution in a 
   * <code>SolutionSet</code>. Used in NSGA-II.
   */
  private double crowdingDistance_ ; 

  /**
   * Stores the distance between this solution and a <code>SolutionSet</code>.
   * Used in AbySS.
   */
  private double distanceToSolutionSet_ ;       

  /**
   * Constructor.
   */
  public Solution() {        
  	problem_                      = null  ;
    marked_                       = false ;
    overallConstraintViolation_   = 0.0   ;
    numberOfViolatedConstraints_  = 0     ;  
    type_                         = null ;
    variable_                     = null ;
    objective_                    = null ;
  } // Solution

  /**
   * Constructor
   * @param numberOfObjectives Number of objectives of the solution
   * 
   * This constructor is used mainly to read objective values from a file to
   * variables of a SolutionSet to apply quality indicators
   */
  public Solution(int numberOfObjectives) {
    numberOfObjectives_ = numberOfObjectives;
    objective_          = new double[numberOfObjectives];
  }
  
  /** 
   * Constructor.
   * @param problem The problem to solve
   * @throws ClassNotFoundException 
   */
  public Solution(Problem problem) throws ClassNotFoundException{
    problem_ = problem ; 
    type_ = problem.getSolutionType() ;
    numberOfObjectives_ = problem.getNumberOfObjectives() ;
    objective_          = new double[numberOfObjectives_] ;

    // Setting initial values
    fitness_              = 0.0 ;
    kDistance_            = 0.0 ;
    crowdingDistance_     = 0.0 ;        
    distanceToSolutionSet_ = Double.POSITIVE_INFINITY ;
    //<-

    //variable_ = problem.solutionType_.createVariables() ; 
    variable_ = type_.createVariables() ; 
  } // Solution
  
  static public Solution getNewSolution(Problem problem) throws ClassNotFoundException {
    return new Solution(problem) ;
  }
  
  /** 
   * Constructor
   * @param problem The problem to solve
   */
  public Solution(Problem problem, Variable [] variables){
    problem_ = problem ;
  	type_ = problem.getSolutionType() ;
    numberOfObjectives_ = problem.getNumberOfObjectives() ;
    objective_          = new double[numberOfObjectives_] ;

    // Setting initial values
    fitness_              = 0.0 ;
    kDistance_            = 0.0 ;
    crowdingDistance_     = 0.0 ;        
    distanceToSolutionSet_ = Double.POSITIVE_INFINITY ;
    //<-

    variable_ = variables ;
  } // Constructor
  
  /** 
   * Copy constructor.
   * @param solution Solution to copy.
   */    
  public Solution(Solution solution) {            
    problem_ = solution.problem_ ;
    type_ = solution.type_;

    numberOfObjectives_ = solution.getNumberOfObjectives();
    objective_ = new double[numberOfObjectives_];
    for (int i = 0; i < objective_.length;i++) {
      objective_[i] = solution.getObjective(i);
    } // for
    //<-

    variable_ = type_.copyVariables(solution.variable_) ;
    overallConstraintViolation_  = solution.getOverallConstraintViolation();
    numberOfViolatedConstraints_ = solution.getNumberOfViolatedConstraint();
    distanceToSolutionSet_ = solution.getDistanceToSolutionSet();
    crowdingDistance_     = solution.getCrowdingDistance();
    kDistance_            = solution.getKDistance();                
    fitness_              = solution.getFitness();
    marked_               = solution.isMarked();
    rank_                 = solution.getRank();
    location_             = solution.getLocation();
  } // Solution

  /**
   * Sets the distance between this solution and a <code>SolutionSet</code>.
   * The value is stored in <code>distanceToSolutionSet_</code>.
   * @param distance The distance to a solutionSet.
   */
  public void setDistanceToSolutionSet(double distance){
    distanceToSolutionSet_ = distance;
  } // SetDistanceToSolutionSet

  /**
   * Gets the distance from the solution to a <code>SolutionSet</code>. 
   * <b> REQUIRE </b>: this method has to be invoked after calling 
   * <code>setDistanceToPopulation</code>.
   * @return the distance to a specific solutionSet.
   */
  public double getDistanceToSolutionSet(){
    return distanceToSolutionSet_;
  } // getDistanceToSolutionSet


  /** 
   * Sets the distance between the solution and its k-nearest neighbor in 
   * a <code>SolutionSet</code>. The value is stored in <code>kDistance_</code>.
   * @param distance The distance to the k-nearest neighbor.
   */
  public void setKDistance(double distance){
    kDistance_ = distance;
  } // setKDistance

  /** 
   * Gets the distance from the solution to his k-nearest nighbor in a 
   * <code>SolutionSet</code>. Returns the value stored in
   * <code>kDistance_</code>. <b> REQUIRE </b>: this method has to be invoked 
   * after calling <code>setKDistance</code>.
   * @return the distance to k-nearest neighbor.
   */
  double getKDistance(){
    return kDistance_;
  } // getKDistance

  /**
   * Sets the crowding distance of a solution in a <code>SolutionSet</code>.
   * The value is stored in <code>crowdingDistance_</code>.
   * @param distance The crowding distance of the solution.
   */  
  public void setCrowdingDistance(double distance){
    crowdingDistance_ = distance;
  } // setCrowdingDistance


  /** 
   * Gets the crowding distance of the solution into a <code>SolutionSet</code>.
   * Returns the value stored in <code>crowdingDistance_</code>.
   * <b> REQUIRE </b>: this method has to be invoked after calling 
   * <code>setCrowdingDistance</code>.
   * @return the distance crowding distance of the solution.
   */
  public double getCrowdingDistance(){
    return crowdingDistance_;
  } // getCrowdingDistance

  /**
   * Sets the fitness of a solution.
   * The value is stored in <code>fitness_</code>.
   * @param fitness The fitness of the solution.
   */
  public void setFitness(double fitness) {
    fitness_ = fitness;
  } // setFitness

  /**
   * Gets the fitness of the solution.
   * Returns the value of stored in the encodings.variable <code>fitness_</code>.
   * <b> REQUIRE </b>: This method has to be invoked after calling 
   * <code>setFitness()</code>.
   * @return the fitness.
   */
  public double getFitness() {
    return fitness_;
  } // getFitness

  /**
   * Sets the value of the i-th objective.
   * @param i The number identifying the objective.
   * @param value The value to be stored.
   */
  public void setObjective(int i, double value) {
    objective_[i] = value;
  } // setObjective

  /**
   * Returns the value of the i-th objective.
   * @param i The value of the objective.
   */
  public double getObjective(int i) {
    return objective_[i];
  } // getObjective

  /**
   * Returns the number of objectives.
   * @return The number of objectives.
   */
  public int getNumberOfObjectives() {
    if (objective_ == null)
      return 0 ;
    else
      return numberOfObjectives_;
  } // getNumberOfObjectives

  /**  
   * Returns the number of decision variables of the solution.
   * @return The number of decision variables.
   */
  public int numberOfVariables() {
    return problem_.getNumberOfVariables() ;
  } // numberOfVariables

  /** 
   * Returns a string representing the solution.
   * @return The string.
   */
  public String toString() {
    String aux="";
    for (int i = 0; i < this.numberOfObjectives_; i++)
      aux = aux + this.getObjective(i) + " ";

    return aux;
  } // toString

  /**
   * Returns the decision variables of the solution.
   * @return the <code>DecisionVariables</code> object representing the decision
   * variables of the solution.
   */
  public Variable[] getDecisionVariables() {
    return variable_ ;
  } // getDecisionVariables

  /**
   * Sets the decision variables for the solution.
   * @param variables The <code>DecisionVariables</code> object
   * representing the decision variables of the solution.
   */
  public void setDecisionVariables(Variable [] variables) {
    variable_ = variables ;
  } // setDecisionVariables

  public Problem getProblem() {
     return problem_ ;
  }

  /**
   * Indicates if the solution is marked.
   * @return true if the method <code>marked</code> has been called and, after 
   * that, the method <code>unmarked</code> hasn't been called. False in other
   * case.
   */
  public boolean isMarked() {
    return this.marked_;
  } // isMarked

  /**
   * Establishes the solution as marked.
   */
  public void marked() {
    this.marked_ = true;
  } // marked

  /**
   * Established the solution as unmarked.
   */
  public void unMarked() {
    this.marked_ = false;
  } // unMarked

  /**  
   * Sets the rank of a solution. 
   * @param value The rank of the solution.
   */
  public void setRank(int value){
    this.rank_ = value;
  } // setRank

  /**
   * Gets the rank of the solution.
   * <b> REQUIRE </b>: This method has to be invoked after calling 
   * <code>setRank()</code>.
   * @return the rank of the solution.
   */
  public int getRank(){
    return this.rank_;
  } // getRank

  /**
   * Sets the overall constraints violated by the solution.
   * @param value The overall constraints violated by the solution.
   */
  public void setOverallConstraintViolation(double value) {
    this.overallConstraintViolation_ = value;
  } // setOverallConstraintViolation

  /**
   * Gets the overall constraint violated by the solution.
   * <b> REQUIRE </b>: This method has to be invoked after calling 
   * <code>overallConstraintViolation</code>.
   * @return the overall constraint violation by the solution.
   */
  public double getOverallConstraintViolation() {
    return this.overallConstraintViolation_;
  }  //getOverallConstraintViolation


  /**
   * Sets the number of constraints violated by the solution.
   * @param value The number of constraints violated by the solution.
   */
  public void setNumberOfViolatedConstraint(int value) {
    this.numberOfViolatedConstraints_ = value;
  } //setNumberOfViolatedConstraint

  /**
   * Gets the number of constraint violated by the solution.
   * <b> REQUIRE </b>: This method has to be invoked after calling
   * <code>setNumberOfViolatedConstraint</code>.
   * @return the number of constraints violated by the solution.
   */
  public int getNumberOfViolatedConstraint() {
    return this.numberOfViolatedConstraints_;
  } // getNumberOfViolatedConstraint

  /**
   * Sets the location of the solution into a solutionSet. 
   * @param location The location of the solution.
   */
  public void setLocation(int location) {
    this.location_ = location;
  } // setLocation

  /**
   * Gets the location of this solution in a <code>SolutionSet</code>.
   * <b> REQUIRE </b>: This method has to be invoked after calling
   * <code>setLocation</code>.
   * @return the location of the solution into a solutionSet
   */
  public int getLocation() {
    return this.location_;
  } // getLocation

  /**
   * Sets the type of the encodings.variable.
   * @param type The type of the encodings.variable.
   */
  //public void setType(String type) {
   // type_ = Class.forName("") ;
  //} // setType

  /**
   * Sets the type of the encodings.variable.
   * @param type The type of the encodings.variable.
   */
  public void setType(SolutionType type) {
    type_ = type ;
  } // setType

  /**
   * Gets the type of the encodings.variable
   * @return the type of the encodings.variable
   */
  public SolutionType getType() {
    return type_;
  } // getType

  /** 
   * Returns the aggregative value of the solution
   * @return The aggregative value.
   */
  public double getAggregativeValue() {
    double value = 0.0;                
    for (int i = 0; i < getNumberOfObjectives(); i++){
      value += getObjective(i);
    }                
    return value;
  } // getAggregativeValue

  /**
   * Returns the number of bits of the chromosome in case of using a binary
   * representation
   * @return The number of bits if the case of binary variables, 0 otherwise
   * This method had a bug which was fixed by Rafael Olaechea
   */
  public int getNumberOfBits() {
    int bits = 0 ;

    for (int i = 0;  i < variable_.length  ; i++)
      if ((variable_[i].getVariableType() == jmetal.encodings.variable.Binary.class) ||
          (variable_[i].getVariableType() == jmetal.encodings.variable.BinaryReal.class))

        bits += ((Binary)(variable_[i])).getNumberOfBits() ;

    return bits ;
  } // getNumberOfBits
} // Solution
