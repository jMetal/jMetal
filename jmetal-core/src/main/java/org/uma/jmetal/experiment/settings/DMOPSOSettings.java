//  dMOEAD_Settings.java
//
//  Authors:
//       Jorge Rodriguez
//       Antonio J. Nebro <antonio@lcc.uma.es>
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
import org.uma.jmetal.metaheuristic.multiobjective.dmopso.dMOPSO;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/**
 * Settings class of algorithm dMOPSO
 */
public class DMOPSOSettings extends Settings {
  private String dataDirectory;
  private int swarmSize;
  private int maxIterations;
  private int maxAge;
  private String functionType;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public DMOPSOSettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    // Default experiment.settings
    swarmSize = 100;
    maxIterations = 250;
    maxAge = 2;
    functionType = "_TCHE";  // _TCHE, _PBI, _AGG

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar

    dataDirectory = "MOEAD_Weights";
  } // dMOPSO_Settings

  /**
   * Configure the algorithm with the specified parameter experiment.settings
   *
   * @return an algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;

    // Creating the problem
    algorithm = new dMOPSO();
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("swarmSize", swarmSize);
    algorithm.setInputParameter("maxIterations", maxIterations);
    algorithm.setInputParameter("maxAge", maxAge);
    algorithm.setInputParameter("functionType", functionType);
    algorithm.setInputParameter("dataDirectory", dataDirectory);

    return algorithm;
  }

  /**
   * Configure dMOPSO with user-defined parameter experiment.settings
   *
   * @return A dMOPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    swarmSize =
      Integer.parseInt(configuration.getProperty("swarmSize", String.valueOf(swarmSize)));
    maxIterations =
      Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations)));
    dataDirectory = configuration.getProperty("dataDirectory", dataDirectory);
    maxAge = Integer.parseInt(configuration.getProperty("maxAge", String.valueOf(maxAge)));
    functionType = configuration.getProperty("functionType", String.valueOf(functionType));

    return configure();
  }
}
