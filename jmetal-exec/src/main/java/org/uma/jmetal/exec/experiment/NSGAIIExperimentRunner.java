//  NSGAIIExperimentRunner.java
//
//  Author:
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

package org.uma.jmetal.exec.experiment;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.experimentoutput.*;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

/**
 * The class defines a jMetal experiment with the following features
 * - Algorithms: four variants of NSGA-II (each of them having a different crossover setProbability
 * - The zdt suite is solved
 * - The reference Pareto fronts are unknown and have to be computed
 * - The algorithm variant settings are read from properties files by using this method in the
 *   AlgorithmExecution object:
 *       .setUseAlgorithmConfigurationFiles()
 */
public class NSGAIIExperimentRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    ExperimentData experimentData = new ExperimentData.Builder("NSGAIIStudy")
      .setAlgorithmNameList(new String[] {"NSGAII", "NSGAIIb", "NSGAIIc", "NSGAIId"})
      .setProblemList(new String[] {"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"})
      .setExperimentBaseDirectory("/Users/antelverde/Softw/jMetal/jMetalGitHub/pruebas")
      .setOutputParetoFrontFileName("FUN")
      .setOutputParetoSetFileName("VAR")
      .setIndependentRuns(4)
      .build() ;

    AlgorithmExecution algorithmExecution = new AlgorithmExecution.Builder(experimentData)
      .setNumberOfThreads(8)
      .setParetoSetFileName("VAR")
      .setParetoFrontFileName("FUN")
      .setUseAlgorithmConfigurationFiles()
      .build() ;

    ParetoFrontsGeneration paretoFrontsGeneration = new ParetoFrontsGeneration.Builder(experimentData)
      .build() ;

    String[] indicatorList = new String[]{"HV", "IGD", "EPSILON", "SPREAD", "GD"} ;
    QualityIndicatorGeneration qualityIndicatorGeneration = new QualityIndicatorGeneration.Builder(experimentData)
      .setParetoFrontDirectory("/Users/antelverde/Softw/pruebas/data/paretoFronts")
      .setParetoFrontFiles(new String[] {"ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf"})
      .setQualityIndicatorList(indicatorList)
      .build() ;

    SetCoverageTableGeneration setCoverageTables = new SetCoverageTableGeneration.Builder(experimentData)
      .build() ;

    BoxplotGeneration boxplotGeneration = new BoxplotGeneration.Builder(experimentData)
      .setIndicatorList(indicatorList)
      .setNumberOfRows(3)
      .setNumberOfColumns(2)
      .build() ;

    WilcoxonTestTableGeneration wilcoxonTestTableGeneration =
      new WilcoxonTestTableGeneration.Builder(experimentData)
        .setIndicatorList(indicatorList)
        .build() ;

    QualityIndicatorLatexTableGeneration qualityIndicatorLatexTableGeneration =
      new QualityIndicatorLatexTableGeneration.Builder(experimentData)
        .setIndicatorList(indicatorList)
        .build() ;

    FriedmanTableGeneration friedmanTableGeneration = new FriedmanTableGeneration.Builder(experimentData)
      .setIndicatorList(indicatorList)
      .build() ;


    Experiment experiment = new Experiment.Builder(experimentData)
      .addExperimentOutput(algorithmExecution)
      .addExperimentOutput(paretoFrontsGeneration)
      .addExperimentOutput(qualityIndicatorGeneration)
      .addExperimentOutput(setCoverageTables)
      .addExperimentOutput(boxplotGeneration)
      .addExperimentOutput(wilcoxonTestTableGeneration)
      .addExperimentOutput(qualityIndicatorLatexTableGeneration)
      .addExperimentOutput(friedmanTableGeneration)
      .build() ;

    experiment.run();
  }
}
