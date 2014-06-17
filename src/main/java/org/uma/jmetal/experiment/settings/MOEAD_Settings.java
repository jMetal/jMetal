//  MOEAD_Settings.java 
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
import org.uma.jmetal.metaheuristic.moead.MOEAD;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm MOEA/D
 */
public class MOEAD_Settings extends Settings {
  private double cr_;
  private double f_;
  private int populationSize_;
  private int maxEvaluations_;

  private double mutationProbability_;
  private double mutationDistributionIndex_;

  private String dataDirectory_;

  private int t_;
  private double delta_;
  private int nr_;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public MOEAD_Settings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiment.settings
    cr_ = 1.0;
    f_ = 0.5;
    populationSize_ = 300;
    maxEvaluations_ = 150000;

    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    mutationDistributionIndex_ = 20;

    t_ = 20;
    delta_ = 0.9;
    nr_ = 2;

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar

    dataDirectory_ = "MOEAD_Weights";
  }

  /**
   * Configure the algorithm with the specified parameter experiment.settings
   *
   * @return an algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;

    // Creating the problem
    algorithm = new MOEAD();
    algorithm.setProblem(problem_);
    

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("dataDirectory", dataDirectory_);
    algorithm.setInputParameter("T", t_);
    algorithm.setInputParameter("delta", delta_);
    algorithm.setInputParameter("nr", nr_);

    // Crossover operator 
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("CR", cr_);
    parameters.put("F", f_);
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    // Mutation operator
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);

    return algorithm;
  }

  /**
   * Configure MOEAD with user-defined parameter experiment.settings
   *
   * @return A MOEAD algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    dataDirectory_ = configuration.getProperty("dataDirectory", dataDirectory_);
    delta_ = Double.parseDouble(configuration.getProperty("delta", String.valueOf(delta_)));
    t_ = Integer.parseInt(configuration.getProperty("T", String.valueOf(t_)));
    nr_ = Integer.parseInt(configuration.getProperty("nr", String.valueOf(nr_)));

    cr_ = Double.parseDouble(configuration.getProperty("CR", String.valueOf(cr_)));
    f_ = Double.parseDouble(configuration.getProperty("F", String.valueOf(f_)));

    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));

    return configure();
  }
} 
