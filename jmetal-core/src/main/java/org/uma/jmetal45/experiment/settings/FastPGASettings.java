//  FastPGASettings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.metaheuristic.multiobjective.fastpga.FastPGA;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.SBXCrossover;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.operator.selection.BinaryTournament;
import org.uma.jmetal45.operator.selection.Selection;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.comparator.FPGAFitnessComparator;

import java.util.Properties;

/** Settings class of algorithm FastPGA */
public class FastPGASettings extends Settings {
  private Problem problem ;
  private int maxPopulationSize ;
  private int initialPopulationSize ;
  private int maxEvaluations ;
  private double a ;
  private double b ;
  private double c ;
  private double d ;
  private int termination ;

  private Crossover crossover;
  private Mutation mutation;
  private Selection selection;

  /** Constructor */
  public FastPGASettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    maxPopulationSize = 100 ;
    initialPopulationSize = 100 ;
    maxEvaluations = 25000 ;
    a = 20.0 ;
    b = 1.0 ;
    c = 20.0 ;
    d = 0.0 ;
    termination = 1 ;
  }

  /** Configure FastPGA with default parameter settings */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    crossover = new SBXCrossover.Builder()
            .setProbability(0.9)
            .setDistributionIndex(20.0)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfVariables())
            .setDistributionIndex(20.0)
            .build() ;

    selection = new BinaryTournament.Builder()
            .setComparator(new FPGAFitnessComparator())
            .build() ;

    algorithm = new FastPGA.Builder(problem)
            .setMaxPopulationSize(maxPopulationSize)
            .setInitialPopulationSize(initialPopulationSize)
            .setMaxEvaluations(maxEvaluations)
            .setA(a)
            .setB(b)
            .setC(c)
            .setD(d)
            .setTermination(termination)
            .setSelection(selection)
            .setCrossover(crossover)
            .setMutation(mutation)
            .build() ;

    return algorithm;
  }

  /** Configure FastPGA from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    maxPopulationSize = Integer
            .parseInt(configuration.getProperty("maxPopulationSize", String.valueOf(maxPopulationSize)));
    initialPopulationSize = Integer
            .parseInt(configuration.getProperty("initialPopulationSize", String.valueOf(initialPopulationSize)));
    maxEvaluations = Integer
            .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));

    a = Double.parseDouble(configuration.getProperty("a", String.valueOf(a)));
    b = Double.parseDouble(configuration.getProperty("b", String.valueOf(b)));
    c = Double.parseDouble(configuration.getProperty("c", String.valueOf(c)));
    d = Double.parseDouble(configuration.getProperty("d", String.valueOf(d)));

    termination = Integer.parseInt(configuration.getProperty("termination", String.valueOf(termination)));

    return configure();
  }
} 
