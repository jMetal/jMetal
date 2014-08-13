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

import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.encoding.variable.BinaryReal;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Class representing a solution for a problem.
 */
public class Solution implements Serializable {
  private static final long serialVersionUID = 7093303373342354904L;
  /**
   * Stores the objectives values of the solution.
   */
  private final double[] objective;
  /**
   * Stores the problem
   */
  private Problem problem;
  /**
   * Stores the type of the encoding.variable
   */
  private SolutionType type;
  /**
   * Stores the decision variables of the solution.
   */
  private Variable[] variable;
  /**
   * Stores the number of objective values of the solution
   */
  private int numberOfObjectives;

  /**
   * Stores the so called fitness value. Used in some metaheuristics
   */
  private double fitness;

  /**
   * Used in algorithm AbYSS, this field is intended to be used to know when a
   * <code>Solution</code> is marked.
   */
  private boolean marked;

  /**
   * Stores the so called rank of the solution. Used in NSGA-II
   */
  private int rank;

  /**
   * Stores the overall constraint violation of the solution.
   */
  private double overallConstraintViolation;

  /**
   * Stores the number of constraints violated by the solution.
   */
  private int numberOfViolatedConstraints;

  /**
   * This field is intended to be used to know the location of a solution into a
   * <code>SolutionSet</code>. Used in MOCell
   */
  private int location;

  /**
   * Stores the distance to his k-nearest neighbor into a
   * <code>SolutionSet</code>. Used in SPEA2.
   */
  private double kDistance;

  /**
   * Stores the crowding distance of the the solution in a
   * <code>SolutionSet</code>. Used in NSGA-II.
   */
  private double crowdingDistance;

  /**
   * Stores the distance between this solution and a <code>SolutionSet</code>.
   * Used in AbySS.
   */
  private double distanceToSolutionSet;

  /**
   * Constructor.
   */
  public Solution() {
    problem = null;
    marked = false;
    overallConstraintViolation = 0.0;
    numberOfViolatedConstraints = 0;
    type = null;
    variable = null;
    objective = null;
  }

  /**
   * Constructor
   *
   * @param numberOfObjectives Number of objectives of the solution
   *                           <p/>
   *                           This constructor is used mainly to read objective values from a
   *                           file to variables of a SolutionSet to apply quality indicators
   */
  public Solution(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
    objective = new double[numberOfObjectives];
  }

  /**
   * Constructor.
   *
   * @param problem The problem to solve
   * @throws ClassNotFoundException
   */
  public Solution(Problem problem) throws ClassNotFoundException {
    this.problem = problem;
    type = problem.getSolutionType();
    numberOfObjectives = problem.getNumberOfObjectives();
    objective = new double[numberOfObjectives];

    // Setting initial values
    fitness = 0.0;
    kDistance = 0.0;
    crowdingDistance = 0.0;
    distanceToSolutionSet = Double.POSITIVE_INFINITY;

    variable = type.createVariables();
  }

  /**
   * Constructor
   *
   * @param problem The problem to solve
   */
  public Solution(Problem problem, Variable[] variables) {
    this.problem = problem;
    type = problem.getSolutionType();
    numberOfObjectives = problem.getNumberOfObjectives();
    objective = new double[numberOfObjectives];

    // Setting initial values
    fitness = 0.0;
    kDistance = 0.0;
    crowdingDistance = 0.0;
    distanceToSolutionSet = Double.POSITIVE_INFINITY;

    variable = Arrays.copyOf(variables, variables.length);
  }

  /**
   * Copy constructor.
   *
   * @param solution Solution to copy.
   */
  public Solution(Solution solution) {
    problem = solution.problem;
    type = solution.type;

    numberOfObjectives = solution.getNumberOfObjectives();
    objective = new double[numberOfObjectives];
    for (int i = 0; i < objective.length; i++) {
      objective[i] = solution.getObjective(i);
    }

    variable = type.copyVariables(solution.variable);
    overallConstraintViolation = solution.getOverallConstraintViolation();
    numberOfViolatedConstraints = solution.getNumberOfViolatedConstraint();
    distanceToSolutionSet = solution.getDistanceToSolutionSet();
    crowdingDistance = solution.getCrowdingDistance();
    kDistance = solution.getKDistance();
    fitness = solution.getFitness();
    marked = solution.isMarked();
    rank = solution.getRank();
    location = solution.getLocation();
  }

  /**
   * Gets the distance from the solution to a <code>SolutionSet</code>. <b>
   * REQUIRE </b>: this method has to be invoked after calling
   * <code>setDistanceToPopulation</code>.
   *
   * @return the distance to a specific solutionSet.
   */
  public double getDistanceToSolutionSet() {
    return distanceToSolutionSet;
  }

  /**
   * Sets the distance between this solution and a <code>SolutionSet</code>. The
   * value is stored in <code>distanceToSolutionSet</code>.
   *
   * @param distance The distance to a solutionSet.
   */
  public void setDistanceToSolutionSet(double distance) {
    distanceToSolutionSet = distance;
  }

  /**
   * Gets the distance from the solution to his k-nearest nighbor in a
   * <code>SolutionSet</code>. Returns the value stored in
   * <code>kDistance</code>. <b> REQUIRE </b>: this method has to be invoked
   * after calling <code>setKDistance</code>.
   *
   * @return the distance to k-nearest neighbor.
   */
  double getKDistance() {
    return kDistance;
  }

  /**
   * Sets the distance between the solution and its k-nearest neighbor in a
   * <code>SolutionSet</code>. The value is stored in <code>kDistance</code>.
   *
   * @param distance The distance to the k-nearest neighbor.
   */
  public void setKDistance(double distance) {
    kDistance = distance;
  }

  /**
   * Gets the crowding distance of the solution into a <code>SolutionSet</code>.
   * Returns the value stored in <code>crowdingDistance</code>. <b> REQUIRE
   * </b>: this method has to be invoked after calling
   * <code>setCrowdingDistance</code>.
   *
   * @return the distance crowding distance of the solution.
   */
  public double getCrowdingDistance() {
    return crowdingDistance;
  }

  /**
   * Sets the crowding distance of a solution in a <code>SolutionSet</code>. The
   * value is stored in <code>crowdingDistance</code>.
   *
   * @param distance The crowding distance of the solution.
   */
  public void setCrowdingDistance(double distance) {
    crowdingDistance = distance;
  }

  /**
   * Gets the fitness of the solution. Returns the value of stored in the
   * encoding.variable <code>fitness</code>. <b> REQUIRE </b>: This method has
   * to be invoked after calling <code>setFitness()</code>.
   *
   * @return the fitness.
   */
  public double getFitness() {
    return fitness;
  }

  /**
   * Sets the fitness of a solution. The value is stored in
   * <code>fitness</code>.
   *
   * @param fitness The fitness of the solution.
   */
  public void setFitness(double fitness) {
    this.fitness = fitness;
  }

  /**
   * Sets the value of the i-th objective.
   *
   * @param i     The number identifying the objective.
   * @param value The value to be stored.
   */
  public void setObjective(int i, double value) {
    objective[i] = value;
  }

  /**
   * Returns the value of the i-th objective.
   *
   * @param i The value of the objective.
   */
  public double getObjective(int i) {
    return objective[i];
  }

  /**
   * Returns the number of objectives.
   *
   * @return The number of objectives.
   */
  public int getNumberOfObjectives() {
    if (objective == null) {
      return 0;
    } else {
      return numberOfObjectives;
    }
  }

  public double[] getObjectives() {
    return objective ;
  }

  /**
   * Returns the number of decision variables of the solution.
   *
   * @return The number of decision variables.
   */
  public int numberOfVariables() {
    return problem.getNumberOfVariables();
  }

  /**
   * Returns the decision variables of the solution.
   *
   * @return the <code>DecisionVariables</code> object representing the decision
   * variables of the solution.
   */
  public Variable[] getDecisionVariables() {
    return variable;
  }

  /**
   * Sets the decision variables for the solution.
   *
   * @param variables The <code>DecisionVariables</code> object representing the
   *                  decision variables of the solution.
   */
  public void setDecisionVariables(Variable[] variables) {
    variable = new Variable[variables.length];
    System.arraycopy(variables, 0, variable, 0, variables.length);
  }

  public Problem getProblem() {
    return problem;
  }

  /**
   * Indicates if the solution is marked.
   *
   * @return true if the method <code>marked</code> has been called and, after
   * that, the method <code>unmarked</code> hasn't been called. False in
   * other case.
   */
  public boolean isMarked() {
    return this.marked;
  }

  /**
   * Establishes the solution as marked.
   */
  public void marked() {
    this.marked = true;
  }

  /**
   * Established the solution as unmarked.
   */
  public void unMarked() {
    this.marked = false;
  }

  /**
   * Gets the rank of the solution. <b> REQUIRE </b>: This method has to be
   * invoked after calling <code>setRank()</code>.
   *
   * @return the rank of the solution.
   */
  public int getRank() {
    return this.rank;
  }

  /**
   * Sets the rank of a solution.
   *
   * @param value The rank of the solution.
   */
  public void setRank(int value) {
    this.rank = value;
  }

  /**
   * Gets the overall constraint violated by the solution. <b> REQUIRE </b>:
   * This method has to be invoked after calling
   * <code>overallConstraintViolation</code>.
   *
   * @return the overall constraint violation by the solution.
   */
  public double getOverallConstraintViolation() {
    return this.overallConstraintViolation;
  }

  /**
   * Sets the overall constraints violated by the solution.
   *
   * @param value The overall constraints violated by the solution.
   */
  public void setOverallConstraintViolation(double value) {
    this.overallConstraintViolation = value;
  }

  /**
   * Gets the number of constraint violated by the solution. <b> REQUIRE </b>:
   * This method has to be invoked after calling
   * <code>setNumberOfViolatedConstraint</code>.
   *
   * @return the number of constraints violated by the solution.
   */
  public int getNumberOfViolatedConstraint() {
    return this.numberOfViolatedConstraints;
  }

  /**
   * Sets the number of constraints violated by the solution.
   *
   * @param value The number of constraints violated by the solution.
   */
  public void setNumberOfViolatedConstraint(int value) {
    this.numberOfViolatedConstraints = value;
  }

  /**
   * Gets the location of this solution in a <code>SolutionSet</code>. <b>
   * REQUIRE </b>: This method has to be invoked after calling
   * <code>setLocation</code>.
   *
   * @return the location of the solution into a solutionSet
   */
  public int getLocation() {
    return this.location;
  }

  /**
   * Sets the location of the solution into a solutionSet.
   *
   * @param location The location of the solution.
   */
  public void setLocation(int location) {
    this.location = location;
  }

  /**
   * Gets the type of the encoding.variable
   *
   * @return the type of the encoding.variable
   */
  public SolutionType getType() {
    return type;
  }

  /**
   * Sets the type of the encoding.variable.
   *
   * @param type The type of the encoding.variable.
   */
  public void setType(SolutionType type) {
    this.type = type;
  }

  /**
   * Returns the aggregative value of the solution
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
   * Returns a string representing the solution.
   *
   * @return The string.
   */
  @Override public String toString() {
    return "Solution{" +
      "objective=" + Arrays.toString(objective) +
      ", type=" + type +
      ", variable=" + Arrays.toString(variable) +
      ", numberOfObjectives=" + numberOfObjectives +
      ", numberOfViolatedConstraints=" + numberOfViolatedConstraints +
      '}';
  }

  @Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + numberOfObjectives;
	  result = prime * result + numberOfViolatedConstraints;
	  result = prime * result + Arrays.hashCode(objective);
	  result = prime * result + ((type == null) ? 0 : type.hashCode());
	  result = prime * result + Arrays.hashCode(variable);
	  return result;
  }

	@Override
  public boolean equals(Object obj) {
	  if (this == obj) {
		  return true;
	  }
	  if (obj == null) {
		  return false;
	  }
	  if (!(obj instanceof Solution)) {
		  return false;
	  }
	  Solution other = (Solution) obj;
	  if (numberOfObjectives != other.numberOfObjectives) {
		  return false;
	  }
	  if (numberOfViolatedConstraints != other.numberOfViolatedConstraints) {
		  return false;
	  }
	  if (!Arrays.equals(objective, other.objective)) {
		  return false;
	  }
	  if (type == null) {
		  if (other.type != null) {
			  return false;
		  }
	  } else if (!type.equals(other.type)) {
		  return false;
	  }
	  if (!Arrays.equals(variable, other.variable)) {
		  return false;
	  }
	  return true;
  }

}
