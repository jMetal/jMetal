package org.uma.jmetal.util.experiment.component;

import org.apache.commons.lang3.StringUtils;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class generates a R script that computes the Wilcoxon Signed Rank Test and generates a Latex script
 * that produces a table per quality indicator containing the pairwise comparison between all the algorithms
 * on all the solved problems.
 *
 * The results are a set of R files that are written in the directory
 * {@link Experiment #getExperimentBaseDirectory()}/R. Each file is called as
 * indicatorName.Wilcoxon.R
 *
 * To run the R script: Rscript indicatorName.Wilcoxon.R
 * To generate the resulting Latex file: pdflatex indicatorName.Wilcoxon.tex
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateWilcoxonTestTablesWithR<Result> implements ExperimentComponent {
  private static final String DEFAULT_R_DIRECTORY = "R";
  private static final String APPEND_STRING = "\", append=TRUE)\n";
  private static final String END_JUMP_STRING = "}\n";

  private final Experiment<?, Result> experiment;

  public GenerateWilcoxonTestTablesWithR(Experiment<?, Result> experimentConfiguration) {
    this.experiment = experimentConfiguration;

    experiment.removeDuplicatedAlgorithms();
  }

  @Override
  public void run() throws IOException {
    String rDirectoryName = experiment.getExperimentBaseDirectory() + "/" + DEFAULT_R_DIRECTORY;
    File rOutput;
    rOutput = new File(rDirectoryName);
    if (!rOutput.exists()) {
      new File(rDirectoryName).mkdirs();
      System.out.println("Creating " + rDirectoryName + " directory");
    }
    for (GenericIndicator<? extends Solution<?>> indicator : experiment.getIndicatorList()) {
      String rFileName = rDirectoryName + "/" + indicator.getName() + ".Wilcoxon" + ".R";
      String latexFileName = rDirectoryName + "/" + indicator.getName() + ".Wilcoxon" + ".tex";

      printHeaderLatexCommands(rFileName, latexFileName);
      printTableHeader(indicator, rFileName, latexFileName);
      printLines(indicator, rFileName, latexFileName);
      printTableTail(rFileName, latexFileName);
      printEndLatexCommands(rFileName, latexFileName);

      printGenerateMainScript(indicator, rFileName, latexFileName) ;
    }
  }

  private void printHeaderLatexCommands(String rFileName, String latexFileName) throws IOException {
    try(FileWriter os = new FileWriter(rFileName, false)){
    String output = "write(\"\", \"" + latexFileName + "\",append=FALSE)";
    os.write(output + "\n");

    String dataDirectory = experiment.getExperimentBaseDirectory() + "/data";
    os.write("resultDirectory<-\"" + dataDirectory + "\"" + "\n");
    output = "latexHeader <- function() {" + "\n" +
        "  write(\"\\\\documentclass{article}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\title{StandardStudy}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\usepackage{amssymb}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\author{A.J.Nebro}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\begin{document}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\maketitle\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\section{Tables}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\\", \"" + latexFileName + APPEND_STRING + "}" + "\n";
    os.write(output + "\n");

    }
  }

  private void printEndLatexCommands(String rFileName, String latexFileName) throws IOException {
    try(FileWriter os = new FileWriter(rFileName, true)){
    String output = "latexTail <- function() { " + "\n" +
        "  write(\"\\\\end{document}\", \"" + latexFileName + APPEND_STRING + "}" + "\n";
    os.write(output + "\n");
    }
  }

  private void printTableHeader(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
    try(FileWriter os = new FileWriter(rFileName, true)){

    String latexTableLabel = "";
    String latexTableCaption = "";

    // Write function latexTableHeader
    latexTableCaption = "  write(\"\\\\caption{\", \"" + latexFileName + APPEND_STRING +
        "  write(problem, \"" + latexFileName + APPEND_STRING +
        "  write(\"." + indicator.getName() + ".}\", \"" + latexFileName + APPEND_STRING;
    latexTableLabel = "  write(\"\\\\label{Table:\", \"" + latexFileName + APPEND_STRING +
        "  write(problem, \"" + latexFileName + "\", append=TRUE)" + "\n" +
        "  write(\"." + indicator.getName() + ".}\", \"" + latexFileName + APPEND_STRING;

    // Generate function latexTableHeader()
    String output = "latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {" + "\n" +
        "  write(\"\\\\begin{table}\", \"" + latexFileName + APPEND_STRING +
        latexTableCaption + "\n" +
        latexTableLabel + "\n" +
        "  write(\"\\\\centering\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\begin{scriptsize}\", \"" + latexFileName + APPEND_STRING +
        //"  write(\"\\\\begin{tabular}{" + latexTabularAlignment + "}\", \"" + texFile + "\", append=TRUE)" + "\n" +
        "  write(\"\\\\begin{tabular}{\", \"" + latexFileName + APPEND_STRING +
        "  write(tabularString, \"" + latexFileName + APPEND_STRING +
        "  write(\"}\", \"" + latexFileName + APPEND_STRING +
        //latexTableFirstLine +
        "  write(latexTableFirstLine, \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\hline \", \"" + latexFileName + APPEND_STRING + "}" + "\n";
    os.write(output + "\n");
    }
  }

  private void printTableTail(String rFileName, String latexFileName) throws IOException {
    // Generate function latexTableTail()
    try(FileWriter os = new FileWriter(rFileName, true)){

    String output = "latexTableTail <- function() { " + "\n" +
        "  write(\"\\\\hline\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\end{tabular}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\end{scriptsize}\", \"" + latexFileName + APPEND_STRING +
        "  write(\"\\\\end{table}\", \"" + latexFileName + APPEND_STRING + "}" + "\n";
    os.write(output + "\n");

    }
  }

  private void printLines(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
    try(FileWriter os = new FileWriter(rFileName, true)){

    String output ;
    if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
      output = "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n" +
          "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
          "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
          "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
          "  data1<-scan(file1)" + "\n" +
          "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
          "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
          "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
          "  data2<-scan(file2)" + "\n" +
          "  if (i == j) {" + "\n" +
          "    write(\"-- \", \"" + latexFileName + APPEND_STRING +
          "  }" + "\n" +
          "  else if (i < j) {" + "\n" +
          "    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
          "      if (median(data1) <= median(data2)) {" + "\n" +
          "        write(\"$\\\\blacktriangle$\", \"" + latexFileName + APPEND_STRING +
          END_JUMP_STRING +
          "      else {" + "\n" +
          "        write(\"$\\\\triangledown$\", \"" + latexFileName + APPEND_STRING +
          END_JUMP_STRING +
          "    }\n" +
          "    else {" + "\n" +
          "      write(\"--\", \"" + latexFileName + APPEND_STRING +
          "    }\n" +
          "  }" + "\n" +
          "  else {" + "\n" +
          "    write(\" \", \"" + latexFileName + APPEND_STRING +
          "  }" + "\n" +
          "}" + "\n";

    } else {
      output = "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n" +
          "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
          "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
          "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
          "  data1<-scan(file1)" + "\n" +
          "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
          "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
          "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
          "  data2<-scan(file2)" + "\n" +
          "  if (i == j) {" + "\n" +
          "    write(\"--\", \"" + latexFileName + APPEND_STRING +
          "  }" + "\n" +
          "  else if (i < j) {" + "\n" +
          "    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
          "      if (median(data1) >= median(data2)) {" + "\n" +
          "        write(\"$\\\\blacktriangle$\", \"" + latexFileName + APPEND_STRING +
          END_JUMP_STRING +
          "      else {" + "\n" +
          "        write(\"$\\\\triangledown$\", \"" + latexFileName + APPEND_STRING +
          END_JUMP_STRING +
          END_JUMP_STRING +
          "    else {" + "\n" +
          "      write(\"$-$\", \"" + latexFileName + APPEND_STRING +
          END_JUMP_STRING +
          "  }" + "\n" +
          "  else {" + "\n" +
          "    write(\" \", \"" + latexFileName + APPEND_STRING +
          "  }" + "\n" +
          "}" + "\n";
      }
      os.write(output + "\n");
    }
  }

  private void printGenerateMainScript(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
    try(FileWriter os = new FileWriter(rFileName, true)){

    // Start of the R script
    String output = "### START OF SCRIPT ";
    os.write(output + "\n");

    String problemList = "problemList <-c(";
    String algorithmList = "algorithmList <-c(";

    for (int i = 0; i < (experiment.getProblemList().size() - 1); i++) {
      problemList += "\"" + experiment.getProblemList().get(i).getTag() + "\", ";
    }
    problemList += "\"" + experiment.getProblemList().get(experiment.getProblemList().size() - 1).getTag() + "\") ";

    for (int i = 0; i < (experiment.getAlgorithmList().size() - 1); i++) {
      algorithmList += "\"" + experiment.getAlgorithmList().get(i).getAlgorithmTag() + "\", ";
    }
    algorithmList += "\"" + experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getAlgorithmTag() + "\") ";

    String latexTabularAlignment = "l";
    for (int i = 1; i < experiment.getAlgorithmList().size(); i++) {
      latexTabularAlignment += "c";
    }

    latexTabularAlignment = "l";
    String latexTableFirstLine = "\\\\hline ";

    for (int i = 1; i < experiment.getAlgorithmList().size(); i++) {
      latexTabularAlignment += "c";
      latexTableFirstLine += " & " + experiment.getAlgorithmList().get(i).getAlgorithmTag();
    }
    latexTableFirstLine += "\\\\\\\\ \"";

    String tabularString = "tabularString <-c(" + "\""+ latexTabularAlignment + "\""+ ") " ;
    String tableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + ") " ;

    output = "# Constants" + "\n" +
        problemList + "\n" +
        algorithmList + "\n" +
        tabularString + "\n" +
        tableFirstLine + "\n" +
        "indicator<-\"" + indicator.getName() + "\"";
    os.write(output + "\n");

    output = "\n # Step 1.  Writes the latex header" + "\n" +
        "latexHeader()";
    os.write(output + "\n");

    // Generate full table
    problemList = "";
    for (ExperimentProblem<?> problem : experiment.getProblemList()) {
      problemList += problem.getTag() + " ";
    }
    // The tabular environment and the latexTableFirstLine encodings.variable must be redefined
    latexTabularAlignment = "| l | ";
    latexTableFirstLine = "\\\\hline \\\\multicolumn{1}{|c|}{}";
    for (int i = 1; i < experiment.getAlgorithmList().size(); i++) {
      latexTabularAlignment += StringUtils.repeat("p{0.15cm }", experiment.getProblemList().size());
      latexTableFirstLine += " & \\\\multicolumn{" + experiment.getProblemList().size() + "}{c|}{" + experiment.getAlgorithmList().get(i).getAlgorithmTag()+"}";
      latexTabularAlignment += " | " ;
    }
    latexTableFirstLine += " \\\\\\\\";

    tabularString = "tabularString <-c(" + "\""+ latexTabularAlignment + "\""+ ") " ;
    latexTableFirstLine = "latexTableFirstLine <-c(" + "\""+ latexTableFirstLine + "\""+ ") " ;

    output = tabularString;
    os.write(output + "\n" + "\n");
    output = latexTableFirstLine ;
    os.write(output + "\n" + "\n");

    output = "# Step 3. Problem loop " + "\n" +
        "latexTableHeader(\"" + problemList + "\", tabularString, latexTableFirstLine)" + "\n\n" +
        "indx = 0" + "\n" +
        "for (i in algorithmList) {" + "\n" +
        "  if (i != \"" +  experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getAlgorithmTag()+ "\") {" + "\n" +
        "    write(i , \"" + latexFileName + APPEND_STRING +
        "    write(\" & \", \"" + latexFileName + APPEND_STRING + "\n" +
        "    jndx = 0" + "\n" +
        "    for (j in algorithmList) {" + "\n" +
        "      for (problem in problemList) {" + "\n" +
        "        if (jndx != 0) {" + "\n" +
        "          if (i != j) {" + "\n" +
        "            printTableLine(indicator, i, j, indx, jndx, problem)" + "\n" +
        "          }" + "\n" +
        "          else {" + "\n" +
        "            write(\"  \", \"" + latexFileName + APPEND_STRING +
        "          } " + "\n" +
        "          if (problem == \"" + experiment.getProblemList().get(experiment.getProblemList().size()- 1).getTag() + "\") {" + "\n" +
        "            if (j == \"" + experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getAlgorithmTag() + "\") {" + "\n" +
        "              write(\" \\\\\\\\ \", \"" + latexFileName + APPEND_STRING +
        "            } " + "\n" +
        "            else {" + "\n" +
        "              write(\" & \", \"" + latexFileName + APPEND_STRING +
        "            }" + "\n" +
        "          }" + "\n" +
        "     else {" + "\n" +
        "    write(\"&\", \"" + latexFileName + APPEND_STRING +
        "     }" + "\n" +
        "        }" + "\n" +
        "      }" + "\n" +
        "      jndx = jndx + 1" + "\n" +
        END_JUMP_STRING +
        "    indx = indx + 1" + "\n" +
        "  }" + "\n" +
        "} # for algorithm" + "\n" + "\n" +
        "  latexTableTail()" + "\n";

    os.write(output + "\n");

    // Generate end of file
    output = "#Step 3. Writes the end of latex file " + "\n" +
        "latexTail()" + "\n";
    os.write(output + "\n");

    }
  }
}