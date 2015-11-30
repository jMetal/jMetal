package org.uma.jmetal.util.experiment.component;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.component.util.ReadDoubleDataFile;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by ajnebro on 30/11/15.
 */
public class GenerateWilcoxonTestTables<Result> implements ExperimentComponent {
  private static final String DEFAULT_LATEX_DIRECTORY = "latex";

  private final ExperimentConfiguration<?, Result> configuration;

  public GenerateWilcoxonTestTables(ExperimentConfiguration<?, Result> experimentConfiguration) {
    this.configuration = experimentConfiguration;
    this.configuration.removeDuplicatedAlgorithms();

    configuration.removeDuplicatedAlgorithms();
  }

  @Override
  public void run() throws IOException {
    String latexDirectoryName = configuration.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;
    File latexOutput;
    latexOutput = new File(latexDirectoryName);
    if (!latexOutput.exists()) {
      boolean result = new File(latexDirectoryName).mkdirs();
      System.out.println("Creating " + latexDirectoryName + " directory");
    }
    for (GenericIndicator<List<? extends Solution<?>>> indicator : configuration.getIndicatorList()) {
      String latexFile = latexDirectoryName + "/" + "Wilcoxon_" + indicator.getName() + ".tex";

      printHeaderLatexCommands(latexFile);
      printTable(indicator, latexFile);
      printEndLatexCommands(latexFile);
    }
  }

  private void printHeaderLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, false);

    os.write("\\documentclass{article}" + "\n");
    os.write("\\title{" + configuration.getExperimentName() + "}" + "\n");
    os.write("\\usepackage{amssymb}" + "\n");
    os.write("\\author{A.J. Nebro}" + "\n");
    os.write("\\begin{document}" + "\n");
    os.write("\\maketitle" + "\n");
    os.write("\\section{Tables}" + "\n");

    os.close();
  }

  private void printEndLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\end{document}" + "\n");
    os.close();
  }

  private void printTable(GenericIndicator<?> indicator, String latexFile) throws IOException {
    printTableHeader(indicator, latexFile);
    printLines(indicator, latexFile);
    printTableTail(indicator, latexFile);
  }

  private void printTableHeader(GenericIndicator<?> indicator, String latexFile) throws IOException {
    FileWriter os = new FileWriter(latexFile, true);

    String caption = indicator.getName() + ". Problems: ";
    for (Problem<?> problem: configuration.getProblemList()) {
      caption += problem.getName() ;
    }

    os.write("\n");
    os.write("\\begin{table}" + "\n");
    os.write("\\caption{" + caption + "}" + "\n");
    os.write("\\label{table: " + indicator.getName() + "}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (int i = 0; i < configuration.getAlgorithmList().size()-1; i++) {
      os.write("l");
    }
    os.write("}\n");
    os.write("\\hline \n");

    // write table head
    for (int i = 0; i < configuration.getAlgorithmList().size(); i++) {
      if (i == 0) {
        os.write(" & ");
      } else if (i == (configuration.getAlgorithmList().size() - 1)) {
        os.write(" " + configuration.getAlgorithmList().get(i).getTag() + "\\\\" + "\n");
      } else {
        os.write(" " + configuration.getAlgorithmList().get(i).getTag() + " & ");
      }
    }
    os.write("\\hline \n");
    os.close();
  }

  private void printTableTail(GenericIndicator<?> indicator, String latexFile) throws IOException {
    FileWriter os = new FileWriter(latexFile, true);

    // close table
    os.write("\\hline" + "\n");
    os.write("\\end{tabular}" + "\n");
    os.write("\\end{scriptsize}" + "\n");
    os.write("\\end{table}" + "\n");
    os.close();
  }

  private void printLines(GenericIndicator<?> indicator, String latexFile) throws IOException {
    FileWriter os = new FileWriter(latexFile, true);

    for (int i = 0; i < configuration.getAlgorithmList().size()-1; i++) {
      os.write(" " + configuration.getAlgorithmList().get(i).getTag() + " & ");

      for (int k = 0; k < i; k++) {
        os.write(" & ");
      }

      for (int j = i + 1; j < configuration.getAlgorithmList().size(); j++) {
        for (Problem<?> problem : configuration.getProblemList()) {
          String dataFile1 = configuration.getExperimentBaseDirectory() + "/data/" +
              configuration.getAlgorithmList().get(i).getTag() + "/" +
              problem.getName() + "/" +
              indicator.getName();

          String dataFile2 = configuration.getExperimentBaseDirectory() + "/data/" +
              configuration.getAlgorithmList().get(j).getTag() + "/" +
              problem.getName() + "/" +
              indicator.getName();

          System.out.println(dataFile1);
          System.out.println(dataFile2);
          System.out.println();
          double[] dataAlgorithm1 = ReadDoubleDataFile.readFile(dataFile1);
          double[] dataAlgorithm2 = ReadDoubleDataFile.readFile(dataFile2);
          WilcoxonSignedRankTest test = new WilcoxonSignedRankTest();
          double pvalue = test.wilcoxonSignedRankTest(dataAlgorithm1, dataAlgorithm2, true);
          DescriptiveStatistics statsData1 = new DescriptiveStatistics();
          for (double value : dataAlgorithm1) {
            statsData1.addValue(value);
          }
          DescriptiveStatistics statsData2 = new DescriptiveStatistics();
          for (double value : dataAlgorithm2) {
            statsData2.addValue(value);
          }
          if (pvalue <= 0.05) {
            if (statsData1.getPercentile(50.0) >= statsData2.getPercentile(50.0)) {
              os.write("$\\blacktriangle$") ;
            } else {
              os.write("$\\blacktriangle$");
            }
          } else {
            os.write("$-$");
          }
        }
        if (j < configuration.getAlgorithmList().size()-1) {
          os.write(" & ");
        }
      }
      os.write("\\\\" + "\n") ;
    }

    os.close();
  }
}
