//  DMOEADSettings.java
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
import org.uma.jmetal.metaheuristic.multiobjective.dmopso.DMOPSO;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/** Settings class of algorithm DMOPSO */
public class DMOPSOSettings extends Settings {
  private String dataDirectory;
  private int swarmSize;
  private int maxIterations;
  private int maxAge;
  private String functionType;

  /** Constructor */
  public DMOPSOSettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    swarmSize = 100;
    maxIterations = 250;
    maxAge = 2;
    // _TCHE, _PBI, _AGG
    functionType = "_TCHE";

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
    dataDirectory = "MOEAD_Weights";
  }

  /** Configure DMOPSO with default settings  */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;

    algorithm = new DMOPSO.Builder(problem)
            .setSwarmSize(swarmSize)
            .setMaxIterations(maxIterations)
            .setMaxAge(maxAge)
            .setFunctionType(functionType)
            .build();

    return algorithm;
  }

  /** Configure DMOPSO method from a properties file */
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
