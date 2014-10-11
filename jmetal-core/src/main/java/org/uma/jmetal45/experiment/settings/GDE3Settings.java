//  GDE3Settings.java
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

package org.uma.jmetal45.experiment.settings;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.metaheuristic.multiobjective.gde3.GDE3;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal45.operator.selection.DifferentialEvolutionSelection;
import org.uma.jmetal45.operator.selection.Selection;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal45.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/**
 * Settings class of algorithm GDE3
 */
public class GDE3Settings extends Settings {
  private double cr;
  private double f;
  private int populationSize;
  private int maxIterations;
  private SolutionSetEvaluator evaluator;

  /** Constructor */
  public GDE3Settings(String problemName) throws JMetalException {
    super(problemName);

    Object[] problemParams = {"Real"};
    problem = (new ProblemFactory()).getProblem(this.problemName, problemParams);

    cr = 0.5;
    f = 0.5;
    populationSize = 100;
    maxIterations = 250;
  }

  /** Configure GDE3 with default parameter settings */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    
    evaluator = new SequentialSolutionSetEvaluator() ;

    crossover = new DifferentialEvolutionCrossover.Builder()
            .setCr(cr)
            .setF(f)
            .build() ;

    selection = new DifferentialEvolutionSelection.Builder()
            .build();

    algorithm = new GDE3.Builder(problem, evaluator)
            .setCrossover(crossover)
            .setSelection(selection)
            .setMaxIterations(maxIterations)
            .setPopulationSize(populationSize)
            .build() ;


    return algorithm ;
  }

  /** Configure GDE3 from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
            .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxIterations =
            Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations)));

    cr = Double.parseDouble(configuration.getProperty("CR", String.valueOf(cr)));
    f = Double.parseDouble(configuration.getProperty("F", String.valueOf(f)));

    return configure();
  }
}
