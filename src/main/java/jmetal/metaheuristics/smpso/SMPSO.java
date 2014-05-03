//  SMPSO.java
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

package jmetal.metaheuristics.smpso;

import jmetal.core.*;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the SMPSO algorithm described in:
 * A.J. Nebro, J.J. Durillo, J. Garcia-Nieto, C.A. Coello Coello, F. Luna and E. Alba
 * "SMPSO: A New PSO-based Metaheuristic for Multi-objective Optimization". 
 * IEEE Symposium on Computational Intelligence in Multicriteria Decision-Making 
 * (MCDM 2009), pp: 66-73. March 2009
 */
public class SMPSO extends Algorithm {

  /**
   * Stores the number of particles_ used
   */
  private int swarmSize_;
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
  private CrowdingArchive leaders_;
  /**
   * Stores the speed_ of each particle
   */
  private double[][] speed_;
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
   * Stores a operator for non uniform mutations
   */
  private Operator polynomialMutation_;

  QualityIndicator indicators_; // QualityIndicator object

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
  public SMPSO(Problem problem) {
    super(problem) ;

    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    C1Max_ = 2.5;
    C1Min_ = 1.5;
    C2Max_ = 2.5;
    C2Min_ = 1.5;
    WMax_ = 0.1;
    WMin_ = 0.1;
    ChVel1_ = -1;
    ChVel2_ = -1;
  } // Constructor

  public SMPSO(Problem problem,
    Vector<Double> variables,
    String trueParetoFront) throws FileNotFoundException {
    super(problem) ;

    r1Max_ = variables.get(0);
    r1Min_ = variables.get(1);
    r2Max_ = variables.get(2);
    r2Min_ = variables.get(3);
    C1Max_ = variables.get(4);
    C1Min_ = variables.get(5);
    C2Max_ = variables.get(6);
    C2Min_ = variables.get(7);
    WMax_ = variables.get(8);
    WMin_ = variables.get(9);
    ChVel1_ = variables.get(10);
    ChVel2_ = variables.get(11);

    hy_ = new Hypervolume();
    jmetal.qualityIndicator.util.MetricsUtil mu = new jmetal.qualityIndicator.util.MetricsUtil();
    trueFront_ = mu.readNonDominatedSolutionSet(trueParetoFront);
    trueHypervolume_ = hy_.hypervolume(trueFront_.writeObjectivesToMatrix(),
      trueFront_.writeObjectivesToMatrix(),
      problem_.getNumberOfObjectives());

  } // SMPSO
  private double trueHypervolume_;
  private Hypervolume hy_;
  private SolutionSet trueFront_;
  private double deltaMax_[];
  private double deltaMin_[];
  boolean success_;

  /** 
   * Constructor
   * @param problem Problem to solve
   */
  public SMPSO(Problem problem, String trueParetoFront) throws FileNotFoundException {
    super(problem) ;
    hy_ = new Hypervolume();
    jmetal.qualityIndicator.util.MetricsUtil mu = new jmetal.qualityIndicator.util.MetricsUtil();
    trueFront_ = mu.readNonDominatedSolutionSet(trueParetoFront);
    trueHypervolume_ = hy_.hypervolume(trueFront_.writeObjectivesToMatrix(),
      trueFront_.writeObjectivesToMatrix(),
      problem_.getNumberOfObjectives());

    // Default configuration
    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    C1Max_ = 2.5;
    C1Min_ = 1.5;
    C2Max_ = 2.5;
    C2Min_ = 1.5;
    WMax_ = 0.1;
    WMin_ = 0.1;
    ChVel1_ = -1;
    ChVel2_ = -1;
  } // Constructor

  /**
   * Initialize all parameter of the algorithm
   */
  public void initParams() {
    swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
    archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
    maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();

    indicators_ = (QualityIndicator) getInputParameter("indicators");

    polynomialMutation_ = operators_.get("mutation") ; 

    iteration_ = 0 ;

    success_ = false;

    particles_ = new SolutionSet(swarmSize_);
    best_ = new Solution[swarmSize_];
    leaders_ = new CrowdingArchive(archiveSize_, problem_.getNumberOfObjectives());

    // Create comparators for dominance and crowding distance
    dominance_ = new DominanceComparator();
    crowdingDistanceComparator_ = new CrowdingDistanceComparator();
    distance_ = new Distance();

    // Create the speed_ vector
    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];


    deltaMax_ = new double[problem_.getNumberOfVariables()];
    deltaMin_ = new double[problem_.getNumberOfVariables()];
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      deltaMax_[i] = (problem_.getUpperLimit(i) -
        problem_.getLowerLimit(i)) / 2.0;
      deltaMin_[i] = -deltaMax_[i];
    } // for
  } // initParams 

  // Adaptive inertia 
  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    return wma; // - (((wma-wmin)*(double)iter)/(double)miter);
  } // inertiaWeight

  // constriction coefficient (M. Clerc)
  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    //rho = 1.0 ;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  } // constrictionCoefficient


  // velocity bounds
  private double velocityConstriction(double v, double[] deltaMax,
                                      double[] deltaMin, int variableIndex,
                                      int particleIndex) throws IOException {


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
  } // velocityConstriction

  /**
   * Update the speed of each particle
   * @throws JMException 
   */
  private void computeSpeed(int iter, int miter) throws JMException, IOException {
    double r1, r2, W, C1, C2;
    double wmax, wmin, deltaMax, deltaMin;
    XReal bestGlobal;

    for (int i = 0; i < swarmSize_; i++) {
    	XReal particle = new XReal(particles_.get(i)) ;
    	XReal bestParticle = new XReal(best_[i]) ;

      //Select a global best_ for calculate the speed of particle i, bestGlobal
      Solution one, two;
      int pos1 = PseudoRandom.randInt(0, leaders_.size() - 1);
      int pos2 = PseudoRandom.randInt(0, leaders_.size() - 1);
      one = leaders_.get(pos1);
      two = leaders_.get(pos2);

      if (crowdingDistanceComparator_.compare(one, two) < 1) {
        bestGlobal = new XReal(one);
      } else {
        bestGlobal = new XReal(two);
      //Params for velocity equation
      }
      r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
      r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
      C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
      C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
      W = PseudoRandom.randDouble(WMin_, WMax_);
      //
      wmax = WMax_;
      wmin = WMin_;

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        //Computing the velocity of this particle 
        speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
          (inertiaWeight(iter, miter, wmax, wmin) *
          speed_[i][var] +
          C1 * r1 * (bestParticle.getValue(var) -
          particle.getValue(var)) +
          C2 * r2 * (bestGlobal.getValue(var) -
          particle.getValue(var))), deltaMax_, //[var],
          deltaMin_, //[var], 
          var,
          i);
      }
    }
  } // computeSpeed

  /**
   * Update the position of each particle
   * @throws JMException 
   */
  private void computeNewPositions() throws JMException {
    for (int i = 0; i < swarmSize_; i++) {
    	XReal particle = new XReal(particles_.get(i)) ;
      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
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
    //->Step 1 (and 3) Create the initial population and evaluate
    for (int i = 0; i < swarmSize_; i++) {
      Solution particle = new Solution(problem_);
      problem_.evaluate(particle);
      problem_.evaluateConstraints(particle);
      particles_.add(particle);
    }

    //-> Step2. Initialize the speed_ of each particle to 0
    for (int i = 0; i < swarmSize_; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] = 0.0;
      }
    }

    // Step4 and 5   
    for (int i = 0; i < particles_.size(); i++) {
      Solution particle = new Solution(particles_.get(i));
      leaders_.add(particle);
    }

    //-> Step 6. Initialize the memory of each particle
    for (int i = 0; i < particles_.size(); i++) {
      Solution particle = new Solution(particles_.get(i));
      best_[i] = particle;
    }

    //Crowding the leaders_
    distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());

    //-> Step 7. Iterations ..        
    while (iteration_ < maxIterations_) {
      try {
        //Compute the speed_
        computeSpeed(iteration_, maxIterations_);
      } catch (IOException ex) {
        Logger.getLogger(SMPSO.class.getName()).log(Level.SEVERE, null, ex);
      }

      //Compute the new positions for the particles_            
      computeNewPositions();

      //Mutate the particles_          
      mopsoMutation(iteration_, maxIterations_);

      //Evaluate the new particles_ in new positions
      for (int i = 0; i < particles_.size(); i++) {
        Solution particle = particles_.get(i);
        problem_.evaluate(particle);
        problem_.evaluateConstraints(particle) ;
      }

      //Actualize the archive          
      for (int i = 0; i < particles_.size(); i++) {
        Solution particle = new Solution(particles_.get(i));
        leaders_.add(particle);
      }

      //Actualize the memory of this particle
      for (int i = 0; i < particles_.size(); i++) {
        int flag = dominance_.compare(particles_.get(i), best_[i]);
        if (flag != 1) { // the new particle is best_ than the older remeber        
          Solution particle = new Solution(particles_.get(i));
          best_[i] = particle;
        }
      }

      //Assign crowding distance to the leaders_
      distance_.crowdingDistanceAssignment(leaders_,
        problem_.getNumberOfObjectives());
      iteration_++;
   /*   
      if ((iteration_ % 1) == 0) {
        leaders_.printObjectivesOfValidSolutionsToFile("FUNV"+iteration_) ;
        leaders_.printObjectivesToFile("FUN"+iteration_) ;
        leaders_.printVariablesToFile("VAR"+iteration_) ;
      }
*/
    }
    //leaders_.printObjectivesOfValidSolutionsToFile("FUNV.SMPSO") ;
    leaders_.printFeasibleFUN("FUN_SMPSO") ;

    return this.leaders_;
  } // execute

  /** 
   * Gets the leaders of the SMPSO algorithm
   */
  public SolutionSet getLeader() {
    return leaders_;
  }  // getLeader   
} // SMPSO
