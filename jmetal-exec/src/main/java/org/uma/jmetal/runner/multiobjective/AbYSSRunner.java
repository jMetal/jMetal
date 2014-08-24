//  AbYSSRunner.java
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
import org.uma.jmetal.metaheuristic.multiobjective.abyss.AbYSS;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.IOException;

/**
 * This class is the main program used to configure and run AbYSS, a multiobjective scatter search metaheuristic,
 * which is described in:
 * A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham
 * "AbYSS: Adapting Scatter Search to Multiobjective Optimization."
 * IEEE Transactions on Evolutionary Computation. Vol. 12, No. 4 (August 2008), pp. 439-457
 */

public class AbYSSRunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws IOException
   * @throws SecurityException
   * @throws java.lang.ClassNotFoundException
   * Usage: three choices
   *       - org.uma.jmetal.runner.multiobjective.AbYSSRunner
   *       - org.uma.jmetal.runner.multiobjective.AbYSSRunner problemName
   *       - org.uma.jmetal.runner.multiobjective.AbYSSRunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws
          JMetalException, SecurityException, IOException, ClassNotFoundException {
    Problem problem ;
    int maxEvaluations;
    int populationSize ;
    int refSet1Size ;
    int refSet2Size ;
    int archiveSize ;
    Archive archive ;
    int improvementRounds ;
    int numberOfSubranges ;

    double mutationProbability;
    double crossoverProbability;
    double mutationDistributionIndex;
    double crossoverDistributionIndex;

    QualityIndicatorGetter indicators;

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
    } else {
      problem = new ZDT4("ArrayReal", 10);
      /* Examples
      //problem = new Kursawe("Real", 3);
      //problem = new Kursawe("BinaryReal", 3);
      //problem = new Water("Real");
      //problem = new ConstrEx("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
      */
    }

    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;
    MutationLocalSearch localSearch ;

    refSet1Size = 10;
    refSet2Size = 10 ;
    populationSize = 20 ;
    archiveSize = 100 ;
    maxEvaluations = 25000;
    numberOfSubranges = 4 ;

    mutationProbability = 1.0 / problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
    improvementRounds = 1 ;

    crossover = new SBXCrossover.Builder()
            .setDistributionIndex(crossoverDistributionIndex)
            .setProbability(crossoverProbability)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setDistributionIndex(mutationDistributionIndex)
            .setProbability(mutationProbability)
            .build();

    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives()) ;

    localSearch = new MutationLocalSearch.Builder(problem)
            .setMutationOperator(mutation)
            .setImprovementRounds(improvementRounds)
            .setArchive(archive)
            .build() ;

    algorithm = new AbYSS.Builder(problem)
            .maxEvaluations(maxEvaluations)
            .populationSize(populationSize)
            .refSet1Size(refSet1Size)
            .refSet2Size(refSet2Size)
            .numberOfSubranges(numberOfSubranges)
            .archive(archive)
            .crossover(crossover)
            .localSearch(localSearch)
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

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      JMetalLogger.logger.info("Quality indicators");
      JMetalLogger.logger.info("Hypervolume: " + indicators.getHypervolume(population));
      JMetalLogger.logger.info("GD         : " + indicators.getGD(population));
      JMetalLogger.logger.info("IGD        : " + indicators.getIGD(population));
      JMetalLogger.logger.info("Spread     : " + indicators.getSpread(population));
      JMetalLogger.logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
