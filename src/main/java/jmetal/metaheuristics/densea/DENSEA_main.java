//  DENSEA_main.java
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

package jmetal.metaheuristics.densea;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.BinaryTournament;
import jmetal.problems.ZDT.ZDT5;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class for configuring and running the DENSEA algorithm
 */
public class DENSEA_main {
  public static Logger logger_;     
  public static FileHandler fileHandler_; 

  public static void main(String[] args) throws JMException, IOException, ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    // Logger object and file to store log messages
    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("Densea.log");
    logger_.addHandler(fileHandler_);

    problem = new ZDT5("Binary");

    algorithm = new DENSEA();
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 25000);

    // Mutation and Crossover Binary codification 
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", 0.9);
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", crossoverParameters);
    crossover.setParameter("probability", 0.9);

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 1.0 / 149);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", mutationParameters);

    // Selection Operator 
    HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
    selection = new BinaryTournament(selectionParameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    Configuration.logger_.info("Total time of execution: " + estimatedTime);

    // Log messages 
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");
  }
}

