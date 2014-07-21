//  ExperimentRunner.java
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

package org.uma.jmetal.experiments;

import org.uma.jmetal.experiment.Experiment2;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.result.AlgorithmExecution;
import org.uma.jmetal.experiment.result.ParetoFrontsGeneration;
import org.uma.jmetal.experiment.result.QualityIndicatorGeneration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

public class ExperimentRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    ExperimentData experimentData = new ExperimentData.Builder("Experiment2")
      .algorithmNameList(new String[]{"NSGAII", "SMPSO"})
      .problemList(new String[]{"ZDT1", "ZDT2", "ZDT4"})
      .experimentBaseDirectory("/Users/antelverde/Softw/jMetal/jMetalGitHub/pruebas")
      .outputParetoFrontFileName("FUN")
      .outputParetoSetFileName("VAR")
      .independentRuns(2)
      .build() ;

    AlgorithmExecution algorithmExecution = new AlgorithmExecution.Builder(experimentData)
      .numberOfThreads(8)
      .paretoSetFileName("VAR")
      .paretoFrontFileName("FUN")
      //.useAlgorithmConfigurationFiles()
      .build() ;

    ParetoFrontsGeneration paretoFrontsGeneration = new ParetoFrontsGeneration.Builder(experimentData)
      .build() ;

    QualityIndicatorGeneration qualityIndicatorGeneration = new QualityIndicatorGeneration.Builder(experimentData)
      .paretoFrontDirectory("/Users/antelverde/Softw/pruebas/data/paretoFronts")
      .paretoFrontFiles(new String[]{"ZDT1.pf","ZDT2.pf", "ZDT4.pf"})
      .qualityIndicatorList(new String[]{"HV", "IGD", "EPSILON", "SPREAD", "GD"})
      .build() ;

    Experiment2 experiment = new Experiment2.Builder(experimentData)
      .addResultObject(algorithmExecution)
      .addResultObject(paretoFrontsGeneration)
      .addResultObject(qualityIndicatorGeneration)
      .build() ;

    experiment.run();
  }
}
