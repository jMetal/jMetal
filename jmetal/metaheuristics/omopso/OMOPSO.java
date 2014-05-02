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

package jmetal.metaheuristics.omopso;

import jmetal.core.*;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.NonDominatedSolutionList;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.EpsilonDominanceComparator;

import java.util.Comparator;

/**
 * This class representing an asynchronous version of OMOPSO algorithm
 */
public class OMOPSO extends Algorithm {
                 
  /**
   * Stores the number of particles_ used
   */
  private int particlesSize_;
  
  /**
  * Stores the maximum size for the archive
  */
  private int archiveSize_;
  
  /**
  * Stores the maximum number of iteration_
  */
  private int maxIterations_;
  
  /**
  * Stores the current number of iteration_
  */
  private int iteration_;
  
  /**
  * Stores the perturbation used by the non-uniform mutation
  */
  private double perturbation_;
  
  /**
  * Stores the particles
  */
  private SolutionSet particles_;
  
  /**
   * Stores the best_ solutions founds so far for each particles
   */
  private Solution[] best_;
  
  /**
  * Stores the leaders_
  */
  private CrowdingArchive leaders_ ;
  
  /**
  * Stores the epsilon-archive
  */
  private NonDominatedSolutionList eArchive_;
  
  /**
  * Stores the speed_ of each particle
  */
  private double [][] speed_;  
  
  /**
  * Stores a comparator for checking dominance
  */
  private Comparator dominance_;
  
  /**
  * Stores a comparator for crowding checking
  */
  private Comparator crowdingDistanceComparator_;
  
  /**
   * Stores a <code>Distance</code> object
   */
  private Distance distance_;
  
  /**
  * Stores a operator for uniform mutations
  */
  private Operator uniformMutation_;
  
  /**
  * Stores a operator for non uniform mutations
  */ 
  private Operator nonUniformMutation_;
  
  /**
  * eta_ value
  */
  private double eta_ = 0.0075;

  /** 
  * Constructor
  * @param problem Problem to solve
  */    
  public OMOPSO(Problem problem) {                
    super (problem) ;
  } // OMOPSO
  
  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams(){
    particlesSize_ = ((Integer)getInputParameter("swarmSize")).intValue();
    archiveSize_   = ((Integer)getInputParameter("archiveSize")).intValue();
    maxIterations_ = ((Integer)getInputParameter("maxIterations")).intValue();

    
    particles_     = new SolutionSet(particlesSize_);        
    best_          = new Solution[particlesSize_];
    leaders_       = new CrowdingArchive(archiveSize_,problem_.getNumberOfObjectives());
    eArchive_      = new NonDominatedSolutionList(new EpsilonDominanceComparator(eta_));
    
    uniformMutation_ = (Mutation)operators_.get("uniformMutation") ;
    nonUniformMutation_ = (Mutation)operators_.get("nonUniformMutation") ;
    
    // Create the dominator for equadless and dominance
    dominance_          = new DominanceComparator();    
    crowdingDistanceComparator_ = new CrowdingDistanceComparator();
    distance_           = new Distance();
    
    // Create the speed_ vector
    speed_ = new double[particlesSize_][problem_.getNumberOfVariables()];
  } // initParams
           
  
  /**
   * Update the spped of each particle
   * @throws JMException 
   */
  private void computeSpeed() throws JMException{        
    double r1,r2,W,C1,C2; 
    Variable[] bestGlobal;                                            
        
    for (int i = 0; i < particlesSize_; i++){
      Variable[] particle     = particles_.get(i).getDecisionVariables();
      Variable[] bestParticle = best_[i].getDecisionVariables();                        

      //Select a global best_ for calculate the speed of particle i, bestGlobal
      Solution one, two;
      int pos1 = PseudoRandom.randInt(0,leaders_.size()-1);
      int pos2 = PseudoRandom.randInt(0,leaders_.size()-1);
      one = leaders_.get(pos1);
      two = leaders_.get(pos2);

      if (crowdingDistanceComparator_.compare(one,two) < 1)
        bestGlobal = one.getDecisionVariables();
      else
        bestGlobal = two.getDecisionVariables();
      //
            
      //Params for velocity equation
      r1 = PseudoRandom.randDouble();
      r2 = PseudoRandom.randDouble();
      C1 = PseudoRandom.randDouble(1.5,2.0);
      C2 = PseudoRandom.randDouble(1.5,2.0);
      W  = PseudoRandom.randDouble(0.1,0.5);            
      //

      for (int var = 0; var < particle.length; var++){                                     
        //Computing the velocity of this particle
        speed_[i][var] = W  * speed_[i][var] +
                   C1 * r1 * (bestParticle[var].getValue() - 
                              particle[var].getValue()) +
                   C2 * r2 * (bestGlobal[var].getValue() - 
                              particle[var].getValue());
      }
                
    }
  } // computeSpeed
     
  /**
   * Update the position of each particle
   * @throws JMException 
   */
  private void computeNewPositions() throws JMException{
    for (int i = 0; i < particlesSize_; i++){
    	Variable[] particle = particles_.get(i).getDecisionVariables();
      //particle.move(speed_[i]);
      for (int var = 0; var < particle.length; var++){
        particle[var].setValue(particle[var].getValue()+ speed_[i][var]);
        if (particle[var].getValue() < problem_.getLowerLimit(var)){
          particle[var].setValue(problem_.getLowerLimit(var));                    
          speed_[i][var] = speed_[i][var] * -1.0;    
        }
        if (particle[var].getValue() > problem_.getUpperLimit(var)){
          particle[var].setValue(problem_.getUpperLimit(var));                    
          speed_[i][var] = speed_[i][var] * -1.0;    
        }                                             
      }
    }
  } // computeNewPositions
        
   
  /**
   * Apply a mutation operator to all particles in the swarm
   * @throws JMException 
   */
  private void mopsoMutation(int actualIteration, int totalIterations) throws JMException{       
    //There are three groups of particles_, the ones that are mutated with
    //a non-uniform mutation operator, the ones that are mutated with a 
    //uniform mutation and the one that no are mutated
    nonUniformMutation_.setParameter("currentIteration",actualIteration);
    //*/

    for (int i = 0; i < particles_.size();i++)            
      if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation
        nonUniformMutation_.execute(particles_.get(i));                                
      } else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
        uniformMutation_.execute(particles_.get(i));                
      } else //particles_ without mutation
          ;      
  } // mopsoMutation
   
    
  /**   
  * Runs of the OMOPSO algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution  
   * @throws JMException 
  */  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    initParams();

    //->Step 1 (and 3) Create the initial population and evaluate
    for (int i = 0; i < particlesSize_; i++){
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      problem_.evaluateConstraints(particle);
      particles_.add(particle);                   
    }
        
    //-> Step2. Initialize the speed_ of each particle to 0
    for (int i = 0; i < particlesSize_; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] = 0.0;
      }
    }
    
        
    // Step4 and 5   
    for (int i = 0; i < particles_.size(); i++){
      Solution particle = new Solution(particles_.get(i));            
      if (leaders_.add(particle)){
        eArchive_.add(new Solution(particle));
      }
    }
                
    //-> Step 6. Initialice the memory of each particle
    for (int i = 0; i < particles_.size(); i++){
      Solution particle = new Solution(particles_.get(i));           
      best_[i] = particle;
    }
        
    //Crowding the leaders_
    distance_.crowdingDistanceAssignment(leaders_,problem_.getNumberOfObjectives());        

    //-> Step 7. Iterations ..        
    while (iteration_ < maxIterations_){
      //Compute the speed_        
      computeSpeed();
            
      //Compute the new positions for the particles_            
      computeNewPositions();

      //Mutate the particles_          
      mopsoMutation(iteration_,maxIterations_);                       
            
      //Evaluate the new particles_ in new positions
      for (int i = 0; i < particles_.size(); i++){
        Solution particle = particles_.get(i);
        problem_.evaluate(particle);                
        problem_.evaluateConstraints(particle);                
      }
            
      //Actualize the archive          
      for (int i = 0; i < particles_.size(); i++){
        Solution particle = new Solution(particles_.get(i));                
        if (leaders_.add(particle)){
          eArchive_.add(new Solution(particle));
        }                
      }
            
      //Actualize the memory of this particle
      for (int i = 0; i < particles_.size();i++){
        int flag = dominance_.compare(particles_.get(i),best_[i]);
        if (flag != 1) { // the new particle is best_ than the older remeber        
          Solution particle = new Solution(particles_.get(i));                    
          //this.best_.reemplace(i,particle);
          best_[i] = particle;
        }
      }       
            
      //Crowding the leaders_
      distance_.crowdingDistanceAssignment(leaders_,
                                              problem_.getNumberOfObjectives());            
      iteration_++;
    }
        
    return this.leaders_;
    //return eArchive_;
  } // execute
    
  /** 
   * Gets the leaders of the OMOPSO algorithm
   */
  public SolutionSet getLeader(){
    return leaders_;
  }  // getLeader 
} // OMOPSO