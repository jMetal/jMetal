//  OMOPSO.java
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

package org.uma.jmetal.metaheuristic.multiobjective.omopso;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.mutation.NonUniformMutation;
import org.uma.jmetal.operator.mutation.UniformMutation;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.NonDominatedSolutionList;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EpsilonDominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

/** Class implementing the  OMOPSO algorithm */
public class OMOPSO extends Algorithm {

  private static final long serialVersionUID = -4920101693175195923L;

  private int swarmSize;
  private int archiveSize;
  private int maxIterations;
  private int currentIteration;

  /**
   * Perturbation used by the non-uniform mutation
   */
  private double perturbation_;

  private SolutionSet swarm;
  private Solution[] localBest;
  private CrowdingArchive leaderArchive;
  private NonDominatedSolutionList epsilonArchive;

  private double[][] speed;

  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> crowdingDistanceComparator;

  /**
   * Stores a <code>Distance</code> object
   */
  private Distance distance_;

  private UniformMutation uniformMutation;
  private NonUniformMutation nonUniformMutation;

  /**
   * eta_ value
   */
  private double eta_ = 0.0075;

  /**
   * Constructor
   */
  @Deprecated
  public OMOPSO() {
    super();
  }

  /** Constructor */
  private OMOPSO(Builder builder) {
    swarmSize = builder.swarmSize ;
    maxIterations = builder.maxIterations ;
    archiveSize = builder.archiveSize ;

    uniformMutation = builder.uniformMutation ;
    nonUniformMutation = builder.nonUniformMutation ;

    swarm = new SolutionSet(swarmSize);
    localBest = new Solution[swarmSize];
    leaderArchive = new CrowdingArchive(archiveSize, problem_.getNumberOfObjectives());
    epsilonArchive = new NonDominatedSolutionList(new EpsilonDominanceComparator(eta_));

    dominanceComparator = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();
    distance_ = new Distance();

    speed = new double[swarmSize][problem_.getNumberOfVariables()];
  }

  /* getters/setters */

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getSwarmSize() {
    return swarmSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public UniformMutation getUniformMutation() {
    return uniformMutation;
  }

  public NonUniformMutation getNonUniformMutation() {
    return nonUniformMutation;
  }

  /** execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {

    //->Step 1 (and 3) Create the initial population and evaluate
    for (int i = 0; i < swarmSize; i++) {
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      problem_.evaluateConstraints(particle);
      swarm.add(particle);
    }

    //-> Step2. Initialize the speed of each particle to 0
    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }

    // Step4 and 5
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      if (leaderArchive.add(particle)) {
        epsilonArchive.add(new Solution(particle));
      }
    }

    //-> Step 6. Initialice the memory of each particle
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      localBest[i] = particle;
    }

    //Crowding the leaderArchive
    distance_.crowdingDistanceAssignment(leaderArchive);

    //-> Step 7. Iterations ..
    while (currentIteration < maxIterations) {
      //Compute the speed
      computeSpeed();

      //Compute the new positions for the swarm
      computeNewPositions();

      //Mutate the swarm
      mopsoMutation(currentIteration);

      //Evaluate the new swarm in new positions
      for (int i = 0; i < swarm.size(); i++) {
        Solution particle = swarm.get(i);
        problem_.evaluate(particle);
        problem_.evaluateConstraints(particle);
      }

      //Actualize the archive
      for (int i = 0; i < swarm.size(); i++) {
        Solution particle = new Solution(swarm.get(i));
        if (leaderArchive.add(particle)) {
          epsilonArchive.add(new Solution(particle));
        }
      }

      //Actualize the memory of this particle
      for (int i = 0; i < swarm.size(); i++) {
        int flag = dominanceComparator.compare(swarm.get(i), localBest[i]);
        if (flag != 1) {
          Solution particle = new Solution(swarm.get(i));
          localBest[i] = particle;
        }
      }

      //Crowding the leaderArchive
      distance_.crowdingDistanceAssignment(leaderArchive);
      currentIteration++;
    }

    return this.leaderArchive;
  }

  /** Update the speed of each particle */
  private void computeSpeed() throws JMetalException {
    double r1, r2, W, C1, C2;
    Variable[] bestGlobal;

    for (int i = 0; i < swarmSize; i++) {
      Variable[] particle = swarm.get(i).getDecisionVariables();
      Variable[] bestParticle = localBest[i].getDecisionVariables();

      //Select a global localBest for calculate the speed of particle i, bestGlobal
      Solution one, two;
      int pos1 = PseudoRandom.randInt(0, leaderArchive.size() - 1);
      int pos2 = PseudoRandom.randInt(0, leaderArchive.size() - 1);
      one = leaderArchive.get(pos1);
      two = leaderArchive.get(pos2);

      if (crowdingDistanceComparator.compare(one, two) < 1) {
        bestGlobal = one.getDecisionVariables();
      } else {
        bestGlobal = two.getDecisionVariables();
      }

      //Parameters for velocity equation
      r1 = PseudoRandom.randDouble();
      r2 = PseudoRandom.randDouble();
      C1 = PseudoRandom.randDouble(1.5, 2.0);
      C2 = PseudoRandom.randDouble(1.5, 2.0);
      W = PseudoRandom.randDouble(0.1, 0.5);
      //

      for (int var = 0; var < particle.length; var++) {
        //Computing the velocity of this particle
        speed[i][var] = W * speed[i][var] +
          C1 * r1 * (bestParticle[var].getValue() -
            particle[var].getValue()) +
          C2 * r2 * (bestGlobal[var].getValue() -
            particle[var].getValue());
      }
    }
  }

  /** Update the position of each particle */
  private void computeNewPositions() throws JMetalException {
    for (int i = 0; i < swarmSize; i++) {
      Variable[] particle = swarm.get(i).getDecisionVariables();
      for (int var = 0; var < particle.length; var++) {
        particle[var].setValue(particle[var].getValue() + speed[i][var]);
        if (particle[var].getValue() < problem_.getLowerLimit(var)) {
          particle[var].setValue(problem_.getLowerLimit(var));
          speed[i][var] = speed[i][var] * -1.0;
        }
        if (particle[var].getValue() > problem_.getUpperLimit(var)) {
          particle[var].setValue(problem_.getUpperLimit(var));
          speed[i][var] = speed[i][var] * -1.0;
        }
      }
    }
  }

  /**  Apply a mutation operator to all particles in the swarm (perturbation) */
  private void mopsoMutation(int actualIteration)  {
    nonUniformMutation.setCurrentIteration(actualIteration);

    for (int i = 0; i < swarm.size(); i++) {
      if (i % 3 == 0) {
        nonUniformMutation.execute(swarm.get(i));
      } else if (i % 3 == 1) {
        uniformMutation.execute(swarm.get(i));
      } else {
      }
    }
  }

  /** Buider class */
  public static class Builder{
    private int swarmSize = 100 ;
    private int archiveSize = 100 ;
    private int maxIterations = 25000 ;

    private UniformMutation uniformMutation ;
    private NonUniformMutation nonUniformMutation ;

    public Builder swarmSize(int swarmSize) {
      this.swarmSize = swarmSize ;

      return this ;
    }

    public Builder archiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder maxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder uniformMutation(UniformMutation uniformMutation) {
      this.uniformMutation = uniformMutation ;

      return this ;
    }

    public Builder nonUniformMutation(NonUniformMutation nonUniformMutation) {
      this.nonUniformMutation = nonUniformMutation ;

      return this ;
    }


    public OMOPSO build() {
      return new OMOPSO(this) ;
    }
  }
}
