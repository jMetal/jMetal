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
import java.util.List;

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
public class GenerateWilcoxonTestTablesWithR<Result> implements ExperimentComponent {
  private static final String DEFAULT_R_DIRECTORY = "R";

  private final ExperimentConfiguration<?, Result> configuration;

  public GenerateWilcoxonTestTablesWithR(ExperimentConfiguration<?, Result> experimentConfiguration) {
    this.configuration = experimentConfiguration;
    this.configuration.removeDuplicatedAlgorithms();

    configuration.removeDuplicatedAlgorithms();
  }

  @Override
  public void run() throws IOException {
    String rDirectoryName = configuration.getExperimentBaseDirectory() + "/" + DEFAULT_R_DIRECTORY;
    File rOutput;
    rOutput = new File(rDirectoryName);
    if (!rOutput.exists()) {
      boolean result = new File(rDirectoryName).mkdirs();
      System.out.println("Creating " + rDirectoryName + " directory");
    }
    for (GenericIndicator<List<? extends Solution<?>>> indicator : configuration.getIndicatorList()) {
      String rFileName = rDirectoryName + "/" + indicator.getName() + ".Wilcoxon" + ".R";
      String latexFileName = rDirectoryName + "/" + indicator.getName() + ".Wilcoxon" + ".tex";

      printHeaderLatexCommands(rFileName, latexFileName);
      printTable(indicator, rFileName, latexFileName);
      printEndLatexCommands(rFileName, latexFileName);
    }
  }

  private void printHeaderLatexCommands(String rFileName, String latexFileName) throws IOException {
    FileWriter os = new FileWriter(rFileName, false);
    String output = "write(\"\", \"" + latexFileName + "\",append=FALSE)";
    os.write(output + "\n");

    String dataDirectory = configuration.getExperimentBaseDirectory() + "/data";
    os.write("resultDirectory<-\"" + dataDirectory + "\"" + "\n");
    output = "latexHeader <- function() {" + "\n" +
        "  write(\"\\\\documentclass{article}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\title{StandardStudy}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\usepackage{amssymb}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\author{A.J.Nebro}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\begin{document}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\maketitle\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\section{Tables}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\\", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
    os.write(output + "\n");

    os.close();
  }

  private void printEndLatexCommands(String rFileName, String latexFileName) throws IOException {
    FileWriter os = new FileWriter(rFileName, true);
    os.write("\\end{document}" + "\n");
    os.close();
  }

  private void printTable(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
    printTableHeader(indicator, rFileName, latexFileName);
    //printLines(indicator, rFileName, latexFileName);
    printTableTail(rFileName, latexFileName);
  }

  private void printTableHeader(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
    FileWriter os = new FileWriter(rFileName, true);

    String caption = indicator.getName() + ". Problems: ";
    for (Problem<?> problem : configuration.getProblemList()) {
      caption += problem.getName() + " ";
    }

    os.write("\n");
    os.write("\\begin{table}" + "\n");
    os.write("\\caption{" + caption + "}" + "\n");
    os.write("\\label{table: " + indicator.getName() + "}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\begin{tabular}{l");

    String latexTableLabel = "";
    String latexTabularAlignment = "";
    String latexTableFirstLine = "";
    String latexTableCaption = "";

    // Write function latexTableHeader
    latexTableCaption = "  write(\"\\\\caption{\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(problem, \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"." + indicator.getName() + ".}\", \"" + latexFileName + "\", append=TRUE)" + "\n";
    latexTableLabel = "  write(\"\\\\label{Table:\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(problem, \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"." + indicator.getName() + ".}\", \"" + latexFileName + "\", append=TRUE)" + "\n";
    latexTabularAlignment = "l";
    latexTableFirstLine = "\\\\hline ";

    for (int i = 1; i < configuration.getAlgorithmList().size(); i++) {
      latexTabularAlignment += "c";
      latexTableFirstLine += " & " + configuration.getAlgorithmList().get(i).getTag();
    }
    latexTableFirstLine += "\\\\\\\\ \"";

    // Generate function latexTableHeader()
    String output = "latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {" + "\n" +
        "  write(\"\\\\begin{table}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        latexTableCaption + "\n" +
        latexTableLabel + "\n" +
        "  write(\"\\\\centering\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\begin{scriptsize}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        //"  write(\"\\\\begin{tabular}{" + latexTabularAlignment + "}\", \"" + texFile + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\begin{tabular}{\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(tabularString, \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        //latexTableFirstLine +
        "  write(latexTableFirstLine, \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\hline \", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
    os.write(output + "\n");
    os.close();
  }

  private void printTableTail(String rFileName, String latexFileName) throws IOException {
    // Generate function latexTableTail()
    FileWriter os = new FileWriter(rFileName, true);

    String output = "latexTableTail <- function() { " + "\n" +
        "  write(\"\\\\hline\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\end{tabular}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\end{scriptsize}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\end{table}\", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
    os.write(output + "\n");
  }
}
