//  AbYSS_Settings.java 
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
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.abyss.AbYSS;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.localSearch.MutationLocalSearch;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm AbYSS
 */
public class AbYSS_Settings extends Settings {
  private int populationSize_;
  private int maxEvaluations_;
  private int archiveSize_;
  private int refSet1Size_;
  private int refSet2Size_;
  private double mutationProbability_;
  private double crossoverProbability_;
  private double crossoverDistributionIndex_;
  private double mutationDistributionIndex_;
  private int improvementRounds_;

  /**
   * Constructor
   *
   * @param problemName Problem to solve
   */
  public AbYSS_Settings(String problemName) {
    super(problemName);

    Object[] problemParams = {"Real"};
    try {
      problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
      Configuration.logger_.log(Level.SEVERE, "Unable to get problem", e);
    }

    populationSize_ = 20;
    maxEvaluations_ = 25000;
    archiveSize_ = 100;
    refSet1Size_ = 10;
    refSet2Size_ = 10;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 1.0;
    crossoverDistributionIndex_ = 20.0;
    mutationDistributionIndex_ = 20.0;
    improvementRounds_ = 1;

  }

  /**
   * Configure the AbYSS algorithm with default parameter experiments.settings
   *
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator improvement;

    // Creating the problem
    algorithm = new AbYSS();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("refSet1Size", refSet1Size_);
    algorithm.setInputParameter("refSet2Size", refSet2Size_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_);
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("improvementRounds", improvementRounds_);
    parameters.put("problem", problem_);
    parameters.put("mutation", mutation);
    improvement = new MutationLocalSearch(parameters);

    // Adding the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("improvement", improvement);

    return algorithm;
  }

  /**
   * Configure AbYSS with user-defined parameter experiments.settings
   *
   * @return An AbYSS algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    // Algorithm parameters
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    archiveSize_ =
      Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));
    refSet1Size_ =
      Integer.parseInt(configuration.getProperty("refSet1Size", String.valueOf(refSet1Size_)));
    refSet2Size_ =
      Integer.parseInt(configuration.getProperty("refSet2Size", String.valueOf(refSet2Size_)));
    improvementRounds_ = Integer
      .parseInt(configuration.getProperty("improvementRounds", String.valueOf(improvementRounds_)));

    crossoverProbability_ = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    crossoverDistributionIndex_ = Double.parseDouble(configuration
      .getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex_)));

    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));

    return configure();
  }
} 
