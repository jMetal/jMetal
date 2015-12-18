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

package org.uma.jmetal.util.experiment.component;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.fileinput.util.ReadDoubleDataFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class computes the Wilcoxon Signed Rank Test and generates a Latex script that produces a table per
 * quality indicator containing the pairwise comparison between all the algorithms on all the solved
 * problems.
 *
 * The results are a set of Latex files that are written in the directory
 * {@link ExperimentConfiguration #getExperimentBaseDirectory()}/latex. Each file is called as
 * indicatorName.tex
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
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
    for (GenericIndicator<? extends Solution<?>> indicator : configuration.getIndicatorList()) {
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
      caption += problem.getName() + " " ;
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

      //DescriptiveStatistics stats = new DescriptiveStatistics() ;
      WilcoxonSignedRankTest test = new WilcoxonSignedRankTest();
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
          double[] dataAlgorithm1 = new ReadDoubleDataFile().readFile(dataFile1);
          double[] dataAlgorithm2 = new ReadDoubleDataFile().readFile(dataFile2);
          double pvalue = test.wilcoxonSignedRankTest(dataAlgorithm1, dataAlgorithm2, true);
/*
          double meanData1 ;
          for (double value : dataAlgorithm1) {
            stats.addValue(value);
          }
          meanData1 = stats.getPercentile(50) ;
          stats.clear();

          double meanData2 ;
          for (double value : dataAlgorithm1) {
            stats.addValue(value);
          }
          meanData2 = stats.getPercentile(50) ;
          */
          if (pvalue <= 0.05) {
            if (StatUtils.percentile(dataAlgorithm1, 50) >= StatUtils.percentile(dataAlgorithm2, 50)) {
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
