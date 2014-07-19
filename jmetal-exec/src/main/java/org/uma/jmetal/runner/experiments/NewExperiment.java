package org.uma.jmetal.runner.experiments;

import org.uma.jmetal.experiment.Experiment2;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.result.AlgorithmExecution;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

public class NewExperiment {
  public static void main(String[] args) throws JMetalException, IOException {
    ExperimentData experimentData = new ExperimentData.Builder("Experiment2")
      .algorithmNameList(new String[]{"NSGAII", "SMPSO"})
      .problemList(new String[]{"ZDT1", "ZDT2", "ZDT3"})
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

    Experiment2 experiment = new Experiment2.Builder(experimentData)
      .addResultObject(algorithmExecution)
      .build() ;

    experiment.run();
  }
}
