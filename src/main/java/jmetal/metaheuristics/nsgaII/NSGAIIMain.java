//  NSGAII_main.java
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

package jmetal.metaheuristics.nsgaII;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import jmetal.util.evaluator.SolutionSetEvaluator;
import jmetal.util.fileOutput.DefaultFileOutputContext;
import jmetal.util.fileOutput.FileOutputContext;
import jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class to configure and execute the NSGA-II algorithm.
 * <p/>
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba
 * "On the Effect of the Steady-State Selection Scheme in
 * Multi-Objective Genetic Algorithms"
 * 5th International Conference, EMO 2009, pp: 183-197.
 * April 2009)
 */

public class NSGAIIMain {
  public static Logger logger_;
  public static FileHandler fileHandler_;

  /**
   * @param args Command line arguments.
   * @throws jmetal.util.JMException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - jmetal.metaheuristics.nsgaII.NSGAII_main
   *                           - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
   *                           - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public static void main(String[] args) throws
    JMException,
    SecurityException,
    IOException,
    ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicator indicators;

    // Logger object and file to store log messages
    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("NSGAII_main.log");
    logger_.addHandler(fileHandler_);

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicator(problem, args[1]);
    } else {
      //problem = new Kursawe("Real", 3);
      //problem = new Kursawe("BinaryReal", 3);
      //problem = new Water("Real");
      problem = new ZDT3("ArrayReal", 30);
      //problem = new ConstrEx("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
    }

    //Injector injector = Guice.createInjector(new ExecutorModule()) ;
    //Executor executor = injector.getInstance(Executor.class) ;

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator();
    //SolutionSetEvaluator executor = new MultithreadedSolutionSetEvaluator(4, problem) ;
    algorithm = new NSGAIIE(problem, evaluator);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 25000);

    // Mutation and Crossover for Real codification 
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", 0.9);
    crossoverParameters.put("distributionIndex", 20.0);
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", crossoverParameters);

    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 1.0 / problem.getNumberOfVariables());
    mutationParameters.put("distributionIndex", 20.0);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);

    // Selection Operator 
    HashMap<String, Object> selectionParameters = null; // FIXME: why we are passing null?
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", selectionParameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators);

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages
    FileOutputContext fileContext = new DefaultFileOutputContext("VAR.tsv") ;
    fileContext.setSeparator("\t");

    logger_.info("Total execution time: " + estimatedTime + "ms");
    logger_.info("Variables values have been writen to file VAR");
    SolutionSetOutput.printVariablesToFile(fileContext, population) ;

    fileContext = new DefaultFileOutputContext("FUN.tsv");
    fileContext.setSeparator("\t");

    SolutionSetOutput.printObjectivesToFile(fileContext, population);
    population.printObjectivesToFile("FUN");
    logger_.info("Objectives values have been written to file FUN");


    if (indicators != null) {
      logger_.info("Quality indicators");
      logger_.info("Hypervolume: " + indicators.getHypervolume(population));
      logger_.info("GD         : " + indicators.getGD(population));
      logger_.info("IGD        : " + indicators.getIGD(population));
      logger_.info("Spread     : " + indicators.getSpread(population));
      logger_.info("Epsilon    : " + indicators.getEpsilon(population));

      int evaluations = (Integer) algorithm.getOutputParameter("evaluations");
      logger_.info("Speed      : " + evaluations + " evaluations");
    }

    evaluator.shutdown();
  }
}
