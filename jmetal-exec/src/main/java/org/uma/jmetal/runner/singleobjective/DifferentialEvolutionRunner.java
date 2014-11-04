//  NSGAIIRunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolution;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class DifferentialEvolutionRunner {
  /**
   */
  public static void main(String[] args) throws Exception {

    ContinuousProblem problem;
    Algorithm algorithm;
    DifferentialEvolutionSelection selection;
    DifferentialEvolutionCrossover crossover;

    String problemName = "org.uma.jmetal.problem.singleobjective.Sphere" ;

    problem = (ContinuousProblem) ProblemUtils.loadProblem(problemName);

     /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator()
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem)
     */

    crossover = new DifferentialEvolutionCrossover.Builder()
            .setCr(0.5)
            .setF(0.5)
            .setVariant("rand/1/bin")
            .build() ;

    selection = new DifferentialEvolutionSelection.Builder()
            .build();

    algorithm = new DifferentialEvolution.Builder(problem)
            .setCrossover(crossover)
            .setSelection(selection)
            .setMaxEvaluations(250000)
            .setPopulationSize(100)
            .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    DoubleSolution solution = ((DifferentialEvolution)algorithm).getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    List<Solution> population = new ArrayList<>(1) ;
    population.add(solution) ;
    new SolutionSetOutput.Printer(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

  }
}
