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
package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.moead.pMOEAD;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm MOEA/D
 */
public class ParallelMOEADSettings extends Settings {
  private double cr;
  private double f;
  private int populationSize;
  private int maxEvaluations;

  private double mutationProbability;
  private double mutationDistributionIndex;

  private String dataDirectory;

  private int t;
  private double delta;
  private int nr;

  private int numberOfThreads;

  /**
   * Constructor
   * @throws org.uma.jmetal.util.JMetalException
   */
  public ParallelMOEADSettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    // Default experiment.settings
    cr = 1.0;
    f = 0.5;
    populationSize = 600;
    maxEvaluations = 150000;

    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    mutationDistributionIndex = 20;

    t = 60;
    delta = 0.9;
    nr = 6;

    // Directory with the files containing the weight vectors used in
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar

    dataDirectory = "MOEAD_Weights";

    numberOfThreads = 4;
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
    algorithm = new pMOEAD();
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("numberOfThreads", numberOfThreads);
    algorithm.setInputParameter("populationSize", populationSize);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations);
    algorithm.setInputParameter("dataDirectory", dataDirectory);
    algorithm.setInputParameter("T", t);
    algorithm.setInputParameter("delta", delta);
    algorithm.setInputParameter("nr", nr);

    // Crossover operator
    HashMap<String, Object> parameters = new HashMap<String, Object>() ;
    parameters.put("CR", cr);
    parameters.put("F", f);

    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    // Mutation operator
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability);
    parameters.put("distributionIndex", mutationDistributionIndex);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);

    return algorithm;
  } 

  /**
   * Configure pMOEAD with user-defined parameter experiment.settings
   *
   * @return A pMOEAD algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(
      populationSize)));
    maxEvaluations = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(
      maxEvaluations)));
    numberOfThreads = Integer.parseInt(configuration.getProperty("numberOfThreads",String.valueOf(
      numberOfThreads)));
    dataDirectory = configuration.getProperty("dataDirectory", dataDirectory);

    delta = Double.parseDouble(configuration.getProperty("delta", String.valueOf(delta)));
    t = Integer.parseInt(configuration.getProperty("T", String.valueOf(t)));
    nr = Integer.parseInt(configuration.getProperty("nr", String.valueOf(nr)));

    return configure() ;
  }
} 
