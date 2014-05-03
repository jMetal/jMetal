//  FastPGA.java
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

package jmetal.metaheuristics.fastPGA;

import jmetal.core.*;
import jmetal.util.Distance;
import jmetal.util.FPGAFitness;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.FPGAFitnessComparator;

import java.util.Comparator;

/**
 * This class implements the FPGA (Fast Pareto Genetic Algorithm).
 */
public class FastPGA extends Algorithm{
  
  /**
   * Constructor
   * Creates a new instance of FastPGA
   */
  public FastPGA(Problem problem) {
    super (problem) ;
  } // FastPGA
  
  /**   
  * Runs of the FastPGA algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution  
   * @throws JMException 
  */  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxPopSize, populationSize,offSpringSize,
        evaluations, maxEvaluations, initialPopulationSize;
    SolutionSet solutionSet, offSpringSolutionSet, candidateSolutionSet = null;
    double a, b, c, d;
    Operator crossover, mutation, selection;    
    int termination;
    Distance distance = new Distance();
    Comparator fpgaFitnessComparator = new FPGAFitnessComparator();
    
    //Read the parameters
    maxPopSize     = ((Integer)getInputParameter("maxPopSize")).intValue();
    maxEvaluations = ((Integer)getInputParameter("maxEvaluations")).intValue();
    initialPopulationSize = 
                  ((Integer)getInputParameter("initialPopulationSize")).intValue();
    termination = ((Integer)getInputParameter("termination")).intValue();
    
    //Read the operators
    crossover = (Operator) operators_.get("crossover");
    mutation  = (Operator) operators_.get("mutation");
    selection = (Operator) operators_.get("selection");
    
    //Read the params
    a = ((Double)getInputParameter("a")).doubleValue();
    b = ((Double)getInputParameter("b")).doubleValue();
    c = ((Double)getInputParameter("c")).doubleValue();
    d = ((Double)getInputParameter("d")).doubleValue();
       
    //Initialize populationSize and offSpringSize
    evaluations = 0;
    populationSize = initialPopulationSize;
    offSpringSize  = maxPopSize;
    
    //Build a solution set randomly
    solutionSet = new SolutionSet(populationSize);
    for (int i = 0; i < populationSize; i++) {
      Solution solution = new Solution(problem_);
      problem_.evaluate(solution);
      problem_.evaluateConstraints(solution);
      evaluations++;
      solutionSet.add(solution);            
    }
    
    //Begin the iterations
    Solution [] parents = new Solution[2];
    Solution [] offSprings;
    boolean stop = false;
    int reachesMaxNonDominated = 0;
    while (!stop) {
      
      // Create the candidate solutionSet
      offSpringSolutionSet = new SolutionSet(offSpringSize);
      for (int i = 0; i < offSpringSize/2; i++) {
        parents[0] = (Solution)selection.execute(solutionSet);
        parents[1] = (Solution)selection.execute(solutionSet);
        offSprings = (Solution [])crossover.execute(parents);
        mutation.execute(offSprings[0]);
        mutation.execute(offSprings[1]);
        problem_.evaluate(offSprings[0]);        
        problem_.evaluateConstraints(offSprings[0]);
        evaluations++;
        problem_.evaluate(offSprings[1]);
        problem_.evaluateConstraints(offSprings[1]);
        evaluations++;
        offSpringSolutionSet.add(offSprings[0]);
        offSpringSolutionSet.add(offSprings[1]);
      }
      
      // Merge the populations
      candidateSolutionSet = solutionSet.union(offSpringSolutionSet);
      
      // Rank
      Ranking ranking = new Ranking(candidateSolutionSet);
      distance.crowdingDistanceAssignment(ranking.getSubfront(0),problem_.getNumberOfObjectives());
      FPGAFitness fitness = new FPGAFitness(candidateSolutionSet,problem_);
      fitness.fitnessAssign();
      
      // Count the non-dominated solutions in candidateSolutionSet      
      int count = ranking.getSubfront(0).size();
      
      //Regulate
      populationSize = (int)Math.min(a + Math.floor(b * count),maxPopSize);
      offSpringSize  = (int)Math.min(c + Math.floor(d * count),maxPopSize);
            
      candidateSolutionSet.sort(fpgaFitnessComparator);
      solutionSet = new SolutionSet(populationSize);
      
      for (int i = 0; i < populationSize; i++) {
        solutionSet.add(candidateSolutionSet.get(i));
      }
      
      //Termination test
      if (termination == 0) {
        ranking = new Ranking(solutionSet);
        count = ranking.getSubfront(0).size();        
        if (count == maxPopSize) {
          if (reachesMaxNonDominated == 0) {
            reachesMaxNonDominated = evaluations;
          }
          if (evaluations - reachesMaxNonDominated >= maxEvaluations) {
            stop = true;
          }        
        } else {
          reachesMaxNonDominated = 0;
        }
      } else {
        if (evaluations >= maxEvaluations) {
          stop = true;
        }
      }
    }
    
    setOutputParameter("evaluations",evaluations);
        
    Ranking ranking = new Ranking(solutionSet);
    return ranking.getSubfront(0);
  } // execute
} // FastPGA
