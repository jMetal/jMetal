//  TSPGARunner.java
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

package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.singleobjective.geneticalgorithm.ssGA;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.singleobjective.TSP;
import org.uma.jmetal.util.JMetalLogger;

import java.util.HashMap;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be
 * a steady-state GA (class SSGA) or a generational GA (class GGA). The TSP
 * is used to org.uma.test the algorithms. The data files accepted as in input are from
 * TSPLIB.
 */
public class TSPGARunner {

  public static void main(String[] args) throws Exception {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    String problemName = "eil101.tsp";

    problem = new TSP("Permutation", problemName);

    algorithm = new ssGA();
    algorithm.setProblem(problem);
    //algorithm = new gGA(problem) ;

    // Algorithm params
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 2000000);

    // Mutation and Crossover for Real codification
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", 0.95);
    crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover", crossoverParameters);
    //crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover");

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 0.2);
    mutation = MutationFactory.getMutationOperator("SwapMutation", mutationParameters);                    
  
    /* Selection Operator */
    HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", selectionParameters);
    
    /* Add the operator to the algorithm*/
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    /* Execute the Algorithm */
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    JMetalLogger.logger.info("Total time of execution: " + estimatedTime);

    /* Log messages */
    JMetalLogger.logger.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    JMetalLogger.logger.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");
  }
}
