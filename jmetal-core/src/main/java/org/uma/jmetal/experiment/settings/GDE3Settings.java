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
  private double cr_;
  private double f_;
  private int populationSize_;
  private int maxIterations_;
  private SolutionSetEvaluator evaluator_ ;
  private Crossover crossover_ ;
  private Selection selection_ ;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public GDE3Settings(String problemName) throws JMetalException {
    super(problemName);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiment.settings
    cr_ = 0.5;
    f_ = 0.5;
    populationSize_ = 100;
    maxIterations_ = 250;

    evaluator_ = new SequentialSolutionSetEvaluator() ;
  }

  /**
   * Configure the algorithm with the specified parameter experiment.settings
   *
   * @return an algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    crossover = new DifferentialEvolutionCrossover.Builder()
      .cr(cr_)
      .f(f_)
      .build() ;

    selection = new DifferentialEvolutionSelection.Builder()
      .build();

    algorithm = new GDE3.Builder(problem_, evaluator_)
      .crossover(crossover)
      .selection(selection)
      .maxIterations(maxIterations_)
      .populationSize(populationSize_)
      .build() ;


    return algorithm ;
  }

  /**
   * Configure GDE3 with user-defined parameter experiment.settings
   *
   * @return A GDE3 algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxIterations_ =
      Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations_)));

    cr_ = Double.parseDouble(configuration.getProperty("CR", String.valueOf(cr_)));
    f_ = Double.parseDouble(configuration.getProperty("F", String.valueOf(f_)));

    return configure();
  }
}
