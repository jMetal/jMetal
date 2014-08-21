package org.uma.jmetal.experiment.experimentoutput;

import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentOutput;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 23/07/14.
 */
public class BoxplotGeneration implements ExperimentOutput {
  private ExperimentData experimentData ;
  private String[] indicatorList ;
  private String outputDirectory ;
  private boolean includeNotch;
  private int numberOfRows ;
  private int numberOfColumns ;

  /** Constructor */
  private BoxplotGeneration(Builder builder) {
    experimentData = builder.experimentData ;
    indicatorList = builder.indicatorList ;
    outputDirectory = builder.outputDirectory ;
    includeNotch = builder.notch ;
    numberOfRows = builder.numberOfRows ;
    numberOfColumns = builder.numberOfColumns ;

    if (outputDirectoryDoesNotExist()) {
      createOutputDirectory() ;
    }
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData ;
    private String[] indicatorList ;
    private String outputDirectory ;
    private boolean notch ;
    private int numberOfRows ;
    private int numberOfColumns ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
      this.indicatorList = null ;
      outputDirectory = experimentData.getExperimentBaseDirectory()+"/R" ;
      notch = false ;
      numberOfRows = 1 ;
      numberOfColumns = 1 ;
    }

    public Builder indicatorList(String[] indicatorList) {
      this.indicatorList = Arrays.copyOf(indicatorList, indicatorList.length) ;

      return this ;
    }

    public Builder outputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;

      return this;
    }

    public Builder includeNotch() {
      notch = true ;

      return this ;
    }

    public Builder numberOfRows(int numberOfRows) {
      this.numberOfRows = numberOfRows ;

      return this ;
    }

    public Builder numberOfColumns(int numberOfColumns) {
      this.numberOfColumns = numberOfColumns ;

      return this ;
    }

    public BoxplotGeneration build() {
      return new BoxplotGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    for (int indicator = 0; indicator < indicatorList.length; indicator++) {
      JMetalLogger.logger.info("Indicator: " + indicatorList[indicator]);
      String outputFile = outputDirectory + "/" + indicatorList[indicator] + ".Boxplot.R";

      try {
        FileWriter os = new FileWriter(outputFile, false);
        os.write("postscript(\"" +
          indicatorList[indicator] +
          ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
          "\n");
        os.write("resultDirectory<-\"../data/" + "\"" + "\n");
        os.write("qIndicator <- function(indicator, problem)" + "\n");
        os.write("{" + "\n");

        for (int i = 0; i < experimentData.getAlgorithmNameList().length; i++) {
          os.write("file" + experimentData.getAlgorithmNameList()[i] +
            "<-paste(resultDirectory, \"" +
            experimentData.getAlgorithmNameList()[i] + "\", sep=\"/\")" + "\n");
          os.write("file" + experimentData.getAlgorithmNameList()[i] +
            "<-paste(file" + experimentData.getAlgorithmNameList()[i] + ", " +
            "problem, sep=\"/\")" + "\n");
          os.write("file" + experimentData.getAlgorithmNameList()[i] +
            "<-paste(file" + experimentData.getAlgorithmNameList()[i] + ", " +
            "indicator, sep=\"/\")" + "\n");
          os.write(experimentData.getAlgorithmNameList()[i] + "<-scan(" + "file" + experimentData
            .getAlgorithmNameList()[i] + ")" + "\n");
          os.write("\n");
        }

        os.write("algs<-c(");
        for (int i = 0; i < experimentData.getAlgorithmNameList().length - 1; i++) {
          os.write("\"" + experimentData.getAlgorithmNameList()[i] + "\",");
        }
        os.write(
          "\"" + experimentData.getAlgorithmNameList()[experimentData.getAlgorithmNameList().length - 1]
            + "\")" + "\n"
        );

        os.write("boxplot(");
        for (int i = 0; i < experimentData.getAlgorithmNameList().length; i++) {
          os.write(experimentData.getAlgorithmNameList()[i] + ",");
        }
        if (includeNotch) {
          os.write("names=algs, notch = TRUE)" + "\n");
        } else {
          os.write("names=algs, notch = FALSE)" + "\n");
        }
        os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
        os.write("title(main=titulo)" + "\n");

        os.write("}" + "\n");

        os.write(
          "par(mfrow=c(" + numberOfRows + "," + numberOfColumns + "))" + "\n"
        );

        os.write("indicator<-\"" + indicatorList[indicator] + "\"" + "\n");

        for (String problem : experimentData.getProblemList()) {
          os.write("qIndicator(indicator, \"" + problem + "\")" + "\n");
        }

        os.close();
      } catch (IOException e) {
        JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      }
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
