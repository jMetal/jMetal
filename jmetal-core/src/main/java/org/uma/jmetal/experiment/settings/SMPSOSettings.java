//  SMPSOSettings.java 
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
import org.uma.jmetal.metaheuristic.multiobjective.smpso.SMPSO;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/** Settings class of algorithm SMPSO */
public class SMPSOSettings extends Settings {
  private int swarmSize;
  private int maxIterations;
  private int archiveSize;
  private double mutationDistributionIndex;
  private double mutationProbability;

  private double c1Max;
  private double c1Min;
  private double c2Max;
  private double c2Min;
  private double r1Max;
  private double r1Min;
  private double r2Max;
  private double r2Min;
  private double weightMax;
  private double weightMin;
  private double changeVelocity1;
  private double changeVelocity2;

  private SolutionSetEvaluator evaluator ;

  /** Constructor */
  public SMPSOSettings(String problem) {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    // Default experiment.settings
    swarmSize = 100;
    maxIterations = 250;
    archiveSize = 100;
    mutationDistributionIndex = 20.0;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();

    evaluator = new SequentialSolutionSetEvaluator() ;

    c1Max = 2.5;
    c1Min = 1.5;
    c2Max = 2.5;
    c2Min = 1.5;
    r1Max = 1.0;
    r1Min = 0.0;
    r2Max = 1.0;
    r2Min = 0.0;
    weightMax = 0.1;
    weightMin = 0.1;
    changeVelocity1 = -1;
    changeVelocity2 = -1;
  }

  /** Configure SMPSO with default parameter settings s*/
  public Algorithm configure() {
    Algorithm algorithm;
    Mutation mutation;

    Archive archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives()) ;

    mutation = new PolynomialMutation.Builder()
      .setDistributionIndex(mutationDistributionIndex)
      .setProbability(mutationProbability)
      .build();

    algorithm = new SMPSO.Builder(problem, archive, evaluator)
      .setMutation(mutation)
      .setMaxIterations(maxIterations)
      .setSwarmSize(swarmSize)
      .setC1Max(c1Max)
      .setC1Min(c1Min)
      .setC2Max(c2Max)
      .setC2Min(c2Min)
      .setR1Max(r1Max)
      .setR1Min(r1Min)
      .setR2Max(r2Max)
      .setR2Min(r2Min)
      .setWeightMax(weightMax)
      .setWeightMin(weightMin)
      .setChangeVelocity1(changeVelocity1)
      .setChangeVelocity2(changeVelocity2)
      .build();

    return algorithm ;
  }

  /** Configure SMPSO from a propertires file */
  @Override
  public Algorithm configure(Properties configuration) {
    swarmSize =
      Integer.parseInt(configuration.getProperty("swarmSize", String.valueOf(swarmSize)));
    maxIterations =
      Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations)));
    archiveSize =
      Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));

    c1Min = Double.parseDouble(configuration.getProperty("c1Min", String.valueOf(c1Min)));
    c1Max = Double.parseDouble(configuration.getProperty("c1Max", String.valueOf(c1Max)));
    c2Min = Double.parseDouble(configuration.getProperty("c2Min", String.valueOf(c2Min)));
    c2Max = Double.parseDouble(configuration.getProperty("c2Max", String.valueOf(c2Max)));
    r1Min = Double.parseDouble(configuration.getProperty("r1Min", String.valueOf(r1Min)));
    r1Max = Double.parseDouble(configuration.getProperty("r1Max", String.valueOf(r1Max)));
    r2Min = Double.parseDouble(configuration.getProperty("r2Min", String.valueOf(r2Min)));
    r2Max = Double.parseDouble(configuration.getProperty("r2Max", String.valueOf(r2Max)));
    weightMin = Double.parseDouble(configuration.getProperty("weightMin", String.valueOf(weightMin)));
    weightMax = Double.parseDouble(configuration.getProperty("weightMax", String.valueOf(weightMax)));
    changeVelocity1 = Double.parseDouble(configuration.getProperty("changeVelocity1", String.valueOf(changeVelocity1)));
    changeVelocity2 = Double.parseDouble(configuration.getProperty("changeVelocity2", String.valueOf(changeVelocity2)));

    return configure() ;
  }
} 
