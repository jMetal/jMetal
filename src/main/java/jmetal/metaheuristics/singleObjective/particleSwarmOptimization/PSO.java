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

package jmetal.metaheuristics.singleObjective.particleSwarmOptimization;

import jmetal.core.*;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.wrapper.XReal;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing a single-objective PSO algorithm
 */
public class PSO extends Algorithm {

	/**
   * Stores the number of particles_ used
   */
  private int particlesSize_;
  /**
   * Stores the maximum number of iteration_
   */
  private int maxIterations_;
  /**
   * Stores the current number of iteration_
   */
  private int iteration_;
  /**
   * Stores the particles
   */
  private SolutionSet particles_;
  /**
   * Stores the local best solutions found so far for each particles
   */
  private Solution[] localBest_;
  /**
   * Stores the global best solution found
   */
  private Solution globalBest_;
  /**
   * Stores the speed_ of each particle
   */
  private double[][] speed_;
  /**
   * Stores a operator for non uniform mutations
   */
  private Operator polynomialMutation_;

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
  double C1Max_;
  double C1Min_;
  double C2Max_;
  double C2Min_;
  double WMax_;
  double WMin_;
  double ChVel1_;
  double ChVel2_;

  /** 
   * Constructor
   * @param problem Problem to solve
   */
  public PSO(Problem problem) {
    super(problem) ;

    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    C1Max_ = 1.5;
    C1Min_ = 1.5;
    C2Max_ = 1.5;
    C2Min_ = 1.5;
    WMax_ = 0.9;
    WMin_ = 0.1;
    ChVel1_ = 1.0;
    ChVel2_ = 1.0;
    
    comparator_ = new ObjectiveComparator(0) ; // Single objective comparator
    HashMap  parameters ; // Operator parameters

    parameters = new HashMap() ;
    parameters.put("comparator", comparator_) ;
    findBestSolution_ = new BestSolutionSelection(parameters) ;

    evaluations_ = 0 ;
  } // Constructor

  private SolutionSet trueFront_;
  private double deltaMax_[];
  private double deltaMin_[];
  boolean success_;


  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    particlesSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
    maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();

    polynomialMutation_ = operators_.get("mutation") ; 

    iteration_ = 0 ;

    success_ = false;

    particles_ = new SolutionSet(particlesSize_);
    localBest_ = new Solution[particlesSize_];

    // Create the speed_ vector
    speed_ = new double[particlesSize_][problem_.getNumberOfVariables()];


    deltaMax_ = new double[problem_.getNumberOfVariables()];
    deltaMin_ = new double[problem_.getNumberOfVariables()];
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      deltaMax_[i] = (problem_.getUpperLimit(i) -
        problem_.getLowerLimit(i)) / 2.0;
      deltaMin_[i] = -deltaMax_[i];
    } // for
  } // initParams 

  // Adaptive inertia 
  private double inertiaWeight(int iter, int miter, double wmax, double wmin) {
    //return wmax; // - (((wmax-wmin)*(double)iter)/(double)miter);
    return wmax - (((wmax-wmin)*(double)iter)/(double)miter);
  } // inertiaWeight

  // constriction coefficient (M. Clerc)
  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    //rho = 1.0 ;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / Math.abs((2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho)));
    }
  } // constrictionCoefficient


  // velocity bounds
  private double velocityConstriction(double v, double[] deltaMax,
                                      double[] deltaMin, int variableIndex,
                                      int particleIndex) throws IOException {

  	return v; 
  	/*

    //System.out.println("v: " + v + "\tdmax: " + dmax + "\tdmin: " + dmin) ;
    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
    */
  } // velocityConstriction

  /**
   * Update the speed of each particle
   * @throws JMException 
   */
  private void computeSpeed(int iter, int miter) throws JMException, IOException {
    double r1, r2 ;
    //double W ;
    double C1, C2;
    double wmax, wmin, deltaMax, deltaMin;
    XReal bestGlobal;

  	bestGlobal = new XReal(globalBest_) ;

    for (int i = 0; i < particlesSize_; i++) {
    	XReal particle = new XReal(particles_.get(i)) ;
    	XReal bestParticle = new XReal(localBest_[i]) ;

      //int bestIndividual = (Integer)findBestSolution_.execute(particles_) ;

      C1Max_ = 2.5;
      C1Min_ = 1.5;
      C2Max_ = 2.5;
      C2Min_ = 1.5;

      r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
      r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
      C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
      C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
      //W =  PseudoRandom.randDouble(WMin_, WMax_);
      //

      WMax_ = 0.9;
      WMin_ = 0.9;
      ChVel1_ = 1.0;
      ChVel2_ = 1.0;
      
      C1 = 2.5 ;
      C2 = 1.5 ;
      
      wmax = WMax_;
      wmin = WMin_;
/*
      for (int var = 0; var < particle.size(); var++) {
        //Computing the velocity of this particle 
        speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
          (inertiaWeight(iter, miter, wmax, wmin) *
          speed_[i][var] +
          C1 * r1 * (bestParticle.getValue(var) - particle.getValue(var)) +
          C2 * r2 * (bestGlobal.getValue(var) -
          particle.getValue(var))), deltaMax_,
          deltaMin_, //[var], 
          var,
          i);
      }
*/
      C1 = 1.5 ;
      C2 = 1.5 ;
      double W = 0.9 ;
      for (int var = 0; var < particle.size(); var++) {
        //Computing the velocity of this particle 
        speed_[i][var] = inertiaWeight(iter, miter, wmax, wmin) * speed_[i][var] +
          C1 * r1 * (bestParticle.getValue(var) - particle.getValue(var)) +
          C2 * r2 * (bestGlobal.getValue(var) - particle.getValue(var)) ;
      }
    }
  } // computeSpeed

  /**
   * Update the position of each particle
   * @throws JMException 
   */
  private void computeNewPositions() throws JMException {
    for (int i = 0; i < particlesSize_; i++) {
    	//Variable[] particle = particles_.get(i).getDecisionVariables();
    	XReal particle = new XReal(particles_.get(i)) ;
      //particle.move(speed_[i]);
      for (int var = 0; var < particle.size(); var++) {
      	particle.setValue(var, particle.getValue(var) +  speed_[i][var]) ;
      	
        if (particle.getValue(var) < problem_.getLowerLimit(var)) {
          particle.setValue(var, problem_.getLowerLimit(var));
          speed_[i][var] = speed_[i][var] * ChVel1_; //    
        }
        if (particle.getValue(var) > problem_.getUpperLimit(var)) {
          particle.setValue(var, problem_.getUpperLimit(var));
          speed_[i][var] = speed_[i][var] * ChVel2_; //   
        }
        
      }
    }
  } // computeNewPositions

  /**
   * Apply a mutation operator to some particles in the swarm
   * @throws JMException 
   */
  private void mopsoMutation(int actualIteration, int totalIterations) throws JMException {
    for (int i = 0; i < particles_.size(); i++) {
      if ( (i % 6) == 0)
        polynomialMutation_.execute(particles_.get(i)) ;
      //if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation %3
      //  nonUniformMutation_.execute(particles_.get(i));
      //} else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
      //  uniformMutation_.execute(particles_.get(i));
      //} else //particles_ without mutation
      //;
    }
  } // mopsoMutation

  /**   
   * Runs of the SMPSO algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution  
   * @throws JMException 
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    initParams();

    success_ = false;
    globalBest_ =  null ;
    //->Step 1 (and 3) Create the initial population and evaluate
    for (int i = 0; i < particlesSize_; i++) {
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      evaluations_ ++ ;
      particles_.add(particle);
      if ((globalBest_ == null) || (particle.getObjective(0) < globalBest_.getObjective(0)))
        globalBest_ = new Solution(particle) ;
    }

    //-> Step2. Initialize the speed_ of each particle to 0
    for (int i = 0; i < particlesSize_; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] = 0.0;
      }
    }

    //-> Step 6. Initialize the memory of each particle
    for (int i = 0; i < particles_.size(); i++) {
      Solution particle = new Solution(particles_.get(i));
      localBest_[i] = particle;
    }

    //-> Step 7. Iterations ..        
    while (iteration_ < maxIterations_) {
      int bestIndividual = (Integer)findBestSolution_.execute(particles_) ;
      try {
        //Compute the speed_
        computeSpeed(iteration_, maxIterations_);
      } catch (IOException ex) {
        Logger.getLogger(PSO.class.getName()).log(Level.SEVERE, null, ex);
      }

      //Compute the new positions for the particles_            
      computeNewPositions();

      //Mutate the particles_          
      //mopsoMutation(iteration_, maxIterations_);

      //Evaluate the new particles_ in new positions
      for (int i = 0; i < particles_.size(); i++) {
        Solution particle = particles_.get(i);
        problem_.evaluate(particle);
        evaluations_ ++ ;
      }

      //Actualize the memory of this particle
      for (int i = 0; i < particles_.size(); i++) {
        //int flag = comparator_.compare(particles_.get(i), localBest_[i]);
        //if (flag < 0) { // the new particle is best_ than the older remember        
      	if ((particles_.get(i).getObjective(0) < localBest_[i].getObjective(0))) {
          Solution particle = new Solution(particles_.get(i));
          localBest_[i] = particle;
        } // if
      	if ((particles_.get(i).getObjective(0) < globalBest_.getObjective(0))) {
          Solution particle = new Solution(particles_.get(i));
          globalBest_ = particle;
        } // if
      	
      }
      iteration_++;
    }
    
    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1) ;
    resultPopulation.add(particles_.get((Integer)findBestSolution_.execute(particles_))) ;
    
    return resultPopulation ;
  } // execute
} // PSO
