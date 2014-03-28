//  StandardPSO2011.java
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
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.random.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Class implementing a Stantard PSO 2011 algorithm
 */
public class StandardPSO2011 extends Algorithm {

  private SolutionSet swarm_;
  private int swarmSize_;
  private int maxIterations_;
  private int iteration_;
  private int numberOfParticlesToInform_ ; // Referred a K in the SPSO document
  private Solution[] localBest_;
  private Solution[] neighborhoodBest_;
  private double[][] speed_;
  private AdaptiveRandomNeighborhood neighborhood_ ;

  int evaluations_ ;

  Comparator  comparator_  ;

  Operator findBestSolution_ ;

  private double W_;
  private double C_;
  private double ChVel_ ;

  public double getW() { return W_ ;}
  public double getC() { return C_ ;}

  /**
   * Constructor
   * @param problem Problem to solve
   */
  public StandardPSO2011(Problem problem) {
    super(problem) ;

    W_ = 1.0/(2.0 * Math.log(2)) ; //0.721;
    C_ = 1.0/2.0 + Math.log(2) ; //1.193;
    ChVel_ = -0.5 ;

    comparator_ = new ObjectiveComparator(0) ; // Single objective comparator
    HashMap  parameters ; // Operator parameters

    parameters = new HashMap() ;
    parameters.put("comparator", comparator_) ;
    findBestSolution_ = new BestSolutionSelection(parameters) ;

    evaluations_ = 0 ;
  } // Constructor

                /*
  public int[] getNeighbourhood(int i) {
    int[] neighbors = new int[3] ;
    neighbors[0] = (i - 1 + swarmSize_)% swarmSize_;
    neighbors[1] = i ;
    neighbors[2] = (i + 1)% swarmSize_;

    return neighbors ;
  }

  public Solution getNeighbourWithMinimumFitness(int i) {
    int[] neighborIndex = getNeighbourhood(i) ;

    SolutionSet neighbors = new SolutionSet() ;
    for (int j = 0 ; j < neighborIndex.length; j++) {
      neighbors.add(swarm_.get(neighborIndex[j])) ;
    }

    neighbors.sort(comparator_) ;

    return neighbors.get(0) ;
  }     */

  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
    maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
    numberOfParticlesToInform_ = ((Integer) getInputParameter("numberOfParticlesToInform")).intValue() ;

    System.out.println("Swarm size: " + swarmSize_) ;

    iteration_ = 0 ;

    swarm_ = new SolutionSet(swarmSize_);
    localBest_ = new Solution[swarmSize_];
    neighborhoodBest_ = new Solution[swarmSize_];

    // Create the speed_ vector
    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
  } // initParams

  private Solution getNeighborBest(int i) {
    Solution bestLocalBestSolution = null ;

    try {
      for (int index : neighborhood_.getNeighbors(i)) {
        if ((bestLocalBestSolution == null) || (bestLocalBestSolution.getObjective(0) > localBest_[index].getObjective(0))) {
          bestLocalBestSolution = localBest_[index] ;
        }
      }
    } catch (JMException e) {
      e.printStackTrace();
    }

    return bestLocalBestSolution ;
  }

  private void computeSpeed() {
    for (int i = 0; i < swarmSize_; i++) {

      XReal particle = new XReal(swarm_.get(i)) ;
      XReal localBest = new XReal(localBest_[i]) ;
      XReal neighborhoodBest = new XReal(neighborhoodBest_[i]) ;
      XReal gravityCenter = new XReal(swarm_.get(i)) ;

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        double G ;
        try {
          G = particle.getValue(var) +
              C_*(localBest.getValue(var)+neighborhoodBest.getValue(var)-2*particle.getValue(var))/3.0 ;

          gravityCenter.setValue(var, G);
        } catch (JMException e) {
          e.printStackTrace();
        }
      }
  /*

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        try {

          if (localBest_[i] != neighborhoodBest_[i]) {
            speed_[i][var] = W_* speed_[i][var] +
                    r1 * (localBest.getValue(var) - particle.getValue(var)) +
                    r2 * (neighborhoodBest.getValue(var) - particle.getValue(var)) ;
          }
          else {
            speed_[i][var] = W_* speed_[i][var] +
                    r1 * (localBest.getValue(var) - particle.getValue(var)) ;
          }
        } catch (JMException e) {
          e.printStackTrace();
        }
      }
      */
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
          speed_[i][var] = ChVel_ * speed_[i][var];
        }
        if (particle.getValue(var) > problem_.getUpperLimit(var)) {
          particle.setValue(var, problem_.getUpperLimit(var));
          speed_[i][var] = ChVel_ * speed_[i][var];
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
        speed_[i][j] = (PseudoRandom.randDouble(
                particle.getLowerBound(j) - particle.getValue(0),
                particle.getUpperBound(j) - particle.getValue(0))) ;
      }
    }

    //-> Step 6. Initialize the memory of each particle
    for (int i = 0; i < swarm_.size(); i++) {
      Solution particle = new Solution(swarm_.get(i));
      localBest_[i] = particle;
    }

    for (int i = 0; i < swarm_.size(); i++) {
      neighborhoodBest_[i] = getNeighborBest(i) ;
    }

    System.out.println("neighborhood_i " + neighborhood_.getNeighbors(0) );
    for (int s :  neighborhood_.getNeighbors(0)) {
      System.out.println(s + ": " + localBest_[s].getObjective(0)) ;
    }

    System.out.println("localBest_i " + localBest_[0].getObjective(0) );
    System.out.println("neighborhoodBest_i " + getNeighborBest(0).getObjective(0) );

    System.out.println("Swarm: " + swarm_) ;
    swarm_.printObjectives();
    Double b = swarm_.best(comparator_).getObjective(0) ;
    System.out.println("Best: " + b) ;


    double bestFoundFitness = Double.MAX_VALUE ;
    //-> Step 7. Iterations ..
    while (iteration_ < maxIterations_) {
      //Compute the speed
      computeSpeed() ;

      //Compute the new positions for the swarm
      computeNewPositions();

      //Evaluate the new swarm_ in new positions
      for (int i = 0; i < swarm_.size(); i++) {
        Solution particle = swarm_.get(i);
        problem_.evaluate(particle);
        evaluations_ ++ ;
      }

      //Update the memory of the particles
      for (int i = 0; i < swarm_.size(); i++) {
        if ((swarm_.get(i).getObjective(0) < localBest_[i].getObjective(0))) {
          Solution particle = new Solution(swarm_.get(i));
          localBest_[i] = particle;
        } // if
      }
      for (int i = 0; i < swarm_.size(); i++) {
        neighborhoodBest_[i] = getNeighborBest(i) ;
      }

      iteration_++;
      //System.out.println("Swarm( " + iteration_+ "): " + swarm_) ;
      //swarm_.printObjectives();
      Double bestCurrentFitness = swarm_.best(comparator_).getObjective(0) ;
      System.out.println("Best: " + bestCurrentFitness) ;

      if (bestCurrentFitness == bestFoundFitness) {
        System.out.println("Recomputing") ;
        neighborhood_.recompute();
      }

      if (bestCurrentFitness < bestFoundFitness) {
        bestFoundFitness = bestCurrentFitness ;
      }
    }
    
    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1) ;
    resultPopulation.add(swarm_.get((Integer)findBestSolution_.execute(swarm_))) ;
    
    return resultPopulation ;
  } // execute

  private XReal SphereRandomPoint(XReal center, XReal radius) {
    XReal result = new XReal(center) ;
    double length ;
    double random ;

    length = 0 ;
    for (int i = 0; i < center.getNumberOfDecisionVariables(); i++) {
      try {
        result.setValue(i, PseudoRandom.randNormal(0, 1));
        length += result.getValue(i)*result.getValue(i) ;
      } catch (JMException e) {
        e.printStackTrace();
      }
    }

    length = Math.sqrt(length) ;
    random = PseudoRandom.randDouble() ;
    random = Math.pow(random, 1.0/center.getNumberOfDecisionVariables()) ;

    for (int i = 0; i < center.getNumberOfDecisionVariables(); i++) {
      try {
        result.setValue(i, random*result.getValue(i)/length );
      } catch (JMException e) {
        e.printStackTrace();
      }
    }

    return result ;
  }
} // StandardPSO2011
