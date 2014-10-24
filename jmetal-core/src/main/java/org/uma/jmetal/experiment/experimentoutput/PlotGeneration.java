//  PlotGeneration.java
//
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
 * Created by jmpadron on 1/09/14.
 */
public class PlotGeneration implements ExperimentOutput {
  private ExperimentData experimentData ;
  private int[] numberOfObjectives;
  private String outputDirectory ;
  private String paretoFrontDirectory;
  private String[] paretoFrontFiles;
  private boolean compareByRun;

  /** Constructor */
  private PlotGeneration(Builder builder) {
    experimentData = builder.experimentData ;
    numberOfObjectives = builder.numberOfObjectives;
    outputDirectory = builder.outputDirectory ;
    paretoFrontDirectory = builder.paretoFrontDirectory;
    paretoFrontFiles = builder.paretoFrontFiles;
    compareByRun = builder.compareByRun;

    if (outputDirectoryDoesNotExist()) {
      createOutputDirectory() ;
    }
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData ;
    private int[] numberOfObjectives;
    private String outputDirectory ;
    private String paretoFrontDirectory;
    private String[] paretoFrontFiles;
    private boolean compareByRun;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
      this.numberOfObjectives = null;
      this.outputDirectory = experimentData.getExperimentBaseDirectory()+"/R" ;
      this.paretoFrontDirectory = "";
      this.paretoFrontFiles = null;
      this.compareByRun = false;
    }

    /**
     * Specifies the output directory for the R script and eps.
     * @param outputDirectory
     * @return Builder
     */
    public Builder outputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;
      return this;
    }

    /**
     * Specifies the number of objective functions for each problem
     * @param numberOfObjectives
     * @return Builder
     */
    public Builder numberOfObjectives(int[] numberOfObjectives){
      this.numberOfObjectives = Arrays.copyOf(numberOfObjectives, numberOfObjectives.length);
      return this;
    }

    /**
     * Specifies the PFTrue's directory
     * @param paretoFrontDirectory
     * @return Builder
     */
    public Builder paretoFrontDirectory(String paretoFrontDirectory){
      this.paretoFrontDirectory = paretoFrontDirectory;
      return this;
    }

    /**
     * Specifies the filename of the PFTrue for each problem
     * @param paretoFrontFiles
     * @return
     */
    public Builder paretoFrontFiles(String[] paretoFrontFiles){
      this.paretoFrontFiles = Arrays.copyOf(paretoFrontFiles, paretoFrontFiles.length);
      return this;
    }

    /**
     * Sets to true the compareByRun property.
     * When compareByRun is true, PFTrue is plotted along with the PFs obtained by each algorithm
     * @return
     */
    public Builder compareByRun(){
      this.compareByRun = true;
      return this;
    }

    public PlotGeneration build() {
      return new PlotGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    String[] problemList = this.experimentData.getProblemList();
    String[] algorithmsNameList = this.experimentData.getAlgorithmNameList();
    if(this.compareByRun){
      for(int i = 0; i < problemList.length; i++){
        generateRScript(problemList[i], this.paretoFrontDirectory, algorithmsNameList,
                problemList[i], this.paretoFrontFiles[i], this.experimentData.getOutputParetoFrontFileName(), this.experimentData.getIndependentRuns(), this.numberOfObjectives[i]);
      }
    } else {
      for(int i = 0; i < problemList.length; i++){
        for(int j = 0; j < algorithmsNameList.length; j++){
          generateRScript(problemList[i] + "." + algorithmsNameList[j], this.paretoFrontDirectory, new String[]{algorithmsNameList[j]},
                  problemList[i], this.paretoFrontFiles[i],this.experimentData.getOutputParetoFrontFileName(), this.experimentData.getIndependentRuns(), this.numberOfObjectives[i]);
        }
      }
    }
  }

  public void generateRScript(String scriptName,String paretoFrontDirectory, String[] algorithms,
                              String problemName, String paretoFrontTrueFileName,String outputParetoFrontFileName, int numberOfRuns, int numberOfObjectives){
    if(numberOfObjectives < 2 || numberOfObjectives > 3){
      JMetalLogger.logger.warning("Plotting is only available for problems with 2 or 3 objective functions");
    } else {
      JMetalLogger.logger.info("Plot generation");
      String outputFile = this.outputDirectory + "/" + scriptName + ".Plot.Generation.R";
      String dataDirectory = experimentData.getExperimentBaseDirectory() + "/data";

      try {
        FileWriter os = new FileWriter(outputFile, false);
        //variables
        os.write("resultDirectory<-\"" + dataDirectory + "\"" + "\n");
        os.write("paretoFrontDirectory <- \"" + paretoFrontDirectory + "\"" + "\n" );
        os.write("algorithms <- c(");
        for (int i = 0; i < algorithms.length; i++){
          os.write("\"" + algorithms[i] + "\"");
          if(i < algorithms.length - 1){
            os.write(",");
          }
        }
        os.write(")" + "\n");
        os.write("problem <- \"" + problemName +"\"" + "\n");
        os.write("paretoFrontTrueFileName <- \"" + paretoFrontTrueFileName + "\"" + "\n");
        os.write("outputParetoFrontFileName <- \"" + outputParetoFrontFileName + "\"" + "\n");
        os.write("numberOfRuns <- " + numberOfRuns + "\n");
        os.write("numberOfObjectives <- " + numberOfObjectives + "\n");
        //end variables

        os.write("if(numberOfObjectives <= 3){" + "\n");
        os.write("\tpostscript(\"" + this.outputDirectory + "/"  + scriptName + ".Plot.Generation.eps\", horizontal=FALSE, onefile=TRUE, " +
                "height=340, width=420, pointsize=10)" + "\n");

        os.write("\tPFFile <-paste(paretoFrontDirectory, problem, sep=\"/\")" + "\n");
        os.write("\tPFFile <-paste(PFFile, \"pf\", sep=\".\")" + "\n");
        os.write("\tPFTrue <- read.table(file=PFFile, head=FALSE)" + "\n");

        os.write("\tif(numberOfObjectives == 3){" + "\n");
        os.write("\t\tlibrary(scatterplot3d)" + "\n");
        os.write("\t}" + "\n");

        os.write("\tiFlag <- 0" + "\n");
        os.write("\twhile(iFlag < numberOfRuns){" + "\n");
        os.write("\t\ttitle <- paste(problem, \"- RUN #\", sep=\" \")" + "\n");
        os.write("\t\ttitle <- paste(title, iFlag + 1, sep=\"\")" + "\n");
        os.write("\t\tif(numberOfObjectives == 2){" + "\n");
        os.write("\t\t\tplot(PFTrue, col = 1, type=\"p\", main = title, xlab = \"F1\", ylab = \"F2\", pch=1)" + "\n");
        os.write("\t\t\tjFlag <- 2" + "\n");
        os.write("\t\t\tfor (algorithm in algorithms) {" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(resultDirectory, algorithm, sep=\"/\")" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(FUNfile, problem, sep=\"/\")" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(FUNfile, outputParetoFrontFileName, sep=\"/\")" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(FUNfile, iFlag, sep=\".\")" + "\n");
        os.write("\t\t\t\tPFKnow <- read.table(file=FUNfile, head=FALSE)" + "\n");
        os.write("\t\t\t\tlines(PFKnow, col = jFlag, type = \"p\", pch=jFlag)" + "\n");
        os.write("\t\t\t\tjFlag <- jFlag + 1" + "\n");
        os.write("\t\t\t}" + "\n");
        os.write("\t\t\tlegend(\"topright\", legend = c(\"PFTrue\", algorithms), col = c(seq(1, (jFlag - 1), 1)), pch = c(seq(1, (jFlag - 1), 1)))" + "\n");
        os.write("\t\t} else {" + "\n");
        os.write("\t\t\ts3d <- scatterplot3d(PFTrue, type = \"p\", color = 1, highlight.3d = FALSE, col.axis = \"black\", main = title, pch = 1, xlab = \"F1\", ylab = \"F2\", zlab=\"F3\")" + "\n");
        os.write("\t\t\tjFlag <- 2" + "\n");
        os.write("\t\t\tfor (algorithm in algorithms) {" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(resultDirectory, algorithm, sep=\"/\")" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(FUNfile, problem, sep=\"/\")" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(FUNfile, outputParetoFrontFileName, sep=\"/\")" + "\n");
        os.write("\t\t\t\tFUNfile<-paste(FUNfile, iFlag, sep=\".\")" + "\n");
        os.write("\t\t\t\tPFKnow <- read.table(file=FUNfile, head=FALSE)" + "\n");
        os.write("\t\t\t\ts3d$points3d(PFKnow, col = jFlag, type = \"p\", pch = jFlag, cex = 1)" + "\n");
        os.write("\t\t\t\tjFlag <- jFlag + 1" + "\n");
        os.write("\t\t\t}" + "\n");
        os.write("\t\t\tlegend(\"bottomright\", legend = c(\"PFTrue\", algorithms), col = c(seq(1, (jFlag - 1), 1)), pch = c(seq(1, (jFlag - 1), 1)))" + "\n");
        os.write("\t\t}" + "\n");
        os.write("\t\tiFlag <- iFlag + 1" + "\n");
        os.write("\t}" + "\n");
        os.write("} else {" + "\n");
        os.write("\tprint(\"Plotting is only available for problems with 2 or 3 objective functions\")" + "\n");
        os.write("}" + "\n");

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
