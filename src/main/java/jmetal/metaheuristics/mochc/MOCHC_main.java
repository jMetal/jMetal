//  MOCHC_main.java
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

package jmetal.metaheuristics.mochc;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ZDT.ZDT5;

import java.util.HashMap;
/**
 * This class executes the algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo 
 * "Optimal antenna placement using a new multi-objective chc algorithm". 
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and 
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHC_main {

  public static void main(String [] args) {
  	
    HashMap  parameters ; // Operator parameters

    try {                               
      Problem problem = new ZDT5("Binary");

      Algorithm algorithm = null;
      algorithm = new MOCHC(problem);
      
      algorithm.setInputParameter("initialConvergenceCount",0.25);
      algorithm.setInputParameter("preservedPopulation",0.05);
      algorithm.setInputParameter("convergenceValue",3);
      algorithm.setInputParameter("populationSize",100);
      algorithm.setInputParameter("maxEvaluations",25000);
      
      Operator crossoverOperator      ;
      Operator mutationOperator       ;
      Operator parentsSelection       ;
      Operator newGenerationSelection ;
      
      // Crossover operator
      parameters = new HashMap() ;
      parameters.put("probability", 1.0) ;
      crossoverOperator = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);
     
      //parentsSelection = new RandomSelection();
      //newGenerationSelection = new RankingAndCrowdingSelection(problem);
      parameters = null ;
      parentsSelection = SelectionFactory.getSelectionOperator("RandomSelection", parameters) ;     
      
      parameters = new HashMap() ;
      parameters.put("problem", problem) ;
      newGenerationSelection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;
     
      // Mutation operator
      parameters = new HashMap() ;
      parameters.put("probability", 0.35) ;
      mutationOperator = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
      
      algorithm.addOperator("crossover",crossoverOperator);
      algorithm.addOperator("cataclysmicMutation",mutationOperator);
      algorithm.addOperator("parentSelection",parentsSelection);
      algorithm.addOperator("newGenerationSelection",newGenerationSelection);
      
      // Execute the Algorithm 
      long initTime = System.currentTimeMillis();
      SolutionSet population = algorithm.execute();
      long estimatedTime = System.currentTimeMillis() - initTime;
      System.out.println("Total execution time: "+estimatedTime);

      // Print results
      population.printVariablesToFile("VAR");
      population.printObjectivesToFile("FUN");
    } //try           
    catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
    } //catch    
  }//main
}
