//  OMOPSO_Settings.java 
//
//  Authors:
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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.omopso.OMOPSO;
import org.uma.jmetal.operator.mutation.NonUniformMutation;
import org.uma.jmetal.operator.mutation.UniformMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/**
 * Settings class of algorithm OMOPSO
 */
public class OMOPSOSettings extends Settings{
  private int swarmSize;
  private int maxIterations;
  private int archiveSize;
  private double perturbationIndex;
  private double mutationProbability;

  private SolutionSetEvaluator evaluator ;

  /** Constructor */
  public OMOPSOSettings(String problem) {
    super(problem) ;

    Object [] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    evaluator = new SequentialSolutionSetEvaluator() ;

    // Default experiment.settings
    swarmSize = 100 ;
    maxIterations = 250 ;
    archiveSize = 100 ;
    perturbationIndex = 0.5 ;
    mutationProbability = 1.0/ this.problem.getNumberOfVariables() ;
  }

  /**
   * Configure OMOPSO with user-defined parameter experiment.settings
   *
   * @return A OMOPSO algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    OMOPSO algorithm;
    UniformMutation uniformMutation;
    NonUniformMutation nonUniformMutation;

    uniformMutation = new UniformMutation.Builder(perturbationIndex, mutationProbability)
      .build() ;

    nonUniformMutation = new NonUniformMutation.Builder(perturbationIndex, mutationProbability, maxIterations)
      .build() ;

    algorithm = new OMOPSO.Builder(problem, evaluator)
      .swarmSize(swarmSize)
      .archiveSize(archiveSize)
      .maxIterations(maxIterations)
      .uniformMutation(uniformMutation)
      .nonUniformMutation(nonUniformMutation)
      .build() ;

    return algorithm ;
  }

  /**
   * Configure OMOPSO with user-defined parameter settings
   *
   * @return A OMOPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    swarmSize = Integer.parseInt(configuration.getProperty("swarmSize",String.valueOf(swarmSize)));
    maxIterations = Integer.parseInt(configuration.getProperty("maxIterations",String.valueOf(
      maxIterations)));
    archiveSize = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(
      archiveSize)));

    mutationProbability = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(
      mutationProbability)));
    perturbationIndex = Double.parseDouble(configuration.getProperty("perturbationIndex",String.valueOf(
      mutationProbability)));

    return configure() ;
  }
} 
