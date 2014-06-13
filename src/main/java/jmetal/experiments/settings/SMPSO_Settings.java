//  SMPSO_Settings.java 
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.ProblemFactory;
import jmetal.util.archive.Archive;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/**
 * Settings class of algorithm SMPSO
 */

public class SMPSO_Settings extends Settings {
  private int swarmSize_;
  private int maxIterations_;
  private int archiveSize_;
  private double mutationDistributionIndex_;
  private double mutationProbability_;

  private double c1Max_;
  private double c1Min_;
  private double c2Max_;
  private double c2Min_;
  private double r1Max_;
  private double r1Min_;
  private double r2Max_;
  private double r2Min_;
  private double weightMax_;
  private double weightMin_;
  private double changeVelocity1_;
  private double changeVelocity2_;

  private SolutionSetEvaluator evaluator_ ;


  public SMPSO_Settings(String problem) {
    super(problem);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiments.settings
    swarmSize_ = 100;
    maxIterations_ = 250;
    archiveSize_ = 100;
    mutationDistributionIndex_ = 20.0;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();

    evaluator_ = new SequentialSolutionSetEvaluator() ;

    c1Max_ = 2.5;
    c1Min_ = 1.5;
    c2Max_ = 2.5;
    c2Min_ = 1.5;
    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    weightMax_ = 0.1;
    weightMin_ = 0.1;
    changeVelocity1_ = -1;
    changeVelocity2_ = -1;
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   *
   * @return A SMPSO algorithm object
   */
  public Algorithm configure() {
    Algorithm algorithm;
    Mutation mutation;

    Archive archive = new CrowdingArchive(100, problem_.getNumberOfObjectives()) ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0 / problem_.getNumberOfVariables())
      .build();

    algorithm = new SMPSO.Builder(problem_, archive, evaluator_)
      .mutation(mutation)
      .maxIterations(maxIterations_)
      .swarmSize(swarmSize_)
      .archiveSize(archiveSize_)
      .c1Max(c1Max_)
      .c1Min(c1Min_)
      .c2Max(c2Max_)
      .c2Min(c2Min_)
      .r1Max(r1Max_)
      .r1Min(r1Min_)
      .r2Max(r2Max_)
      .r2Min(r2Min_)
      .weightMax(weightMax_)
      .weightMin(weightMin_)
      .changeVelocity1(changeVelocity1_)
      .changeVelocity2(changeVelocity2_)
      .build();

    return algorithm ;
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   *
   * @return A SMPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) {
    swarmSize_ =
      Integer.parseInt(configuration.getProperty("swarmSize", String.valueOf(swarmSize_)));
    maxIterations_ =
      Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations_)));
    archiveSize_ =
      Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));

    c1Min_ = Double.parseDouble(configuration.getProperty("c1Min", String.valueOf(c1Min_)));
    c1Max_ = Double.parseDouble(configuration.getProperty("c1Max", String.valueOf(c1Max_)));
    c2Min_ = Double.parseDouble(configuration.getProperty("c2Min", String.valueOf(c2Min_)));
    c2Max_ = Double.parseDouble(configuration.getProperty("c2Max", String.valueOf(c2Max_)));
    r1Min_ = Double.parseDouble(configuration.getProperty("r1Min", String.valueOf(r1Min_)));
    r1Max_ = Double.parseDouble(configuration.getProperty("r1Max", String.valueOf(r1Max_)));
    r2Min_ = Double.parseDouble(configuration.getProperty("r2Min", String.valueOf(r2Min_)));
    r2Max_ = Double.parseDouble(configuration.getProperty("r2Max", String.valueOf(r2Max_)));
    weightMin_ = Double.parseDouble(configuration.getProperty("weightMin", String.valueOf(weightMin_)));
    weightMax_ = Double.parseDouble(configuration.getProperty("weightMax", String.valueOf(weightMax_)));
    changeVelocity1_ = Double.parseDouble(configuration.getProperty("changeVelocity1", String.valueOf(changeVelocity1_)));
    changeVelocity2_ = Double.parseDouble(configuration.getProperty("changeVelocity2", String.valueOf(changeVelocity2_)));

    return configure() ;
  }
} 
