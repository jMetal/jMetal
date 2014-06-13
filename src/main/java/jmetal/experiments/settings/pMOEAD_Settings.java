//  pMOEAD_Settings.java
//
//  Author:
//       Andre Siqueira
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
import jmetal.metaheuristics.moead.pMOEAD;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm MOEA/D
 */
public class pMOEAD_Settings extends Settings {
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

  private int numberOfThreads_; 

  /**
   * Constructor
   * @throws JMException 
   */
  public pMOEAD_Settings(String problem) throws JMException {
    super(problem);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiments.settings
    cr_ = 1.0;
    f_ = 0.5;
    populationSize_ = 600;
    maxEvaluations_ = 150000;

    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    mutationDistributionIndex_ = 20;

    t_ = 60;
    delta_ = 0.9;
    nr_ = 6;

    // Directory with the files containing the weight vectors used in
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar

    dataDirectory_ = "MOEAD_Weights";

    numberOfThreads_ = 4; 
  } 

  /**
   * Configure the algorithm with the specified parameter experiments.settings
   *
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;

    // Creating the problem
    algorithm = new pMOEAD();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("numberOfThreads", numberOfThreads_);
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("dataDirectory", dataDirectory_);
    algorithm.setInputParameter("T", t_);
    algorithm.setInputParameter("delta", delta_);
    algorithm.setInputParameter("nr", nr_);

    // Crossover operator
    HashMap<String, Object> parameters = new HashMap<String, Object>() ;
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
   * Configure pMOEAD with user-defined parameter experiments.settings
   *
   * @return A pMOEAD algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    numberOfThreads_  = Integer.parseInt(configuration.getProperty("numberOfThreads",String.valueOf(numberOfThreads_)));
    dataDirectory_  = configuration.getProperty("dataDirectory", dataDirectory_);

    delta_ = Double.parseDouble(configuration.getProperty("delta", String.valueOf(delta_)));
    t_ = Integer.parseInt(configuration.getProperty("T", String.valueOf(t_)));
    nr_ = Integer.parseInt(configuration.getProperty("nr", String.valueOf(nr_)));

    return configure() ;
  }
} 
