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

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
public class GenerateBoxplotsWithR<Result> implements ExperimentComponent {
  private static final String DEFAULT_R_DIRECTORY = "R";

  private final Experiment<?, Result> experiment;
  private int numberOfRows ;
  private int numberOfColumns ;
  private boolean displayNotch ;

  public GenerateBoxplotsWithR(Experiment<?, Result> experimentConfiguration) {
    this.experiment = experimentConfiguration;
    this.experiment.removeDuplicatedAlgorithms();

    experiment.removeDuplicatedAlgorithms();
    displayNotch = false ;
    numberOfRows = 3 ;
    numberOfColumns = 3 ;
  }

  public GenerateBoxplotsWithR<Result> setRows(int rows) {
    numberOfRows = rows ;

    return this ;
  }

  public GenerateBoxplotsWithR<Result> setColumns(int columns) {
    numberOfColumns = columns ;

    return this ;
  }

  public GenerateBoxplotsWithR<Result> setDisplayNotch() {
    displayNotch = true ;

    return this ;
  }

  @Override
  public void run() throws IOException {
    String rDirectoryName = experiment.getExperimentBaseDirectory() + "/" + DEFAULT_R_DIRECTORY;
    File rOutput;
    rOutput = new File(rDirectoryName);
    if (!rOutput.exists()) {
      boolean result = new File(rDirectoryName).mkdirs();
      System.out.println("Creating " + rDirectoryName + " directory");
    }
    for (GenericIndicator<? extends Solution<?>> indicator : experiment.getIndicatorList()) {
      String rFileName = rDirectoryName + "/" + indicator.getName() + ".Boxplot" + ".R";

      FileWriter os = new FileWriter(rFileName, false);
      os.write("postscript(\"" +
               indicator.getName() +
              ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
              "\n");

      os.write("resultDirectory<-\"../data" + "\"" + "\n");
      os.write("qIndicator <- function(indicator, problem)" + "\n");
      os.write("{" + "\n");

      for (int i = 0; i <  experiment.getAlgorithmList().size(); i++) {
        String algorithmName = experiment.getAlgorithmList().get(i).getTag() ;
        os.write("file" +  algorithmName + "<-paste(resultDirectory, \"" + algorithmName + "\", sep=\"/\")" + "\n");
        os.write("file" +  algorithmName + "<-paste(file" +  algorithmName + ", " +  "problem, sep=\"/\")" + "\n");
        os.write("file" +  algorithmName + "<-paste(file" +  algorithmName + ", " + "indicator, sep=\"/\")" + "\n");
        os.write( algorithmName + "<-scan(" + "file" +  algorithmName + ")" + "\n");
        os.write("\n");
      }

      os.write("algs<-c(");
      for (int i = 0; i <  experiment.getAlgorithmList().size() - 1; i++) {
        os.write("\"" +  experiment.getAlgorithmList().get(i).getTag() + "\",");
      } // for
      os.write("\"" +  experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getTag() + "\")" + "\n");

      os.write("boxplot(");
      for (int i = 0; i <  experiment.getAlgorithmList().size(); i++) {
        os.write(experiment.getAlgorithmList().get(i).getTag() + ",");
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

      for (Problem<?> problem : experiment.getProblemList()) {
        os.write("qIndicator(indicator, \"" + problem.getName() + "\")" + "\n");
      }

      os.close();
    }
  }
}