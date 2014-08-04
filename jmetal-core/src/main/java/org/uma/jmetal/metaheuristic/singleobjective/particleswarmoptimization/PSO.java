//  PSO.java
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

package org.uma.jmetal.metaheuristic.singleobjective.particleswarmoptimization;

import org.uma.jmetal.core.*;
import org.uma.jmetal.operator.selection.BestSolutionSelection;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XReal;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Class implementing a single-objective PSO algorithm
 */
public class PSO extends Algorithm {

  /**
   *
   */
  private static final long serialVersionUID = -2657053204274107750L;
  int evaluations_;
  /**
   * Comparator object
   */
  Comparator<Solution> comparator;
  Operator findBestSolution;
  double r1Max;
  double r1Min;
  double r2Max;
  double r2Min;
  double C1Max;
  double C1Min;
  double C2Max;
  double C2Min;
  double WMax;
  double WMin;
  double ChVel1;
  double ChVel2;
  boolean success;

  private int swarmSize;
  private int maxIterations;
  private int iteration;
  private SolutionSet swarm;
  private Solution[] localBest;
  private Solution globalBest;
  private double[][] speed;
  private Operator polynomialMutation;

  private double deltaMax[];
  private double deltaMin[];

  /** Constructor */

  public PSO() {
    super();

    r1Max = 1.0;
    r1Min = 0.0;
    r2Max = 1.0;
    r2Min = 0.0;
    C1Max = 1.5;
    C1Min = 1.5;
    C2Max = 1.5;
    C2Min = 1.5;
    WMax = 0.9;
    WMin = 0.1;
    ChVel1 = 1.0;
    ChVel2 = 1.0;

    comparator = new ObjectiveComparator(0); // Single objective comparator
    HashMap<String, Object> selectionParameters = new HashMap<String, Object>();
    selectionParameters.put("comparator", comparator);
    findBestSolution = new BestSolutionSelection(selectionParameters);

    evaluations_ = 0;
  }

  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    swarmSize = (Integer) getInputParameter("swarmSize");
    maxIterations = (Integer) getInputParameter("maxIterations");

    polynomialMutation = operators.get("mutation");

    iteration = 0;

    success = false;

    swarm = new SolutionSet(swarmSize);
    localBest = new Solution[swarmSize];

    // Create the speed vector
    speed = new double[swarmSize][problem.getNumberOfVariables()];


    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperLimit(i) -
        problem.getLowerLimit(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }
  }

  // Adaptive inertia 
  private double inertiaWeight(int iter, int miter, double wmax, double wmin) {
    //return wmax; // - (((wmax-wmin)*(double)iter)/(double)miter);
    return wmax - (((wmax - wmin) * (double) iter) / (double) miter);
  }

  // constriction coefficient (M. Clerc)
  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    //rho = 1.0 ;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / Math.abs((2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho)));
    }
  }


  // velocity bounds
  private double velocityConstriction(double v, double[] deltaMax,
    double[] deltaMin, int variableIndex,
    int particleIndex) throws IOException {

    return v;
  }

  /**
   * Update the speed of each particle
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  private void computeSpeed(int iter, int miter) throws JMetalException, IOException {
    double r1, r2;
    //double W ;
    double C1, C2;
    double wmax, wmin, deltaMax, deltaMin;
    XReal bestGlobal;

    bestGlobal = new XReal(globalBest);

    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      XReal bestParticle = new XReal(localBest[i]);

      C1Max = 2.5;
      C1Min = 1.5;
      C2Max = 2.5;
      C2Min = 1.5;

      r1 = PseudoRandom.randDouble(r1Min, r1Max);
      r2 = PseudoRandom.randDouble(r2Min, r2Max);
      C1 = PseudoRandom.randDouble(C1Min, C1Max);
      C2 = PseudoRandom.randDouble(C2Min, C2Max);
      //W =  PseudoRandom.randDouble(WMin, WMax);
      //

      WMax = 0.9;
      WMin = 0.9;
      ChVel1 = 1.0;
      ChVel2 = 1.0;

      C1 = 2.5;
      C2 = 1.5;

      wmax = WMax;
      wmin = WMin;

      C1 = 1.5;
      C2 = 1.5;
      double W = 0.9;
      for (int var = 0; var < particle.size(); var++) {
        //Computing the velocity of this particle 
        speed[i][var] = inertiaWeight(iter, miter, wmax, wmin) * speed[i][var] +
          C1 * r1 * (bestParticle.getValue(var) - particle.getValue(var)) +
          C2 * r2 * (bestGlobal.getValue(var) - particle.getValue(var));
      }
    }
  }

  /**
   * Update the position of each particle
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  private void computeNewPositions() throws JMetalException {
    for (int i = 0; i < swarmSize; i++) {
      //Variable[] particle = swarm.get(i).getDecisionVariables();
      XReal particle = new XReal(swarm.get(i));
      //particle.move(speed[i]);
      for (int var = 0; var < particle.size(); var++) {
        particle.setValue(var, particle.getValue(var) + speed[i][var]);

        if (particle.getValue(var) < problem.getLowerLimit(var)) {
          particle.setValue(var, problem.getLowerLimit(var));
          speed[i][var] = speed[i][var] * ChVel1; //
        }
        if (particle.getValue(var) > problem.getUpperLimit(var)) {
          particle.setValue(var, problem.getUpperLimit(var));
          speed[i][var] = speed[i][var] * ChVel2; //
        }

      }
    }
  }

  /**
   * Apply a mutation operator to some particles in the swarm
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  private void mopsoMutation(int actualIteration, int totalIterations) throws JMetalException {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        polynomialMutation.execute(swarm.get(i));
      }
    }
  }

  /**
   * Runs of the SMPSO algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    initParams();

    success = false;
    globalBest = null;
    //->Step 1 (and 3) Create the initial population and evaluate
    for (int i = 0; i < swarmSize; i++) {
      Solution particle = new Solution(problem);
      problem.evaluate(particle);
      evaluations_++;
      swarm.add(particle);
      if ((globalBest == null) || (particle.getObjective(0) < globalBest.getObjective(0))) {
        globalBest = new Solution(particle);
      }
    }

    //-> Step2. Initialize the speed of each particle to 0
    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }

    //-> Step 6. Initialize the memory of each particle
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      localBest[i] = particle;
    }

    //-> Step 7. Iterations ..        
    while (iteration < maxIterations) {
      int bestIndividual = (Integer) findBestSolution.execute(swarm);
      try {
        //Compute the speed
        computeSpeed(iteration, maxIterations);
      } catch (IOException ex) {
        java.util.logging.Logger.getLogger(PSO.class.getName()).log(Level.SEVERE, null, ex);
      }

      //Compute the new positions for the swarm
      computeNewPositions();

      //Mutate the swarm
      //mopsoMutation(iteration, maxIterations);

      //Evaluate the new swarm in new positions
      for (int i = 0; i < swarm.size(); i++) {
        Solution particle = swarm.get(i);
        problem.evaluate(particle);
        evaluations_++;
      }

      //Actualize the memory of this particle
      for (int i = 0; i < swarm.size(); i++) {
        //int flag = comparator.compare(swarm.get(i), localBest[i]);
        //if (flag < 0) { // the new particle is best_ than the older remember        
        if ((swarm.get(i).getObjective(0) < localBest[i].getObjective(0))) {
          Solution particle = new Solution(swarm.get(i));
          localBest[i] = particle;
        } // if
        if ((swarm.get(i).getObjective(0) < globalBest.getObjective(0))) {
          Solution particle = new Solution(swarm.get(i));
          globalBest = particle;
        } // if
        Double bestCurrentFitness = swarm.best(comparator).getObjective(0);
        JMetalLogger.logger.info("Best: " + bestCurrentFitness);
      }
      iteration++;
    }

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(swarm.get((Integer) findBestSolution.execute(swarm)));

    return resultPopulation;
  }
}
