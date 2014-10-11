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

package org.uma.jmetal45.metaheuristic.multiobjective.omopso;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.NonUniformMutation;
import org.uma.jmetal45.operator.mutation.UniformMutation;
import org.uma.jmetal45.util.Distance;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.NonDominatedSolutionList;
import org.uma.jmetal45.util.archive.CrowdingArchive;
import org.uma.jmetal45.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal45.util.comparator.DominanceComparator;
import org.uma.jmetal45.util.comparator.EpsilonDominanceComparator;
import org.uma.jmetal45.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal45.util.random.PseudoRandom;

import java.util.Comparator;

/** Class implementing the OMOPSO algorithm */
public class OMOPSO implements Algorithm {
  private static final long serialVersionUID = -4920101693175195923L;

  private Problem problem ;

  SolutionSetEvaluator evaluator;

  private int swarmSize;
  private int archiveSize;
  private int maxIterations;
  private int currentIteration;

  private SolutionSet swarm;
  private Solution[] localBest;
  private CrowdingArchive leaderArchive;
  private NonDominatedSolutionList epsilonArchive;

  private double[][] speed;

  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> crowdingDistanceComparator;


  private UniformMutation uniformMutation;
  private NonUniformMutation nonUniformMutation;

  private double eta = 0.0075;

  /**
   * Constructor
   */
  @Deprecated
  public OMOPSO() {
    super();
  }

  /** Constructor */
  private OMOPSO(Builder builder) {
    evaluator = builder.evaluator ;
    problem = builder.problem ;

    swarmSize = builder.swarmSize ;
    maxIterations = builder.maxIterations ;
    archiveSize = builder.archiveSize ;

    uniformMutation = builder.uniformMutation ;
    nonUniformMutation = builder.nonUniformMutation ;

    swarm = new SolutionSet(swarmSize);
    localBest = new Solution[swarmSize];
    leaderArchive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives());
    epsilonArchive = new NonDominatedSolutionList(new EpsilonDominanceComparator(eta));

    dominanceComparator = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();

    speed = new double[swarmSize][problem.getNumberOfVariables()];
  }

  /* Getters */
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

  /** Buider class */
  public static class Builder{
    protected SolutionSetEvaluator evaluator;

    protected Problem problem;

    private int swarmSize = 100 ;
    private int archiveSize = 100 ;
    private int maxIterations = 25000 ;

    private UniformMutation uniformMutation ;
    private NonUniformMutation nonUniformMutation ;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
      this.problem = problem ;
    }

    public Builder setSwarmSize(int swarmSize) {
      this.swarmSize = swarmSize ;

      return this ;
    }

    public Builder setArchiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setUniformMutation(Mutation uniformMutation) {
      this.uniformMutation = (UniformMutation)uniformMutation ;

      return this ;
    }

    public Builder setNonUniformMutation(Mutation nonUniformMutation) {
      this.nonUniformMutation = (NonUniformMutation)nonUniformMutation ;

      return this ;
    }


    public OMOPSO build() {
      return new OMOPSO(this) ;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    createInitialSwarm();
    evaluateSwarm();
    initializeSpeed() ;
    updateEpsilonArchive() ;
    initializeParticleMemory() ;

    Distance.crowdingDistance(leaderArchive);

    while (currentIteration < maxIterations) {
      computeSpeed();
      computeNewPositions();
      mopsoMutation(currentIteration);
      evaluateSwarm();
      updateEpsilonArchive();
      updateParticleMemory() ;

      Distance.crowdingDistance(leaderArchive);
      currentIteration++;
    }

    tearDown();

    return this.leaderArchive;
  }

  protected void createInitialSwarm() throws ClassNotFoundException, JMetalException {
    swarm = new SolutionSet(swarmSize);

    Solution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = new Solution(problem);
      swarm.add(newSolution);
    }
  }

  protected void evaluateSwarm() throws JMetalException {
    swarm = evaluator.evaluate(swarm, problem);
  }

  protected void initializeSpeed() {
    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  protected void updateEpsilonArchive() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      if (leaderArchive.add(particle)) {
        epsilonArchive.add(new Solution(particle));
      }
    }
  }

  protected void initializeParticleMemory()  {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      localBest[i] = particle;
    }
  }

  protected void updateParticleMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBest[i]);
      if (flag != 1) {
        Solution particle = new Solution(swarm.get(i));
        localBest[i] = particle;
      }
    }
  }

  /** Update the speed of each particle */
  private void computeSpeed() throws JMetalException {
    double r1, r2, W, C1, C2;
    XReal bestGlobal;

    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i)) ;
      XReal bestParticle = new XReal(localBest[i]) ;

      //Select a global localBest for calculate the speed of particle i, bestGlobal
      Solution one, two;
      int pos1 = PseudoRandom.randInt(0, leaderArchive.size() - 1);
      int pos2 = PseudoRandom.randInt(0, leaderArchive.size() - 1);
      one = leaderArchive.get(pos1);
      two = leaderArchive.get(pos2);

      if (crowdingDistanceComparator.compare(one, two) < 1) {
        bestGlobal = new XReal(one) ;
      } else {
        bestGlobal = new XReal(two) ;
      }

      //Parameters for velocity equation
      r1 = PseudoRandom.randDouble();
      r2 = PseudoRandom.randDouble();
      C1 = PseudoRandom.randDouble(1.5, 2.0);
      C2 = PseudoRandom.randDouble(1.5, 2.0);
      W = PseudoRandom.randDouble(0.1, 0.5);
      //

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        //Computing the velocity of this particle
        speed[i][var] = W * speed[i][var] + C1 * r1 * (bestParticle.getValue(var) - particle.getValue(var)) +
                C2 * r2 * (bestGlobal.getValue(var) - particle.getValue(var));
      }
    }
  }

  /** Update the position of each particle */
  private void computeNewPositions() throws JMetalException {
    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        particle.setValue(var, particle.getValue(var) + speed[i][var]);
        if (particle.getValue(var) < problem.getLowerLimit(var)) {
          particle.setValue(var, problem.getLowerLimit(var));
          speed[i][var] = speed[i][var] * -1.0;
        }
        if (particle.getValue(var) > problem.getUpperLimit(var)) {
          particle.setValue(var, problem.getUpperLimit(var));
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

  protected void tearDown() {
    evaluator.shutdown();
  }
}
