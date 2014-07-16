//  MOCHCRunner.java
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
import org.uma.jmetal.metaheuristic.multiobjective.mochc.MOCHC;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.HUXCrossover;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.selection.RandomSelection;
import org.uma.jmetal.operator.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.ZDT.ZDT5;
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
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHCRunner {
  private static Logger logger_;
  private static FileHandler fileHandler_;

  public static void main(String[] args) throws IOException, JMetalException, ClassNotFoundException {
    Crossover crossoverOperator;
    Mutation mutationOperator;
    Selection parentsSelection;
    Selection newGenerationSelection;
    Algorithm algorithm ;

    logger_ = Configuration.logger;
    fileHandler_ = new FileHandler("MOCHCRunner.log");
    logger_.addHandler(fileHandler_);

    Problem problem = new ZDT5("Binary");

    crossoverOperator = new HUXCrossover.Builder()
      .probability(1.0)
      .build() ;

    parentsSelection = new RandomSelection.Builder()
      .build() ;

    newGenerationSelection = new RankingAndCrowdingSelection.Builder(100)
      .build() ;

    mutationOperator = new BitFlipMutation.Builder()
      .probability(0.35)
      .build() ;

    algorithm = new MOCHC.Builder(problem)
      .initialConvergenceCount(0.25)
      .convergenceValue(3)
      .preservedPopulation(0.05)
      .populationSize(100)
      .maxEvaluations(25000)
      .crossover(crossoverOperator)
      .newGenerationSelection(newGenerationSelection)
      .cataclysmicMutation(mutationOperator)
      .parentSelection(parentsSelection)
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
  }
}
