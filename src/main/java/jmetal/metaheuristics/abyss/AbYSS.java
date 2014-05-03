//  AbYSS.java
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

package jmetal.metaheuristics.abyss;

import jmetal.core.*;
import jmetal.operators.localSearch.LocalSearch;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Spea2Fitness;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.EqualSolutions;
import jmetal.util.comparators.FitnessComparator;
import jmetal.util.wrapper.XReal;

import java.util.Comparator;

/**
 * This class implements the AbYSS algorithm. This algorithm is an adaptation
 * of the single-objective scatter search template defined by F. Glover in:
 * F. Glover. "A template for scatter search and path relinking", Lecture Notes 
 * in Computer Science, Springer Verlag, 1997. AbYSS is described in: 
 *   A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham 
 *   "AbYSS: Adapting Scatter Search to Multiobjective Optimization." 
 *   IEEE Transactions on Evolutionary Computation. Vol. 12, 
 *   No. 4 (August 2008), pp. 439-457
 */
public class AbYSS extends Algorithm {
     
  /**
   * Stores the number of subranges in which each encodings.variable is divided. Used in
   * the diversification method. By default it takes the value 4 (see the method
   * <code>initParams</code>).
   */
  int numberOfSubranges_ ;
  
  /**
   * These variables are used in the diversification method.
   */
  int []  sumOfFrequencyValues_        ; 
  int []  sumOfReverseFrequencyValues_ ;
  int [][] frequency_                  ; 
  int [][] reverseFrequency_           ; 
  
  /**
   * Stores the initial solution set
   */
  private SolutionSet solutionSet_;
  
  /**
   * Stores the external solution archive
   */
  private CrowdingArchive archive_ ;
  
  /**
   * Stores the reference set one
   */
  private SolutionSet refSet1_ ;
  
  /**
   * Stores the reference set two
   */
  private SolutionSet refSet2_ ;
  
  /**
   * Stores the solutions provided by the subset generation method of the
   * scatter search template
   */
  private SolutionSet subSet_ ;    
  
  /**
   * Maximum number of solution allowed for the initial solution set
   */
  private int solutionSetSize_;
  
  /**
   * Maximum size of the external archive
   */
  private int archiveSize_;
  
  /** 
   * Maximum size of the reference set one
   */
  private int refSet1Size_;
  
  /**
   * Maximum size of the reference set two
   */
  private int refSet2Size_;  
  
  /**
   * Maximum number of getEvaluations to carry out
   */
  private int maxEvaluations;  
  
  /**
   * Stores the current number of performed getEvaluations
   */
  private int evaluations_ ;
  
  /**
   * Stores the comparators for dominance and equality, respectively
   */
  private Comparator dominance_ ;
  private Comparator equal_     ;
  private Comparator fitness_   ;
  private Comparator crowdingDistance_;
  
  /**
   * Stores the crossover operator
   */
  private Operator crossoverOperator_;
  
  /**
   * Stores the improvement operator
   */
  private LocalSearch improvementOperator_;
  
  /**
   * Stores a <code>Distance</code> object
   */
  private Distance distance_;
    
  /**
   * Constructor.
   * @param problem Problem to solve
   */
  public AbYSS(Problem problem){
    super (problem) ;
  	//Initialize the fields 
       
    solutionSet_ = null ;
    archive_     = null ;
    refSet1_     = null ;
    refSet2_     = null ;
    subSet_      = null ;    
  } // AbYSS
    
  /**
   * Reads the parameter from the parameter list using the
   * <code>getInputParameter</code> method.
   */
  public void initParam(){
    //Read the parameters
    solutionSetSize_= ((Integer)getInputParameter("populationSize")).intValue();
    refSet1Size_    = ((Integer)getInputParameter("refSet1Size")).intValue();
    refSet2Size_    = ((Integer)getInputParameter("refSet2Size")).intValue();
    archiveSize_    = ((Integer)getInputParameter("archiveSize")).intValue();
    maxEvaluations  = ((Integer)getInputParameter("maxEvaluations")).intValue();
        
    //Initialize the variables
    solutionSet_ = new SolutionSet(solutionSetSize_);     
    archive_     = new CrowdingArchive(archiveSize_,problem_.getNumberOfObjectives());        
    refSet1_     = new SolutionSet(refSet1Size_);        
    refSet2_     = new SolutionSet(refSet2Size_);        
    subSet_      = new SolutionSet(solutionSetSize_*1000);
    evaluations_       = 0 ;
    
    numberOfSubranges_ = 4 ; 

    dominance_ = new DominanceComparator();
    equal_     = new EqualSolutions();     
    fitness_   = new FitnessComparator();
    crowdingDistance_ = new CrowdingDistanceComparator();
    distance_  = new Distance();
    sumOfFrequencyValues_        = new int[problem_.getNumberOfVariables()] ;
    sumOfReverseFrequencyValues_ = new int[problem_.getNumberOfVariables()] ;
    frequency_        = new int[numberOfSubranges_][problem_.getNumberOfVariables()] ;
    reverseFrequency_ = new int[numberOfSubranges_][problem_.getNumberOfVariables()] ;    
    
    //Read the operators of crossover and improvement
    crossoverOperator_   =  operators_.get("crossover");
    improvementOperator_ = (LocalSearch) operators_.get("improvement");
    improvementOperator_.setParameter("archive",archive_);        
  } // initParam
            
  /**
   * Returns a <code>Solution</code> using the diversification generation method
   * described in the scatter search template.
   * @throws JMException 
   * @throws ClassNotFoundException 
   */
  public Solution diversificationGeneration() throws JMException, ClassNotFoundException{
    Solution solution ;
    solution = new Solution(problem_) ;
    XReal wrapperSolution = new XReal(solution)  ;
    
    double value ;
    int    range ;

    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      sumOfReverseFrequencyValues_[i] = 0 ;
      for (int j = 0; j < numberOfSubranges_; j++) {
        reverseFrequency_[j][i] = sumOfFrequencyValues_[i] - frequency_[j][i] ;
        sumOfReverseFrequencyValues_[i] += reverseFrequency_[j][i] ;
      } // for

      if (sumOfReverseFrequencyValues_[i] == 0) {
        range = PseudoRandom.randInt(0, numberOfSubranges_ - 1) ;
      } else { 
        value = PseudoRandom.randInt(0, sumOfReverseFrequencyValues_[i] - 1) ;
        range = 0 ;                
        while (value > reverseFrequency_[range][i]) {
          value -= reverseFrequency_[range][i] ;
          range++ ;
        } // while
      } // else            
            
      frequency_[range][i]     ++ ;
      sumOfFrequencyValues_[i] ++ ;

      double low = problem_.getLowerLimit(i) + range*(problem_.getUpperLimit(i) - 
                   problem_.getLowerLimit(i)) / numberOfSubranges_ ;
      double high = low + (problem_.getUpperLimit(i) - 
                   problem_.getLowerLimit(i)) / numberOfSubranges_ ;
      value = PseudoRandom.randDouble(low, high) ;
      //solution.getDecisionVariables()[i].setValue(value);
      wrapperSolution.setValue(i, value);
    } // for       
    return solution ;
  } // diversificationGeneration
                
    
  /** 
   * Implements the referenceSetUpdate method.
   * @param build if true, indicates that the reference has to be build for the
   *        first time; if false, indicates that the reference set has to be
   *        updated with new solutions
   * @throws JMException 
   */
  public void referenceSetUpdate(boolean build) throws JMException{
    if (build) { // Build a new reference set
      // STEP 1. Select the p best individuals of P, where p is refSet1Size_. 
      //         Selection Criterium: Spea2Fitness
      Solution individual;            
      (new Spea2Fitness(solutionSet_)).fitnessAssign();
      solutionSet_.sort(fitness_);
             
      // STEP 2. Build the RefSet1 with these p individuals            
      for (int i = 0; i  < refSet1Size_; i++) {
        individual = solutionSet_.get(0);
        solutionSet_.remove(0);
        individual.unMarked();
        refSet1_.add(individual);                 
      }
                                        
      // STEP 3. Compute Euclidean distances in SolutionSet to obtain q 
      //         individuals, where q is refSet2Size_
      for (int i = 0; i < solutionSet_.size();i++){
        individual = solutionSet_.get(i);                
        individual.setDistanceToSolutionSet(distance_.distanceToSolutionSetInSolutionSpace(individual,refSet1_));                                
      }
            
      int size = refSet2Size_;
      if (solutionSet_.size() < refSet2Size_) {
        size = solutionSet_.size();
      }
                       
      // STEP 4. Build the RefSet2 with these q individuals
      for (int i = 0; i < size; i++){
        // Find the maximumMinimunDistanceToPopulation
        double maxMinimum = 0.0;
        int index = 0;
        for (int j = 0; j < solutionSet_.size(); j++) {
          if (solutionSet_.get(j).getDistanceToSolutionSet() > maxMinimum){
            maxMinimum = solutionSet_.get(j).getDistanceToSolutionSet();
            index = j;
          }
        }
        individual = solutionSet_.get(index);
        solutionSet_.remove(index);

        // Update distances to REFSET in population
        for (int j = 0; j < solutionSet_.size(); j++){
          double aux = distance_.distanceBetweenSolutions(solutionSet_.get(j),individual);
          if (aux < individual.getDistanceToSolutionSet()){
            solutionSet_.get(j).setDistanceToSolutionSet(aux);
          }
        }

        // Insert the individual into REFSET2
        refSet2_.add(individual);

        // Update distances in REFSET2
        for (int j = 0; j < refSet2_.size();j++){
          for (int k = 0; k < refSet2_.size();k++){
            if (i != j){
              double aux = distance_.distanceBetweenSolutions(refSet2_.get(j),refSet2_.get(k));
              if (aux < refSet2_.get(j).getDistanceToSolutionSet()){
                refSet2_.get(j).setDistanceToSolutionSet(aux);
              } // if
            } // if
          } // for
        } // for   
      } // for                       
                        
    } else { // Update the reference set from the subset generation result
      Solution individual;
      for (int i = 0; i < subSet_.size();i++){
        individual = (Solution)improvementOperator_.execute(subSet_.get(i));
        evaluations_ += improvementOperator_.getEvaluations();
                
        if (refSet1Test(individual)){ //Update distance of RefSet2
          for (int indSet2 = 0; indSet2 < refSet2_.size();indSet2++) {
            double aux = distance_.distanceBetweenSolutions(individual,
            refSet2_.get(indSet2));
            if (aux < refSet2_.get(indSet2).getDistanceToSolutionSet()) {
              refSet2_.get(indSet2).setDistanceToSolutionSet(aux);
            } // if
          } // for                    
        }  else {
          refSet2Test(individual);
        } // if 
      }
      subSet_.clear();
    }
  } // referenceSetUpdate
   
  /** 
   * Tries to update the reference set 2 with a <code>Solution</code>
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false 
   * otherwise.
   * @throws JMException 
   */
  public boolean refSet2Test(Solution solution) throws JMException{        
        
    if (refSet2_.size() < refSet2Size_){
      solution.setDistanceToSolutionSet(distance_.distanceToSolutionSetInSolutionSpace(solution,refSet1_));
      double aux = distance_.distanceToSolutionSetInSolutionSpace(solution,refSet2_);
      if (aux < solution.getDistanceToSolutionSet()) {
        solution.setDistanceToSolutionSet(aux);
      }
      refSet2_.add(solution);
      return true;
    }

    solution.setDistanceToSolutionSet(distance_.distanceToSolutionSetInSolutionSpace(solution,refSet1_));
    double aux = distance_.distanceToSolutionSetInSolutionSpace(solution,refSet2_);
    if (aux < solution.getDistanceToSolutionSet()) {
      solution.setDistanceToSolutionSet(aux);
    }
        
    double peor = 0.0;     
    int index = 0;
    for (int i = 0; i < refSet2_.size();i++){
      aux = refSet2_.get(i).getDistanceToSolutionSet();
      if (aux > peor){
        peor = aux;
        index = i;
      }
    }
                
    if (solution.getDistanceToSolutionSet() < peor){            
      refSet2_.remove(index);
      //Update distances in REFSET2
      for (int j = 0; j < refSet2_.size();j++){
        aux = distance_.distanceBetweenSolutions(refSet2_.get(j),solution);
        if (aux < refSet2_.get(j).getDistanceToSolutionSet()){
          refSet2_.get(j).setDistanceToSolutionSet(aux);
        }
      }
      solution.unMarked();
      refSet2_.add(solution);
      return true;
    }           
    return false;
  } // refSet2Test
    
  /** 
   * Tries to update the reference set one with a <code>Solution</code>.
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean refSet1Test(Solution solution){
    boolean dominated = false;
    int flag;      
    int i = 0;
    while (i < refSet1_.size()){
      flag = dominance_.compare(solution,refSet1_.get(i));
      if (flag == -1) { //This is: solution dominates 
        refSet1_.remove(i);
      } else if (flag == 1) {
        dominated = true;
        i++;
      } else {
        flag = equal_.compare(solution,refSet1_.get(i));
        if (flag == 0) {
          return true;
        } // if
        i++;
      } // if 
    } // while
        
    if (!dominated){
      solution.unMarked();
      if (refSet1_.size() < refSet1Size_) { //refSet1 isn't full
        refSet1_.add(solution);
      } else {
        archive_.add(solution);                
      } // if
    } else {
      return false;
    } // if
    return true;        
  } // refSet1Test
    
  /** 
   * Implements the subset generation method described in the scatter search
   * template
   * @return  Number of solutions created by the method
   * @throws JMException 
   */
  public int subSetGeneration() throws JMException{            
    Solution [] parents = new Solution[2];
    Solution [] offSpring;
        
    subSet_.clear();                                                                                        
        
    //All pairs from refSet1
    for (int i = 0; i < refSet1_.size();i++){
      parents[0] = refSet1_.get(i);
      for (int j = i+1; j < refSet1_.size();j++){                
        parents[1] = refSet1_.get(j);
        if (!parents[0].isMarked() || !parents[1].isMarked()){
          //offSpring = parent1.crossover(1.0,parent2);
          offSpring = (Solution [])crossoverOperator_.execute(parents);
          problem_.evaluate(offSpring[0]);
          problem_.evaluate(offSpring[1]);    
          problem_.evaluateConstraints(offSpring[0]);
          problem_.evaluateConstraints(offSpring[1]);                    
          evaluations_ += 2;                                        
          if (evaluations_ < maxEvaluations){
            subSet_.add(offSpring[0]);
            subSet_.add(offSpring[1]);    
          }
          parents[0].marked();
          parents[1].marked();
        }                
      }
    }
    
    // All pairs from refSet2
    for (int i = 0; i < refSet2_.size();i++){
      parents[0] = refSet2_.get(i);
      for (int j = i+1; j < refSet2_.size();j++){                
        parents[1] = refSet2_.get(j);
        if (!parents[0].isMarked() || !parents[1].isMarked()){
          //offSpring = parents[0].crossover(1.0,parent2);                    
          offSpring = (Solution []) crossoverOperator_.execute(parents);
          problem_.evaluateConstraints(offSpring[0]);
          problem_.evaluateConstraints(offSpring[1]);                    
          problem_.evaluate(offSpring[0]);
          problem_.evaluate(offSpring[1]);
          evaluations_+=2;                                        
          if (evaluations_ < maxEvaluations){
            subSet_.add(offSpring[0]);
            subSet_.add(offSpring[1]);
          }
          parents[0].marked();
          parents[1].marked();
        }                
      }
    }
                        
    return subSet_.size();
  } // subSetGeneration
    
  /**   
  * Runs of the AbYSS algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution  
   * @throws JMException 
  */  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
	// STEP 1. Initialize parameters
    initParam();        
    
    // STEP 2. Build the initial solutionSet
    Solution solution;
    for (int i = 0; i < solutionSetSize_; i++) {        
      solution = diversificationGeneration();              
      problem_.evaluate(solution);            
      problem_.evaluateConstraints(solution);
      evaluations_++;
      solution = (Solution)improvementOperator_.execute(solution);            
      evaluations_ += improvementOperator_.getEvaluations();
      solutionSet_.add(solution);            
    } // fpr
        
    // STEP 3. Main loop
    int newSolutions = 0;
    while (evaluations_ < maxEvaluations) {                       
      referenceSetUpdate(true);
      newSolutions = subSetGeneration();        
      while (newSolutions > 0) { // New solutions are created           
        referenceSetUpdate(false);
        if (evaluations_ >= maxEvaluations)                                        
          return archive_;                
        newSolutions = subSetGeneration();                
      } // while
      
      // RE-START
      if (evaluations_ < maxEvaluations){
        solutionSet_.clear();
        // Add refSet1 to SolutionSet
        for (int i = 0; i < refSet1_.size();i++){
          solution = refSet1_.get(i);
          solution.unMarked();
          solution = (Solution)improvementOperator_.execute(solution);
          evaluations_ += improvementOperator_.getEvaluations();
          solutionSet_.add(solution);
        }
        // Remove refSet1 and refSet2
        refSet1_.clear();        
        refSet2_.clear();

        // Sort the archive and insert the best solutions
        distance_.crowdingDistanceAssignment(archive_,
        		                                problem_.getNumberOfObjectives());                                
        archive_.sort(crowdingDistance_);                
            
        int insert = solutionSetSize_  / 2;
        if (insert > archive_.size()) 
          insert = archive_.size();
        
        if (insert > (solutionSetSize_ - solutionSet_.size())) 
          insert = solutionSetSize_ - solutionSet_.size();         
                                
        // Insert solutions 
        for (int i = 0; i < insert; i++){                
          solution = new Solution(archive_.get(i));                                        
          //solution = improvement(solution);
          solution.unMarked();
          solutionSet_.add(solution);
        }
                
        // Create the rest of solutions randomly
        while (solutionSet_.size() < solutionSetSize_){
          solution = diversificationGeneration();                    
          problem_.evaluateConstraints(solution);                                         
          problem_.evaluate(solution);
          evaluations_++;
          solution = (Solution)improvementOperator_.execute(solution);
          evaluations_ += improvementOperator_.getEvaluations();
          solution.unMarked();
          solutionSet_.add(solution);
        } // while
      } // if   
    } // while       
    
    archive_.printFeasibleFUN("FUN_AbYSS") ;

    // STEP 4. Return the archive
    return archive_;                
  } // execute
} // AbYSS
