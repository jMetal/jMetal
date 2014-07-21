//  MOEAD_main.java
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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.moead.MOEAD;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityIndicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This class executes the algorithm described in:
 * H. Li and Q. Zhang,
 * "Multiobjective Optimization Problems with Complicated Pareto Sets,  MOEA/D
 * and NSGA-II". IEEE Trans on Evolutionary Computation, vol. 12,  no 2,
 * pp 284-302, April/2009.
 */
public class MOEADRunner {
  public static Logger logger_;
  public static FileHandler fileHandler_;

  /**
   * @param args Command line arguments. The first (optional) argument specifies
   *             the problem to solve.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws IOException
   * @throws SecurityException      Usage: three options
   *                                - org.uma.jmetal.runner.MOEAD_main
   *                                - org.uma.jmetal.runner.MOEAD_main problemName
   *                                - org.uma.jmetal.runner.MOEAD_main problemName ParetoFrontFile
   * @throws ClassNotFoundException
   */
  public static void main(String[] args)
    throws JMetalException, SecurityException, IOException, ClassNotFoundException {
    Problem problem;
    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;

    QualityIndicatorGetter indicators;

    // Logger object and file to store log messages
    logger_ = Configuration.logger;
    fileHandler_ = new FileHandler("MOEAD.log");
    logger_.addHandler(fileHandler_);

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
    } else {
      problem = new Kursawe("Real", 3);
      //problem = new Kursawe("BinaryReal", 3);
      //problem = new Water("Real");
      //problem = new ZDT1("ArrayReal", 100);
      //problem = new ConstrEx("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
    }

    crossover = new DifferentialEvolutionCrossover.Builder()
      .cr(1.0)
      .f(0.5)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0/problem.getNumberOfVariables())
      .build();

    algorithm = new MOEAD.Builder(problem)
      .populationSize(300)
      .maxEvaluations(150000)
      .neighborhoodSelectionProbability(0.9)
      .maximumNumberOfReplacedSolutions(2)
      .neighborSize(20)
      .crossover(crossover)
      .mutation(mutation)
      .dataDirectory("MOEAD_Weights")
      .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
      .separator("\t")
      .varFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .funFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    logger_.info("Total execution time: " + computingTime + "ms");
    logger_.info("Objectives values have been written to file FUN.tsv");
    logger_.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      logger_.info("Quality indicators");
      logger_.info("Hypervolume: " + indicators.getHypervolume(population));
      logger_.info("GD         : " + indicators.getGD(population));
      logger_.info("IGD        : " + indicators.getIGD(population));
      logger_.info("Spread     : " + indicators.getSpread(population));
      logger_.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }

    /*
    algorithm = new MOEAD();
    algorithm.setProblem(problem);
    //algorithm = new MOEAD_DRA(problem);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", 300);
    algorithm.setInputParameter("maxEvaluations", 150000);

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
    algorithm.setInputParameter("dataDirectory", "MOEAD_Weight");

    // used by MOEAD_DRA
    algorithm.setInputParameter("finalSize", 300);

    algorithm.setInputParameter("T", 20);
    algorithm.setInputParameter("delta", 0.9);
    algorithm.setInputParameter("nr", 2);

    // Crossover operator 
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("CR", 1.0);
    crossoverParameters.put("F", 0.5);
    crossover =
      CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", crossoverParameters);

    // Mutation operator
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 1.0 / problem.getNumberOfVariables());
    mutationParameters.put("distributionIndex", 20.0);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", mutationParameters);

    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages 
    logger.info("Total execution time: " + estimatedTime + "ms");
    logger.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
    logger.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");

    if (indicators != null) {
      logger.info("Quality indicators");
      logger.info("Hypervolume: " + indicators.getHypervolume(population));
      logger.info("EPSILON    : " + indicators.getEpsilon(population));
      logger.info("GD         : " + indicators.getGD(population));
      logger.info("IGD        : " + indicators.getIGD(population));
      logger.info("Spread     : " + indicators.getSpread(population));
    }
    */

}
