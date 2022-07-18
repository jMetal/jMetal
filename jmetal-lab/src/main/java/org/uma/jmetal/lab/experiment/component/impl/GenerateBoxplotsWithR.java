package org.uma.jmetal.lab.experiment.component.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;

/**
 * This class generates a R script that generates an eps file containing boxplots
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
public class GenerateBoxplotsWithR<Result extends List<? extends Solution<?>>> implements ExperimentComponent {
  private static final String DEFAULT_R_DIRECTORY = "R";

  private final Experiment<?, Result> experiment;
  private int numberOfRows ;
  private int numberOfColumns ;
  private boolean displayNotch ;

  public GenerateBoxplotsWithR(Experiment<?, Result> experimentConfiguration) {
    this.experiment = experimentConfiguration;

    displayNotch = false ;
    numberOfRows = 3 ;
    numberOfColumns = 3 ;

    experiment.removeDuplicatedAlgorithms();
  }

  public GenerateBoxplotsWithR<Result> setRows(int rows) {
    numberOfRows = rows ;

    return this ;
  }

  public @NotNull GenerateBoxplotsWithR<Result> setColumns(int columns) {
    numberOfColumns = columns ;

    return this ;
  }

  public GenerateBoxplotsWithR<Result> setDisplayNotch() {
    displayNotch = true ;

    return this ;
  }

  @Override
  public void run() throws IOException {
    @NotNull String rDirectoryName = experiment.getExperimentBaseDirectory() + "/" + DEFAULT_R_DIRECTORY;
      var rOutput = new File(rDirectoryName);
    if (!rOutput.exists()) {
      new File(rDirectoryName).mkdirs();
      System.out.println("Creating " + rDirectoryName + " directory");
    }
    for (var indicator : experiment.getIndicatorList()) {
        var rFileName = rDirectoryName + "/" + indicator.getName() + ".Boxplot" + ".R";

     try(@NotNull FileWriter os = new FileWriter(rFileName, false)){
      os.write("postscript(\"" +
               indicator.getName() +
              ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
              "\n");

      os.write("resultDirectory<-\"../data" + "\"" + "\n");
      os.write("qIndicator <- function(indicator, problem)" + "\n");
      os.write("{" + "\n");

      for (var i = 0; i <  experiment.getAlgorithmList().size(); i++) {
          var algorithmName = experiment.getAlgorithmList().get(i).getAlgorithmTag();
        os.write("file" +  algorithmName + "<-paste(resultDirectory, \"" + algorithmName + "\", sep=\"/\")" + "\n");
        os.write("file" +  algorithmName + "<-paste(file" +  algorithmName + ", " +  "problem, sep=\"/\")" + "\n");
        os.write("file" +  algorithmName + "<-paste(file" +  algorithmName + ", " + "indicator, sep=\"/\")" + "\n");
        os.write( algorithmName + "<-scan(" + "file" +  algorithmName + ")" + "\n");
        os.write("\n");
      }

      os.write("algs<-c(");
      for (var i = 0; i <  experiment.getAlgorithmList().size() - 1; i++) {
        os.write("\"" +  experiment.getAlgorithmList().get(i).getAlgorithmTag() + "\",");
      } // for
      os.write("\"" +  experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getAlgorithmTag() + "\")" + "\n");

      os.write("boxplot(");
      for (var i = 0; i <  experiment.getAlgorithmList().size(); i++) {
        os.write(experiment.getAlgorithmList().get(i).getAlgorithmTag() + ",");
      } // for
      if (displayNotch) {
        os.write("names=algs, notch = TRUE)" + "\n");
      } else {
        os.write("names=algs, notch = FALSE)" + "\n");
      }
      os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
      os.write("title(main=titulo)" + "\n");

      os.write("}" + "\n");

      os.write("par(mfrow=c(" + numberOfRows + "," + numberOfColumns + "))" + "\n");

      os.write("indicator<-\"" +  indicator.getName() + "\"" + "\n");

      for (ExperimentProblem<?> problem : experiment.getProblemList()) {
        os.write("qIndicator(indicator, \"" + problem.getTag() + "\")" + "\n");
      }
     }
    }
  }
}