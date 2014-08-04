package org.uma.jmetal.experiment.experimentoutput;

import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentOutput;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 23/07/14.
 */
public class WilcoxonTestTableGeneration implements ExperimentOutput {
  private ExperimentData experimentData ;
  private String[] indicatorList ;
  private String outputDirectory ;

  /** Constructor */
  private WilcoxonTestTableGeneration(Builder builder) {
    experimentData = builder.experimentData ;
    indicatorList = builder.indicatorList ;
    outputDirectory = builder.outputDirectory ;

    if (outputDirectoryDoesNotExist()) {
      createOutputDirectory() ;
    }
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData ;
    private String[] indicatorList ;
    private String outputDirectory ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
      this.indicatorList = null ;
      outputDirectory = experimentData.getExperimentBaseDirectory()+"/R" ;
    }

    public Builder indicatorList(String[] indicatorList) {
      this.indicatorList = Arrays.copyOf(indicatorList, indicatorList.length) ;

      return this ;
    }

    public Builder outputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;

      return this;
    }

    public WilcoxonTestTableGeneration build() {
      return new WilcoxonTestTableGeneration(this) ;
    }
  }

  @Override public void generate() {
    try {
      for (int indicator = 0; indicator < indicatorList.length; indicator++) {
        JMetalLogger.logger.info("Indicator: " + indicatorList[indicator]);
        String rFile =
          outputDirectory + "/" + indicatorList[indicator] + ".Wilcoxon.R";
        String texFile =
          outputDirectory + "/" + indicatorList[indicator] + ".Wilcoxon.tex";

        FileWriter os = new FileWriter(rFile, false);
        String output = "write(\"\", \"" + texFile + "\",append=FALSE)";
        os.write(output + "\n");

        // Generate function latexHeader()
        String dataDirectory = experimentData.getExperimentBaseDirectory() + "/data";
        os.write("resultDirectory<-\"" + dataDirectory + "\"" + "\n");
        output = "latexHeader <- function() {" + "\n" +
          "  write(\"\\\\documentclass{article}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\title{StandardStudy}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\usepackage{amssymb}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\usepackage[margin=0.6in]{geometry}\", \"" + texFile + "\", append=TRUE)"
          + "\n" +
          "  write(\"\\\\author{A.J.Nebro}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\begin{document}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\maketitle\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\section{Tables}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\\", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");

        // Write function latexTableHeader
        String latexTableLabel = "";
        String latexTabularAlignment = "";
        String latexTableFirstLine = "";
        String latexTableCaption = "";

        latexTableCaption = "  write(\"\\\\caption{\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(problem, \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"." + indicatorList[indicator] + ".}\", \"" + texFile
          + "\", append=TRUE)" + "\n";
        latexTableLabel = "  write(\"\\\\label{Table:\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(problem, \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"." + indicatorList[indicator] + ".}\", \"" + texFile
          + "\", append=TRUE)" + "\n";
        latexTabularAlignment = "l";
        latexTableFirstLine = "\\\\hline ";

        for (int i = 1; i < experimentData.getAlgorithmNameList().length; i++) {
          latexTabularAlignment += "c";
          latexTableFirstLine += " & " + experimentData.getAlgorithmNameList()[i];
        }
        latexTableFirstLine += "\\\\\\\\ \"";

        // Generate function latexTableHeader()
        output =
          "latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {" + "\n" +
            "  write(\"\\\\begin{table}\", \"" + texFile + "\", append=TRUE)" + "\n" +
            latexTableCaption + "\n" +
            latexTableLabel + "\n" +
            "  write(\"\\\\centering\", \"" + texFile + "\", append=TRUE)" + "\n" +
            "  write(\"\\\\begin{scriptsize}\", \"" + texFile + "\", append=TRUE)" + "\n" +
            //"  write(\"\\\\begin{tabular}{" + latexTabularAlignment + "}\", \"" + texFile + "\", append=TRUE)" + "\n" +
            "  write(\"\\\\begin{tabular}{\", \"" + texFile + "\", append=TRUE)" + "\n" +
            "  write(tabularString, \"" + texFile + "\", append=TRUE)" + "\n" +
            "  write(\"}\", \"" + texFile + "\", append=TRUE)" + "\n" +
            //latexTableFirstLine +
            "  write(latexTableFirstLine, \"" + texFile + "\", append=TRUE)" + "\n" +
            "  write(\"\\\\hline \", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");

        // Generate function latexTableTail()
        output = "latexTableTail <- function() { " + "\n" +
          "  write(\"\\\\hline\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\end{tabular}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\end{scriptsize}\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "  write(\"\\\\end{table}\", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");

        // Generate function latexTail()
        output = "latexTail <- function() { " + "\n" +
          "  write(\"\\\\end{document}\", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");

        if (!indicatorList[indicator].equals("HV")) {
          output =
            "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n"
              +
              "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
              "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
              "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
              "  data1<-scan(file1)" + "\n" +
              "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
              "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
              "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
              "  data2<-scan(file2)" + "\n" +
              "  if (i == j) {" + "\n" +
              "    write(\"-- \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  }" + "\n" +
              "  else if (i < j) {" + "\n" +
              "    if (wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
              "      if (median(data1) <= median(data2)) {" + "\n" +
              "        write(\"$\\\\blacktriangle$\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "      }" + "\n" +
              "      else {" + "\n" +
              "        write(\"$\\\\triangledown$\", \"" + texFile + "\", append=TRUE) " + "\n" +
              "      }" + "\n" +
              "    }" + "\n" +
              "    else {" + "\n" +
              "      write(\"--\", \"" + texFile + "\", append=TRUE) " + "\n" +
              "    }" + "\n" +
              "  }" + "\n" +
              "  else {" + "\n" +
              "    write(\" \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  }" + "\n" +
              "}" + "\n";
        } else {
          output =
            "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n"
              +
              "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
              "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
              "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
              "  data1<-scan(file1)" + "\n" +
              "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
              "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
              "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
              "  data2<-scan(file2)" + "\n" +
              "  if (i == j) {" + "\n" +
              "    write(\"--\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  }" + "\n" +
              "  else if (i < j) {" + "\n" +
              "    if (wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
              "      if (median(data1) >= median(data2)) {" + "\n" +
              "        write(\"$\\\\blacktriangle$\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "      }" + "\n" +
              "      else {" + "\n" +
              "        write(\"$\\\\triangledown$\", \"" + texFile + "\", append=TRUE) " + "\n" +
              "      }" + "\n" +
              "    }" + "\n" +
              "    else {" + "\n" +
              "      write(\"--\", \"" + texFile + "\", append=TRUE) " + "\n" +
              "    }" + "\n" +
              "  }" + "\n" +
              "  else {" + "\n" +
              "    write(\" \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  }" + "\n" +
              "}" + "\n";
        }
        os.write(output + "\n");

        // Start of the R script
        output = "### START OF SCRIPT ";
        os.write(output + "\n");

        String problemList = "problemList <-c(";
        String algorithmList = "algorithmList <-c(";

        for (int i = 0; i < (experimentData.getProblemList().length - 1); i++) {
          problemList += "\"" + experimentData.getProblemList()[i] + "\", ";
        }
        problemList +=
          "\"" + experimentData.getProblemList()[experimentData.getProblemList().length - 1] + "\") ";

        for (int i = 0; i < (experimentData.getAlgorithmNameList().length - 1); i++) {
          algorithmList += "\"" + experimentData.getAlgorithmNameList()[i] + "\", ";
        }
        algorithmList +=
          "\"" + experimentData.getAlgorithmNameList()[experimentData.getAlgorithmNameList().length - 1]
            + "\") ";

        latexTabularAlignment = "l";
        for (int i = 1; i < experimentData.getAlgorithmNameList().length; i++) {
          latexTabularAlignment += "c";
        } // for
        String tabularString = "tabularString <-c(" + "\"" + latexTabularAlignment + "\"" + ") ";
        String tableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + ") ";

        output = "# Constants" + "\n" +
          problemList + "\n" +
          algorithmList + "\n" +
          tabularString + "\n" +
          tableFirstLine + "\n" +
          "indicator<-\"" + indicatorList[indicator] + "\"";
        os.write(output + "\n");


        output = "\n # Step 1.  Writes the latex header" + "\n" +
          "latexHeader()";
        os.write(output + "\n");

        // Generate tables per problem
        output = "# Step 2. Problem loop " + "\n" +
          "for (problem in problemList) {" + "\n" +
          "  latexTableHeader(problem,  tabularString, latexTableFirstLine)" + "\n\n" +
          "  indx = 0" + "\n" +
          "  for (i in algorithmList) {" + "\n" +
          "    if (i != \"" + experimentData.getAlgorithmNameList()[
          experimentData.getAlgorithmNameList().length - 1] + "\") {" + "\n" +
          "      write(i , \"" + texFile + "\", append=TRUE)" + "\n" +
          "      write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "      jndx = 0 " + "\n" +
          "      for (j in algorithmList) {" + "\n" +
          "        if (jndx != 0) {" + "\n" +
          "          if (indx != jndx) {" + "\n" +
          "            printTableLine(indicator, i, j, indx, jndx, problem)" + "\n" +
          "          }" + "\n" +
          "          else {" + "\n" +
          "            write(\"  \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "          }" + "\n" +
          "          if (j != \"" + experimentData.getAlgorithmNameList()[
          experimentData.getAlgorithmNameList().length - 1] + "\") {" + "\n" +
          "            write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "          }" + "\n" +
          "          else {" + "\n" +
          "            write(\" \\\\\\\\ \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "          }" + "\n" +
          "        }" + "\n" +
          "        jndx = jndx + 1" + "\n" +
          "      }" + "\n" +
          "      indx = indx + 1" + "\n" +
          "    }" + "\n" +
          "  }" + "\n" + "\n" +
          "  latexTableTail()" + "\n" +
          "} # for problem" + "\n";
        os.write(output + "\n");

        // Generate full table
        problemList = "";
        for (String problem : experimentData.getProblemList()) {
          problemList += problem + " ";
        }
        // The tabular environment and the latexTableFirstLine encoding.variable must be redefined
        latexTabularAlignment = "| l | ";
        latexTableFirstLine = "\\\\hline \\\\multicolumn{1}{|c|}{}";
        for (int i = 1; i < experimentData.getAlgorithmNameList().length; i++) {
          for (String problem : experimentData.getProblemList()) {
            latexTabularAlignment += "p{0.15cm}  ";
            //latexTabularAlignment += "c ";
          } // for
          latexTableFirstLine +=
            " & \\\\multicolumn{" + experimentData.getProblemList().length + "}{c|}{" + experimentData
              .getAlgorithmNameList()[i] + "}";
          latexTabularAlignment += " | ";
        } // for
        latexTableFirstLine += " \\\\\\\\";

        tabularString = "tabularString <-c(" + "\"" + latexTabularAlignment + "\"" + ") ";
        latexTableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + "\"" + ") ";

        output = tabularString;
        os.write(output + "\n" + "\n");
        output = latexTableFirstLine;
        os.write(output + "\n" + "\n");

        output = "# Step 3. Problem loop " + "\n" +
          "latexTableHeader(\"" + problemList + "\", tabularString, latexTableFirstLine)" + "\n\n" +
          "indx = 0" + "\n" +
          "for (i in algorithmList) {" + "\n" +
          "  if (i != \"" + experimentData.getAlgorithmNameList()[
          experimentData.getAlgorithmNameList().length - 1] + "\") {" + "\n" +
          "    write(i , \"" + texFile + "\", append=TRUE)" + "\n" +
          "    write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" + "\n" +
          "    jndx = 0" + "\n" +
          "    for (j in algorithmList) {" + "\n" +
          "      for (problem in problemList) {" + "\n" +
          "        if (jndx != 0) {" + "\n" +
          "          if (i != j) {" + "\n" +
          "            printTableLine(indicator, i, j, indx, jndx, problem)" + "\n" +
          "          }" + "\n" +
          "          else {" + "\n" +
          "            write(\"  \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "          } " + "\n" +
          "          if (problem == \"" + experimentData.getProblemList()[
          experimentData.getProblemList().length - 1] + "\") {" + "\n" +
          "            if (j == \"" + experimentData.getAlgorithmNameList()[
          experimentData.getAlgorithmNameList().length - 1] + "\") {" + "\n" +
          "              write(\" \\\\\\\\ \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "            } " + "\n" +
          "            else {" + "\n" +
          "              write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" +
          "            }" + "\n" +
          "          }" + "\n" +
          "     else {" + "\n" +
          "    write(\"&\", \"" + texFile + "\", append=TRUE)" + "\n" +
          "     }" + "\n" +
          "        }" + "\n" +
          "      }" + "\n" +
          "      jndx = jndx + 1" + "\n" +
          "    }" + "\n" +
          "    indx = indx + 1" + "\n" +
          "  }" + "\n" +
          "} # for algorithm" + "\n" + "\n" +
          "  latexTableTail()" + "\n";

        os.write(output + "\n");

        // Generate end of file
        output = "#Step 3. Writes the end of latex file " + "\n" +
          "latexTail()" + "\n";
        os.write(output + "\n");


        os.close();
      }
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
  }

  private boolean outputDirectoryDoesNotExist() {
    boolean result;

    File directory = new File(outputDirectory);
    if (directory.exists() && directory.isDirectory()) {
      result = false;
    } else {
      result = true;
    }

    return result;
  }

  private void createOutputDirectory() {
    File directory = new File(outputDirectory);

    if (directory.exists()) {
      directory.delete() ;
    }

    boolean result ;
    result = new File(outputDirectory).mkdirs() ;
    if (!result) {
      throw new JMetalException("Error creating experiment directory: " + outputDirectory) ;
    }
  }
}
