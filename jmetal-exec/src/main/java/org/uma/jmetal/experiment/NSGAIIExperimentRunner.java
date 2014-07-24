package org.uma.jmetal.experiment;

import org.uma.jmetal.experiment.experimentoutput.*;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 24/07/14.
 */
public class NSGAIIExperimentRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    ExperimentData experimentData = new ExperimentData.Builder("NSGAIIStudy")
      .algorithmNameList(new String[]{"NSGAII", "NSGAIIb", "NSGAIIc", "NSGAIId"})
      .problemList(new String[]{"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"})
      .experimentBaseDirectory("/Users/antelverde/Softw/jMetal/jMetalGitHub/pruebas")
      .outputParetoFrontFileName("FUN")
      .outputParetoSetFileName("VAR")
      .independentRuns(4)
      .build() ;

    AlgorithmExecution algorithmExecution = new AlgorithmExecution.Builder(experimentData)
      .numberOfThreads(8)
      .paretoSetFileName("VAR")
      .paretoFrontFileName("FUN")
      .useAlgorithmConfigurationFiles()
      .build() ;

    ParetoFrontsGeneration paretoFrontsGeneration = new ParetoFrontsGeneration.Builder(experimentData)
      .build() ;

    String[] indicatorList = new String[]{"HV", "IGD", "EPSILON", "SPREAD", "GD"} ;
    QualityIndicatorGeneration qualityIndicatorGeneration = new QualityIndicatorGeneration.Builder(experimentData)
      .paretoFrontDirectory("/Users/antelverde/Softw/pruebas/data/paretoFronts")
      .paretoFrontFiles(new String[]{"ZDT1.pf","ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf"})
      .qualityIndicatorList(indicatorList)
      .build() ;

    SetCoverageTableGeneration setCoverageTables = new SetCoverageTableGeneration.Builder(experimentData)
      .build() ;

    BoxplotGeneration boxplotGeneration = new BoxplotGeneration.Builder(experimentData)
      .indicatorList(indicatorList)
      .numberOfRows(2)
      .numberOfColumns(2)
      .build() ;

    WilcoxonTestTableGeneration wilcoxonTestTableGeneration =
      new WilcoxonTestTableGeneration.Builder(experimentData)
        .indicatorList(indicatorList)
        .build() ;

    QualityIndicatorLatexTableGeneration qualityIndicatorLatexTableGeneration =
      new QualityIndicatorLatexTableGeneration.Builder(experimentData)
        .indicatorList(indicatorList)
        .build() ;

    FriedmanTableGeneration friedmanTableGeneration = new FriedmanTableGeneration.Builder(experimentData)
      .indicatorList(indicatorList)
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
