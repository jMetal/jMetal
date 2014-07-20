package org.uma.jmetal.runner.experiments;

import org.uma.jmetal.experiment.Experiment2;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.result.AlgorithmExecution;
import org.uma.jmetal.experiment.result.ParetoFrontsGeneration;
import org.uma.jmetal.experiment.result.QualityIndicatorGeneration;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

public class NewExperiment {
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
      .qualityIndicatorList(new String[]{"HV", "IGD", "Epsilon", "Spread"})
      .build() ;

    Experiment2 experiment = new Experiment2.Builder(experimentData)
      .addResultObject(algorithmExecution)
      .addResultObject(paretoFrontsGeneration)
      .addResultObject(qualityIndicatorGeneration)
      .build() ;

    experiment.run();
  }
}
