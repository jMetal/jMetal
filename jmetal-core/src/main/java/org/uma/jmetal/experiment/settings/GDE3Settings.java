//  GDE3_Settings.java 
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
import org.uma.jmetal.metaheuristic.multiobjective.gde3.GDE3;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

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

    // Default experiment.settings
    cr = 0.5;
    f = 0.5;
    populationSize = 100;
    maxIterations = 250;

    evaluator = new SequentialSolutionSetEvaluator() ;
  }

  /** configure() method */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    crossover = new DifferentialEvolutionCrossover.Builder()
      .cr(cr)
      .f(f)
      .build() ;

    selection = new DifferentialEvolutionSelection.Builder()
      .build();

    algorithm = new GDE3.Builder(problem, evaluator)
      .crossover(crossover)
      .selection(selection)
      .maxIterations(maxIterations)
      .populationSize(populationSize)
      .build() ;


    return algorithm ;
  }

  /** configure() method using a properties file */
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
