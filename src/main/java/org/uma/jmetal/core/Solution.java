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

package org.uma.jmetal.core;

import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.encoding.variable.BinaryReal;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Class representing a solutiontype for a problem.
 */
public class Solution implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 7093303373342354904L;
  /**
   * Stores the objectives values of the solutiontype.
   */
  private final double[] objective_;
  /**
   * Stores the problem
   */
  private Problem problem_;
  /**
   * Stores the type of the encoding.variable
   */
  private SolutionType type_;
  /**
   * Stores the decision variables of the solutiontype.
   */
  private Variable[] variable_;
  /**
   * Stores the number of objective values of the solutiontype
   */
  private int numberOfObjectives_;

  /**
   * Stores the so called fitness value. Used in some metaheuristic
   */
  private double fitness_;

  /**
   * Used in algorithm AbYSS, this field is intended to be used to know when a
   * <code>Solution</code> is marked.
   */
  private boolean marked_;

  /**
   * Stores the so called rank of the solutiontype. Used in NSGA-II
   */
  private int rank_;

  /**
   * Stores the overall constraint violation of the solutiontype.
   */
  private double overallConstraintViolation_;

  /**
   * Stores the number of constraints violated by the solutiontype.
   */
  private int numberOfViolatedConstraints_;

  /**
   * This field is intended to be used to know the location of a solutiontype into a
   * <code>SolutionSet</code>. Used in MOCell
   */
  private int location_;

  /**
   * Stores the distance to his k-nearest neighbor into a
   * <code>SolutionSet</code>. Used in SPEA2.
   */
  private double kDistance_;

  /**
   * Stores the crowding distance of the the solutiontype in a
   * <code>SolutionSet</code>. Used in NSGA-II.
   */
  private double crowdingDistance_;

  /**
   * Stores the distance between this solutiontype and a <code>SolutionSet</code>.
   * Used in AbySS.
   */
  private double distanceToSolutionSet_;

  /**
   * Constructor.
   */
  public Solution() {
    problem_ = null;
    marked_ = false;
    overallConstraintViolation_ = 0.0;
    numberOfViolatedConstraints_ = 0;
    type_ = null;
    variable_ = null;
    objective_ = null;
  }

  /**
   * Constructor
   *
   * @param numberOfObjectives Number of objectives of the solutiontype
   *                           <p/>
   *                           This constructor is used mainly to read objective values from a
   *                           file to variables of a SolutionSet to apply quality indicators
   */
  public Solution(int numberOfObjectives) {
    numberOfObjectives_ = numberOfObjectives;
    objective_ = new double[numberOfObjectives];
  }

  /**
   * Constructor.
   *
   * @param problem The problem to solve
   * @throws ClassNotFoundException
   */
  public Solution(Problem problem) throws ClassNotFoundException {
    problem_ = problem;
    type_ = problem.getSolutionType();
    numberOfObjectives_ = problem.getNumberOfObjectives();
    objective_ = new double[numberOfObjectives_];

    // Setting initial values
    fitness_ = 0.0;
    kDistance_ = 0.0;
    crowdingDistance_ = 0.0;
    distanceToSolutionSet_ = Double.POSITIVE_INFINITY;

    variable_ = type_.createVariables();
  }

  /**
   * Constructor
   *
   * @param problem The problem to solve
   */
  public Solution(Problem problem, Variable[] variables) {
    problem_ = problem;
    type_ = problem.getSolutionType();
    numberOfObjectives_ = problem.getNumberOfObjectives();
    objective_ = new double[numberOfObjectives_];

    // Setting initial values
    fitness_ = 0.0;
    kDistance_ = 0.0;
    crowdingDistance_ = 0.0;
    distanceToSolutionSet_ = Double.POSITIVE_INFINITY;

    variable_ = Arrays.copyOf(variables, variables.length);
  }

  /**
   * Copy constructor.
   *
   * @param solution Solution to copy.
   */
  public Solution(Solution solution) {
    problem_ = solution.problem_;
    type_ = solution.type_;

    numberOfObjectives_ = solution.getNumberOfObjectives();
    objective_ = new double[numberOfObjectives_];
    for (int i = 0; i < objective_.length; i++) {
      objective_[i] = solution.getObjective(i);
    }

    variable_ = type_.copyVariables(solution.variable_);
    overallConstraintViolation_ = solution.getOverallConstraintViolation();
    numberOfViolatedConstraints_ = solution.getNumberOfViolatedConstraint();
    distanceToSolutionSet_ = solution.getDistanceToSolutionSet();
    crowdingDistance_ = solution.getCrowdingDistance();
    kDistance_ = solution.getKDistance();
    fitness_ = solution.getFitness();
    marked_ = solution.isMarked();
    rank_ = solution.getRank();
    location_ = solution.getLocation();
  }

  public static Solution getNewSolution(Problem problem)
    throws ClassNotFoundException {
    return new Solution(problem);
  }

  /**
   * Gets the distance from the solutiontype to a <code>SolutionSet</code>. <b>
   * REQUIRE </b>: this method has to be invoked after calling
   * <code>setDistanceToPopulation</code>.
   *
   * @return the distance to a specific solutionSet.
   */
  public double getDistanceToSolutionSet() {
    return distanceToSolutionSet_;
  }

  /**
   * Sets the distance between this solutiontype and a <code>SolutionSet</code>. The
   * value is stored in <code>distanceToSolutionSet_</code>.
   *
   * @param distance The distance to a solutionSet.
   */
  public void setDistanceToSolutionSet(double distance) {
    distanceToSolutionSet_ = distance;
  }

  /**
   * Gets the distance from the solutiontype to his k-nearest nighbor in a
   * <code>SolutionSet</code>. Returns the value stored in
   * <code>kDistance_</code>. <b> REQUIRE </b>: this method has to be invoked
   * after calling <code>setKDistance</code>.
   *
   * @return the distance to k-nearest neighbor.
   */
  double getKDistance() {
    return kDistance_;
  }

  /**
   * Sets the distance between the solutiontype and its k-nearest neighbor in a
   * <code>SolutionSet</code>. The value is stored in <code>kDistance_</code>.
   *
   * @param distance The distance to the k-nearest neighbor.
   */
  public void setKDistance(double distance) {
    kDistance_ = distance;
  }

  /**
   * Gets the crowding distance of the solutiontype into a <code>SolutionSet</code>.
   * Returns the value stored in <code>crowdingDistance_</code>. <b> REQUIRE
   * </b>: this method has to be invoked after calling
   * <code>setCrowdingDistance</code>.
   *
   * @return the distance crowding distance of the solutiontype.
   */
  public double getCrowdingDistance() {
    return crowdingDistance_;
  }

  /**
   * Sets the crowding distance of a solutiontype in a <code>SolutionSet</code>. The
   * value is stored in <code>crowdingDistance_</code>.
   *
   * @param distance The crowding distance of the solutiontype.
   */
  public void setCrowdingDistance(double distance) {
    crowdingDistance_ = distance;
  }

  /**
   * Gets the fitness of the solutiontype. Returns the value of stored in the
   * encoding.variable <code>fitness_</code>. <b> REQUIRE </b>: This method has
   * to be invoked after calling <code>setFitness()</code>.
   *
   * @return the fitness.
   */
  public double getFitness() {
    return fitness_;
  }

  /**
   * Sets the fitness of a solutiontype. The value is stored in
   * <code>fitness_</code>.
   *
   * @param fitness The fitness of the solutiontype.
   */
  public void setFitness(double fitness) {
    fitness_ = fitness;
  }

  /**
   * Sets the value of the i-th objective.
   *
   * @param i     The number identifying the objective.
   * @param value The value to be stored.
   */
  public void setObjective(int i, double value) {
    objective_[i] = value;
  }

  /**
   * Returns the value of the i-th objective.
   *
   * @param i The value of the objective.
   */
  public double getObjective(int i) {
    return objective_[i];
  }

  /**
   * Returns the number of objectives.
   *
   * @return The number of objectives.
   */
  public int getNumberOfObjectives() {
    if (objective_ == null) {
      return 0;
    } else {
      return numberOfObjectives_;
    }
  }

  /**
   * Returns the number of decision variables of the solutiontype.
   *
   * @return The number of decision variables.
   */
  public int numberOfVariables() {
    return problem_.getNumberOfVariables();
  }

  /**
   * Returns a string representing the solutiontype.
   *
   * @return The string.
   */
  public String toString() {
    String aux = "";
    for (int i = 0; i < this.numberOfObjectives_; i++) {
      aux = aux + this.getObjective(i) + " ";
    }
    return aux;
  }

  /**
   * Returns the decision variables of the solutiontype.
   *
   * @return the <code>DecisionVariables</code> object representing the decision
   * variables of the solutiontype.
   */
  public Variable[] getDecisionVariables() {
    return variable_;
  }

  /**
   * Sets the decision variables for the solutiontype.
   *
   * @param variables The <code>DecisionVariables</code> object representing the
   *                  decision variables of the solutiontype.
   */
  public void setDecisionVariables(Variable[] variables) {
    variable_ = new Variable[variables.length];
    System.arraycopy(variables, 0, variable_, 0, variables.length);
  }

  public Problem getProblem() {
    return problem_;
  }

  /**
   * Indicates if the solutiontype is marked.
   *
   * @return true if the method <code>marked</code> has been called and, after
   * that, the method <code>unmarked</code> hasn't been called. False in
   * other case.
   */
  public boolean isMarked() {
    return this.marked_;
  }

  /**
   * Establishes the solutiontype as marked.
   */
  public void marked() {
    this.marked_ = true;
  }

  /**
   * Established the solutiontype as unmarked.
   */
  public void unMarked() {
    this.marked_ = false;
  }

  /**
   * Gets the rank of the solutiontype. <b> REQUIRE </b>: This method has to be
   * invoked after calling <code>setRank()</code>.
   *
   * @return the rank of the solutiontype.
   */
  public int getRank() {
    return this.rank_;
  }

  /**
   * Sets the rank of a solutiontype.
   *
   * @param value The rank of the solutiontype.
   */
  public void setRank(int value) {
    this.rank_ = value;
  }

  /**
   * Gets the overall constraint violated by the solutiontype. <b> REQUIRE </b>:
   * This method has to be invoked after calling
   * <code>overallConstraintViolation</code>.
   *
   * @return the overall constraint violation by the solutiontype.
   */
  public double getOverallConstraintViolation() {
    return this.overallConstraintViolation_;
  }

  /**
   * Sets the overall constraints violated by the solutiontype.
   *
   * @param value The overall constraints violated by the solutiontype.
   */
  public void setOverallConstraintViolation(double value) {
    this.overallConstraintViolation_ = value;
  }

  /**
   * Gets the number of constraint violated by the solutiontype. <b> REQUIRE </b>:
   * This method has to be invoked after calling
   * <code>setNumberOfViolatedConstraint</code>.
   *
   * @return the number of constraints violated by the solutiontype.
   */
  public int getNumberOfViolatedConstraint() {
    return this.numberOfViolatedConstraints_;
  }

  /**
   * Sets the number of constraints violated by the solutiontype.
   *
   * @param value The number of constraints violated by the solutiontype.
   */
  public void setNumberOfViolatedConstraint(int value) {
    this.numberOfViolatedConstraints_ = value;
  }

  /**
   * Gets the location of this solutiontype in a <code>SolutionSet</code>. <b>
   * REQUIRE </b>: This method has to be invoked after calling
   * <code>setLocation</code>.
   *
   * @return the location of the solutiontype into a solutionSet
   */
  public int getLocation() {
    return this.location_;
  }

  /**
   * Sets the location of the solutiontype into a solutionSet.
   *
   * @param location The location of the solutiontype.
   */
  public void setLocation(int location) {
    this.location_ = location;
  }

  /**
   * Gets the type of the encoding.variable
   *
   * @return the type of the encoding.variable
   */
  public SolutionType getType() {
    return type_;
  }

  /**
   * Sets the type of the encoding.variable.
   *
   * @param type The type of the encoding.variable.
   */
  public void setType(SolutionType type) {
    type_ = type;
  }

  /**
   * Returns the aggregative value of the solutiontype
   *
   * @return The aggregative value.
   */
  public double getAggregativeValue() {
    double value = 0.0;
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      value += getObjective(i);
    }
    return value;
  }

  /**
   * Returns the number of bits of the chromosome in case of using a binary
   * representation
   *
   * @return The number of bits if the case of binary variables, 0 otherwise
   * This method had a bug which was fixed by Rafael Olaechea
   */
  public int getNumberOfBits() {
    int bits = 0;

    for (Variable aVariable_ : variable_) {
      if ((aVariable_.getVariableType() == Binary.class)
        || (aVariable_.getVariableType() == BinaryReal.class)) {

        bits += ((Binary) (aVariable_)).getNumberOfBits();
      }
    }

    return bits;
  }
}
