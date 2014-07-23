package org.uma.jmetal.experiment.experimentoutput;

import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentOutput;
import org.uma.jmetal.qualityIndicator.SetCoverage;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by Antonio J. Nebro on 22/07/14.
 */
public class SetCoverageTableGeneration implements ExperimentOutput {

  private ExperimentData experimentData ;
  private String outputDirectory ;
  private FileWriter fileWriter;
  private String outputFile ;

  /** Constructor */
  private SetCoverageTableGeneration(Builder builder) throws IOException {
    this.experimentData = builder.experimentData ;
    this.outputDirectory = builder.outputDirectory ;
    this.outputFile = builder.outputFile ;

    System.out.println("output directory: " + outputDirectory) ;

    if (outputDirectoryDoesNotExist()) {
      createOutputDirectory() ;
    }
    fileWriter = new FileWriter(outputDirectory+"/"+outputFile, false) ;
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData ;
    private String outputDirectory ;
    private String outputFile ;

    public Builder (ExperimentData experimentData) {
      this.experimentData = experimentData ;
      outputDirectory = experimentData.getExperimentBaseDirectory()+"/latex" ;
      outputFile = "SetCoverage.tex" ;
    }

    public Builder outputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory ;

      return this ;
    }

    public Builder outputFileName(String outputFileName) {
      this.outputFile = outputFileName ;

      return this ;
    }

    public SetCoverageTableGeneration build() throws IOException {
      return new SetCoverageTableGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    try {
      generateLatexHeader();
      for (String problem : experimentData.getProblemList()) {
        generateTable(problem);
      }
      generateLatexTail();

      fileWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error generating set coverage tables.", e) ;
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

  private void generateLatexHeader() throws IOException {
    fileWriter.write("\\documentclass{article}" + "\n");
    fileWriter.write("\\title{" + experimentData.getExperimentName() + "}" + "\n");
    fileWriter.write("\\usepackage{colortbl}" + "\n");
    fileWriter.write("\\usepackage[table*]{xcolor}" + "\n");
    fileWriter.write("\\usepackage[margin=0.6in]{geometry}" + "\n");
    fileWriter.write("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    fileWriter.write("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    fileWriter.write("\\author{}" + "\n");
    fileWriter.write("\\begin{document}" + "\n");
    fileWriter.write("\\maketitle" + "\n");
    fileWriter.write("\\section{Tables}" + "\n");
  }

  private void generateTable(String problem) throws IOException, JMetalException {
    writeTableHeader(problem);

    for (int i = 0 ; i < experimentData.getAlgorithmNameList().length; i++) {
      writeTableRow(i, problem) ;
    }

    writeTableTail() ;
  }

  private void generateLatexTail() throws IOException {
    fileWriter.write("\\end{document}" + "\n");
  }

  private void writeTableHeader(String problem) throws IOException {
    fileWriter.write("\n\n");
    fileWriter.write("\\begin{table}" + "\n");
    fileWriter.write("\\caption{" + problem + ". Set Coverage}" + "\n");
    fileWriter.write("\\label{table:setCoverage." + problem + "}" + "\n");
    fileWriter.write("\\centering" + "\n");
    fileWriter.write("\\begin{scriptsize}" + "\n");
    fileWriter.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (int i = 0 ; i < experimentData.getAlgorithmNameList().length; i++) {
      fileWriter.write("l");
    }
    fileWriter.write("}\n");

    // write table head
    for (int i = -1; i < experimentData.getAlgorithmNameList().length; i++) {
      if (i == -1) {
        fileWriter.write(" & ");
      } else if (i == (experimentData.getAlgorithmNameList().length - 1)) {
        fileWriter.write(" " + experimentData.getAlgorithmNameList()[i] + "\\\\" + "\n");
      } else {
        fileWriter.write("" + experimentData.getAlgorithmNameList()[i] + " & ");
      }
    }
    fileWriter.write("\\hline" + "\n");
  }

  private void writeTableRow(int algorithmIndex, String problem) throws IOException,
    JMetalException {
    fileWriter.write("" + experimentData.getAlgorithmNameList()[algorithmIndex] + " & ");
    for (int i = 0; i < experimentData.getAlgorithmNameList().length; i++) {
      if (i != algorithmIndex) {
        double setCoverageValueAB = calculateSetCoverage(
          problem,
          experimentData.getAlgorithmNameList()[algorithmIndex],
          experimentData.getAlgorithmNameList()[i]
        ) ;

        String setCoverageAB =  String.format(Locale.ENGLISH, "%.2f",
          setCoverageValueAB);

        fileWriter.write("" + setCoverageAB);
      } else
        fileWriter.write("--" );
      if (i < (experimentData.getAlgorithmNameList().length-1)) {
        fileWriter.write("&") ;
      }
    }

    fileWriter.write("\\\\");
  }

  private void writeTableTail() throws IOException {
    fileWriter.write("\\hline" + "\n");
    fileWriter.write("\\end{tabular}" + "\n");
    fileWriter.write("\\end{scriptsize}" + "\n");
    fileWriter.write("\\end{table}" + "\n");
  }

  private double calculateSetCoverage(String problem, String algorithmA, String algorithmB)
    throws JMetalException {
    double result ;
    SetCoverage setCoverageMetric = new SetCoverage() ;

    Vector<Double> values  = new Vector<Double>(experimentData.getIndependentRuns()) ;

    for (int i = 0; i < experimentData.getIndependentRuns(); i++) {
      String front1 = experimentData.getExperimentBaseDirectory()+"/data/"+algorithmA+"/"+problem+"/FUN."+i ;
      String front2 = experimentData.getExperimentBaseDirectory()+"/data/"+algorithmB+"/"+problem+"/FUN."+i ;
      result = setCoverageMetric.setCoverage(front1, front2) ;
      values.add(i, result);
    }

    return Statistics.calculateMean(values);
  }

  /*
  private void createOutputDirectory() {
    outputDirectoryName = "R";
    outputDirectoryName = experiment.getExperimentBaseDirectory() + "/" + outputDirectoryName;
    Configuration.logger.info("R    : " + outputDirectoryName);
    outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      new File(outputDirectoryName).mkdirs();
      Configuration.logger.info("Creating " + outputDirectoryName + " directory");
    }
  }
  */
}
