package org.uma.jmetal.experiment;

import org.uma.jmetal.experiment.experimentoutput.*;
import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

/**
 * This class reproduces a study carried out in this paper:
 *  A.J. Nebro, J.J. Durillo, F. Luna, B. Dorronsoro, E. Alba
 * "Design Issues in a Multiobjective Cellular Genetic Algorithm."
 * Evolutionary Multi-Criterion Optimization. 4th International Conference,
 * EMO 2007. Sendai/Matsushima, Japan, March 2007.
 */
public class MOCellExperimentRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    ExperimentData experimentData = new ExperimentData.Builder("MOCellStudy")
      .setAlgorithmNameList(
        new String[] {"sMOCell1", "sMOCell2", "aMOCell1", "aMOCell2", "aMOCell3", "aMOCell4"})
      .setProblemList(new String[] {"ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"})
      .setExperimentBaseDirectory("/Users/antelverde/Softw/jMetal/jMetalGitHub/pruebas")
      .setOutputParetoFrontFileName("FUN")
      .setOutputParetoSetFileName("VAR")
      .setIndependentRuns(30)
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
