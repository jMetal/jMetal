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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.abyss.AbYSS;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;

/** Settings class of algorithm AbYSS */
public class AbYSSSettings extends Settings {
  private int populationSize;
  private int maxEvaluations;
  private int archiveSize;
  private int refSet1Size;
  private int refSet2Size;
  private double mutationProbability;
  private double crossoverProbability;
  private double crossoverDistributionIndex;
  private double mutationDistributionIndex;
  private int improvementRounds;

  /** Constructor */
  public AbYSSSettings(String problemName) throws JMetalException {
    super(problemName);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(this.problemName, problemParams);

    populationSize = 20;
    maxEvaluations = 25000;
    archiveSize = 100;
    refSet1Size = 10;
    refSet2Size = 10;
    mutationProbability = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability = 1.0;
    crossoverDistributionIndex = 20.0;
    mutationDistributionIndex = 20.0;
    improvementRounds = 1;
  }

  /** configure() method */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator improvement;

    // Creating the problem
    algorithm = new AbYSS();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize);
    algorithm.setInputParameter("refSet1Size", refSet1Size);
    algorithm.setInputParameter("refSet2Size", refSet2Size);
    algorithm.setInputParameter("archiveSize", archiveSize);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations);

    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability);
    parameters.put("distributionIndex", crossoverDistributionIndex);
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability);
    parameters.put("distributionIndex", mutationDistributionIndex);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("improvementRounds", improvementRounds);
    parameters.put("problem", problem_);
    parameters.put("mutation", mutation);
    improvement = new MutationLocalSearch(parameters);

    // Adding the operator to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("improvement", improvement);

    return algorithm;
  }

  /** configure() method using a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    // Algorithm parameters
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    archiveSize =
      Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));
    refSet1Size =
      Integer.parseInt(configuration.getProperty("refSet1Size", String.valueOf(refSet1Size)));
    refSet2Size =
      Integer.parseInt(configuration.getProperty("refSet2Size", String.valueOf(refSet2Size)));
    improvementRounds = Integer
      .parseInt(configuration.getProperty("improvementRounds", String.valueOf(improvementRounds)));

    crossoverProbability = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    crossoverDistributionIndex = Double.parseDouble(configuration
      .getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex)));

    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex)));

    return configure();
  }
} 
