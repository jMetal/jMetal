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
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.wrapper.XReal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class implementing a Stantard PSO 2011 algorithm
 */
public class StandardPSO2007 extends Algorithm {

  private SolutionSet swarm_;
  private int swarmSize_;
  private int maxIterations_;
  private int iteration_;
  private int numberOfParticlesToInform_ ; // Referred a K in SPSO document
  private Solution[] localBest_;
  private Solution[] neighborhoodBest_;
  private double[][] speed_;
  private AdaptiveRandomNeighborhood neighborhood_ ;

  int evaluations_ ;

  /**
   * Comparator object
   */
  Comparator  comparator_  ;

  Operator findBestSolution_ ;

  double r1Max_;
  double r1Min_;
  double r2Max_;
  double r2Min_;
  double W_;
  double C_;
  double ChVel_;

  /**
   * Constructor
   * @param problem Problem to solve
   */
  public StandardPSO2007(Problem problem) {
    super(problem) ;

    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    C_ = 1.193;
    W_ = 0.721;

    ChVel_ = -0.5;

    comparator_ = new ObjectiveComparator(0) ; // Single objective comparator
    HashMap  parameters ; // Operator parameters

    parameters = new HashMap() ;
    parameters.put("comparator", comparator_) ;
    findBestSolution_ = new BestSolutionSelection(parameters) ;

    evaluations_ = 0 ;
  } // Constructor

  boolean success_;


//  public int[] getNeighbourhood(int i) {
//    int[] neighbors = new int[3] ;
//    neighbors[0] = (i - 1 + swarmSize_)% swarmSize_;
//    neighbors[1] = i ;
//    neighbors[2] = (i + 1)% swarmSize_;
//
//    return neighbors ;
//  }

//  public Solution getNeighbourWithMinimumFitness(int i) {
//    int[] neighborIndex = getNeighbourhood(i) ;
//
//    SolutionSet neighbors = new SolutionSet() ;
//    for (int j = 0 ; j < neighborIndex.length; j++) {
//      neighbors.add(swarm_.get(neighborIndex[j])) ;
//    }
//
//    neighbors.sort(comparator_) ;
//
//    return neighbors.get(0) ;
//  }

  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    swarmSize_ = 10 + 2 * (int)Math.sqrt(problem_.getNumberOfObjectives()) ;
    maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
    numberOfParticlesToInform_ = ((Integer) getInputParameter("numberOfParticlesToInform")).intValue() ;

    iteration_ = 0 ;

    success_ = false;

    swarm_ = new SolutionSet(swarmSize_);
    localBest_ = new Solution[swarmSize_];
    neighborhoodBest_ = new Solution[swarmSize_];

    // Create the speed_ vector
    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
  } // initParams

  private Solution getNeighbourWithMinimumFitness(int i) {
    Solution bestSolution = null ;

    ArrayList<Integer> neighbors = null;
    try {
      neighbors = neighborhood_.getNeighbors(i);
    } catch (JMException e) {
      e.printStackTrace();
    }

    for (Integer index : neighbors) {
      if ((bestSolution == null) || (bestSolution.getObjective(0) > swarm_.get(index).getObjective(0))) {
        bestSolution = swarm_.get(i) ;
      }
    }

    return bestSolution ;
  }

  private void computeSpeed() {
    double r1, r2 ;

    for (int i = 0; i < swarmSize_; i++) {

      XReal particle = new XReal(swarm_.get(i)) ;
      XReal localBest = new XReal(localBest_[i]) ;
      XReal neighborhoodBest = new XReal(neighborhoodBest_[i]) ;

      r1 = PseudoRandom.randDouble(0, C_);
      r2 = PseudoRandom.randDouble(0, C_);

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        //Computing the velocity of this particle
        try {
          speed_[i][var] = W_* speed_[i][var] +
                  r1 * (localBest.getValue(var) - particle.getValue(var)) +
                  r2 * (neighborhoodBest.getValue(var) - particle.getValue(var)) ;
        } catch (JMException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Update the position of each particle
   * @throws jmetal.util.JMException
   */
  private void computeNewPositions() throws JMException {
    for (int i = 0; i < swarmSize_; i++) {
    	//Variable[] particle = swarm_.get(i).getDecisionVariables();
    	XReal particle = new XReal(swarm_.get(i)) ;
      //particle.move(speed_[i]);
      for (int var = 0; var < particle.size(); var++) {
      	particle.setValue(var, particle.getValue(var) +  speed_[i][var]) ;

        if (particle.getValue(var) < problem_.getLowerLimit(var)) {
          particle.setValue(var, problem_.getLowerLimit(var));
          speed_[i][var] = speed_[i][var] * ChVel_; //
        }
        if (particle.getValue(var) > problem_.getUpperLimit(var)) {
          particle.setValue(var, problem_.getUpperLimit(var));
          speed_[i][var] = speed_[i][var] * ChVel_; //
        }

      }
    }
  } // computeNewPositions


  /**
   * Runs of the SMPSO algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws jmetal.util.JMException
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    initParams();

    success_ = false;

    // Step 1 Create the initial population and evaluate
    for (int i = 0; i < swarmSize_; i++) {
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      evaluations_ ++ ;
      swarm_.add(particle);
    }

    neighborhood_ = new AdaptiveRandomNeighborhood(swarm_, numberOfParticlesToInform_) ;

    System.out.println("SwarmSize: " + swarmSize_) ;
    System.out.println("Swarm size: " + swarm_.size()) ;
    System.out.println("list size: " + neighborhood_.getNeighborhood().size()) ;

    //-> Step2. Initialize the speed_ of each particle
    for (int i = 0; i < swarmSize_; i++) {
      XReal particle = new XReal(swarm_.get(i))  ;
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] = (PseudoRandom.randDouble(particle.getLowerBound(j),particle.getUpperBound(j))-particle.getValue(j))/2.0 ;
      }
    }

    //-> Step 6. Initialize the memory of each particle
    for (int i = 0; i < swarm_.size(); i++) {
      Solution particle = new Solution(swarm_.get(i));
      localBest_[i] = particle;
      neighborhoodBest_[i] = getNeighbourWithMinimumFitness(i) ;
    }

    double bestFoundFitness = Double.MAX_VALUE ;
    //-> Step 7. Iterations ..        
    while (iteration_ < maxIterations_) {
        //Compute the speed_
      computeSpeed() ;

      //Compute the new positions for the swarm_
      computeNewPositions();

      //Evaluate the new swarm_ in new positions
      for (int i = 0; i < swarm_.size(); i++) {
        Solution particle = swarm_.get(i);
        problem_.evaluate(particle);
        evaluations_ ++ ;
      }

      //Actualize the memory of this particle
      for (int i = 0; i < swarm_.size(); i++) {
        //int flag = comparator_.compare(swarm_.get(i), localBest_[i]);
        //if (flag < 0) { // the new particle is best_ than the older remember        
      	if ((swarm_.get(i).getObjective(0) < localBest_[i].getObjective(0))) {
          Solution particle = new Solution(swarm_.get(i));
          localBest_[i] = particle;
        } // if
      	if ((swarm_.get(i).getObjective(0) < neighborhoodBest_[i].getObjective(0))) {
          Solution particle = new Solution(swarm_.get(i));
          neighborhoodBest_[i] = particle;
        } // if
      	
      }
      iteration_++;
      Double bestCurrentFitness = swarm_.best(comparator_).getObjective(0) ;
      System.out.println("Best: " + bestCurrentFitness) ;
      if (bestCurrentFitness == bestFoundFitness) {
        neighborhood_.recompute();
      }
    }
    
    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1) ;
    resultPopulation.add(swarm_.get((Integer)findBestSolution_.execute(swarm_))) ;
    
    return resultPopulation ;
  } // execute


} // StandardPSO2007
