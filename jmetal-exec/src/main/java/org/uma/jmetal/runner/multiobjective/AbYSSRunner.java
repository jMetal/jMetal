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
import org.uma.jmetal.metaheuristic.multiobjective.abyss.AbYSS;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This class is the main program used to configure and run AbYSS, a
 * multiobjective scatter search metaheuristic, which is described in:
 * A.J. Nebro, F. Luna, E. Alba, B. Dorronsoro, J.J. Durillo, A. Beham
 * "AbYSS: Adapting Scatter Search to Multiobjective Optimization."
 * IEEE Transactions on Evolutionary Computation. Vol. 12,
 * No. 4 (August 2008), pp. 439-457
 */

public class AbYSSRunner {
  public static Logger LOGGER = Logger.getLogger(AbYSSRunner.class.getName());
  public static FileHandler fileHandler;

  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws IOException
   * @throws SecurityException Usage: three choices
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main problemName
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_main problemName paretoFrontFile
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

    // Logger object and file to store log messages
    fileHandler = new FileHandler("AbYSS.log");
    LOGGER.addHandler(fileHandler);

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
    } else {
      //problem = new Kursawe("Real", 3);
      //problem = new Kursawe("BinaryReal", 3);
      //problem = new Water("Real");
      problem = new ZDT4("ArrayReal", 10);
      //problem = new ConstrEx("Real");
      //problem = new DTLZ1("Real");
      //problem = new OKA2("Real") ;
    } // else

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
            .distributionIndex(crossoverDistributionIndex)
            .probability(crossoverProbability)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .distributionIndex(mutationDistributionIndex)
            .probability(mutationProbability)
            .build();

    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives()) ;

    localSearch = new MutationLocalSearch.Builder(problem)
            .mutationOperator(mutation)
            .improvementRounds(improvementRounds)
            .archive(archive)
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
  }
}
