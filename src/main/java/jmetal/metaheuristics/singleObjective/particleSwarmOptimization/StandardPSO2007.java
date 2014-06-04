//  StandardPSO2007.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package jmetal.metaheuristics.singleObjective.particleSwarmOptimization;

import jmetal.core.*;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.util.AdaptiveRandomNeighborhood;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.comparator.ObjectiveComparator;
import jmetal.util.random.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Class implementing a Standard PSO 2007 algorithm
 */
public class StandardPSO2007 extends Algorithm {

  int evaluations_;
  /**
   * Comparator object
   */
  Comparator comparator_;
  Operator findBestSolution_;
  private SolutionSet swarm_;
  private int swarmSize_;
  private int maxIterations_;
  private int iteration_;
  private int numberOfParticlesToInform_; // Referred a K in the SPSO document
  private Solution[] localBest_;
  private Solution[] neighborhoodBest_;
  private double[][] speed_;
  private AdaptiveRandomNeighborhood neighborhood_;
  private double W_;
  private double C_;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   */
  public StandardPSO2007() {
    super();

    W_ = 1.0 / (2.0 * Math.log(2)); //0.721;
    C_ = 1.0 / 2.0 + Math.log(2); //1.193;

    comparator_ = new ObjectiveComparator(0); // Single objective comparator
    HashMap parameters; // Operator parameters

    parameters = new HashMap();
    parameters.put("comparator", comparator_);
    findBestSolution_ = new BestSolutionSelection(parameters);

    evaluations_ = 0;
  }

  public double getW() {
    return W_;
  }

  public double getC() {
    return C_;
  }

  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    swarmSize_ = (Integer) getInputParameter("swarmSize");
    maxIterations_ = (Integer) getInputParameter("maxIterations");
    numberOfParticlesToInform_ =
      (Integer) getInputParameter("numberOfParticlesToInform");

    //Configuration.logger_.info("Swarm size: " + swarmSize_) ;

    iteration_ = 0;

    swarm_ = new SolutionSet(swarmSize_);
    localBest_ = new Solution[swarmSize_];
    neighborhoodBest_ = new Solution[swarmSize_];

    // Create the speed_ vector
    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
  }


  private Solution getNeighborBest(int i) {
    Solution bestLocalBestSolution = null;

    try {
      for (int index : neighborhood_.getNeighbors(i)) {
        if ((bestLocalBestSolution == null) || (bestLocalBestSolution.getObjective(0)
          > localBest_[index].getObjective(0))) {
          bestLocalBestSolution = localBest_[index];
        }
      }
    } catch (JMException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    }

    return bestLocalBestSolution;
  }

  private void computeSpeed() throws JMException {
    double r1, r2;

    for (int i = 0; i < swarmSize_; i++) {

      XReal particle = new XReal(swarm_.get(i));
      XReal localBest = new XReal(localBest_[i]);
      XReal neighborhoodBest = new XReal(neighborhoodBest_[i]);

      r1 = PseudoRandom.randDouble(0, C_);
      r2 = PseudoRandom.randDouble(0, C_);

      if (localBest_[i] != neighborhoodBest_[i]) {
        for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
          speed_[i][var] = W_ * speed_[i][var] +
            r1 * (localBest.getValue(var) - particle.getValue(var)) +
            r2 * (neighborhoodBest.getValue(var) - particle.getValue(var));
        }
      } else {
        for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
          speed_[i][var] = W_ * speed_[i][var] +
            r1 * (localBest.getValue(var) - particle.getValue(var));
        }
      }
    }
  }

  /**
   * Update the position of each particle
   *
   * @throws jmetal.util.JMException
   */
  private void computeNewPositions() throws JMException {
    for (int i = 0; i < swarmSize_; i++) {
      XReal particle = new XReal(swarm_.get(i));
      for (int var = 0; var < particle.size(); var++) {
        particle.setValue(var, particle.getValue(var) + speed_[i][var]);

        if (particle.getValue(var) < problem_.getLowerLimit(var)) {
          particle.setValue(var, problem_.getLowerLimit(var));
          speed_[i][var] = 0;
        }
        if (particle.getValue(var) > problem_.getUpperLimit(var)) {
          particle.setValue(var, problem_.getUpperLimit(var));
          speed_[i][var] = 0;
        }
      }
    }
  }


  /**
   * Runs of the StandardPSO2007 algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws jmetal.util.JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    initParams();

    // Step 1 Create the initial population and evaluate
    for (int i = 0; i < swarmSize_; i++) {
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      evaluations_++;
      swarm_.add(particle);
    }

    neighborhood_ = new AdaptiveRandomNeighborhood(swarm_, numberOfParticlesToInform_);

    Configuration.logger_.info("SwarmSize: " + swarmSize_);
    Configuration.logger_.info("Swarm size: " + swarm_.size());
    Configuration.logger_.info("list size: " + neighborhood_.getNeighborhood().size());

    // Step2. Initialize the speed_ of each particle
    for (int i = 0; i < swarmSize_; i++) {
      XReal particle = new XReal(swarm_.get(i));
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] =
          (PseudoRandom.randDouble(particle.getLowerBound(j), particle.getUpperBound(j))
            - particle.getValue(j)) / 2.0;
      }
    }

    //-> Step 6. Initialize the memory of each particle
    for (int i = 0; i < swarm_.size(); i++) {
      Solution particle = new Solution(swarm_.get(i));
      localBest_[i] = particle;
    }

    for (int i = 0; i < swarm_.size(); i++) {
      neighborhoodBest_[i] = getNeighborBest(i);
    }

    //Configuration.logger_.info("neighborhood_i " + neighborhood_.getNeighbors(0) );
    //for (int s :  neighborhood_.getNeighbors(0)) {
    //  Configuration.logger_.info(s + ": " + localBest_[s].getObjective(0)) ;
    //}

    //Configuration.logger_.info("localBest_i " + localBest_[0].getObjective(0) );
    //Configuration.logger_.info("neighborhoodBest_i " + getNeighborBest(0).getObjective(0) );

    //Configuration.logger_.info("Swarm: " + swarm_) ;
    swarm_.printObjectives();
    Double b = swarm_.best(comparator_).getObjective(0);
    //Configuration.logger_.info("Best: " + b) ;

    double bestFoundFitness = Double.MAX_VALUE;

    while (iteration_ < maxIterations_) {
      //Compute the speed
      computeSpeed();

      //Compute the new positions for the swarm
      computeNewPositions();

      //Evaluate the new swarm_ in new positions
      for (int i = 0; i < swarm_.size(); i++) {
        Solution particle = swarm_.get(i);
        problem_.evaluate(particle);
        evaluations_++;
      }

      //Update the memory of the particles
      for (int i = 0; i < swarm_.size(); i++) {
        if ((swarm_.get(i).getObjective(0) < localBest_[i].getObjective(0))) {
          Solution particle = new Solution(swarm_.get(i));
          localBest_[i] = particle;
        }
      }
      for (int i = 0; i < swarm_.size(); i++) {
        neighborhoodBest_[i] = getNeighborBest(i);
      }


      iteration_++;

      Double bestCurrentFitness = swarm_.best(comparator_).getObjective(0);
      Configuration.logger_.info("Best: " + bestCurrentFitness);

      if (bestCurrentFitness == bestFoundFitness) {
        Configuration.logger_.info("Recomputing");
        neighborhood_.recompute();
      }

      if (bestCurrentFitness < bestFoundFitness) {
        bestFoundFitness = bestCurrentFitness;
      }
    }

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(swarm_.get((Integer) findBestSolution_.execute(swarm_)));

    return resultPopulation;
  }
}
