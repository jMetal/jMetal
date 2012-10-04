//  RBoxplot.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package jmetal.experiments.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import jmetal.experiments.Experiment;

/**
 * Class for generating results in form of boxplots
 */
public class RBoxplot {
  /**
   * This method produces R scripts for generating eps files containing boxplots
   * of the results previosly obtained. The boxplots will be arranged in a grid
   * of rows x cols. As the number of problems in the experiment can be too high,
   * the @param problems includes a list of the problems to be plotted.
   * @param rows
   * @param cols
   * @param problems List of problem to plot
   * @param prefix Prefix to be added to the names of the R scripts
   * @throws java.io.FileNotFoundException
   * @throws java.io.IOException
   */
  public static  void generateScripts(int rows,
                                      int cols,
                                      String[] problems,
                                      String prefix,
                                      boolean notch,
                                      Experiment experiment)
                                            throws FileNotFoundException, IOException {
    // STEP 1. Creating R output directory

    String rDirectory = "R";
    rDirectory = experiment.experimentBaseDirectory_ + "/" +  rDirectory;
    System.out.println("R    : " + rDirectory);
    File rOutput;
    rOutput = new File(rDirectory);
    if (!rOutput.exists()) {
      new File( rDirectory).mkdirs();
      System.out.println("Creating " +  rDirectory + " directory");
    }

    for (int indicator = 0; indicator <  experiment.indicatorList_.length; indicator++) {
      System.out.println("Indicator: " +  experiment.indicatorList_[indicator]);
      String rFile =  rDirectory + "/" + prefix + "." +  experiment.indicatorList_[indicator] + ".Boxplot.R";

      FileWriter os = new FileWriter(rFile, false);
      os.write("postscript(\"" + prefix + "." +
               experiment.indicatorList_[indicator] +
              ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
              "\n");
      //os.write("resultDirectory<-\"../data/" + experimentName_ +"\"" + "\n");
      os.write("resultDirectory<-\"../data/" + "\"" + "\n");
      os.write("qIndicator <- function(indicator, problem)" + "\n");
      os.write("{" + "\n");

      for (int i = 0; i <  experiment.algorithmNameList_.length; i++) {
        os.write("file" +  experiment.algorithmNameList_[i] +
                "<-paste(resultDirectory, \"" +
                 experiment.algorithmNameList_[i] + "\", sep=\"/\")" + "\n");
        os.write("file" +  experiment.algorithmNameList_[i] +
                "<-paste(file" +  experiment.algorithmNameList_[i] + ", " +
                "problem, sep=\"/\")" + "\n");
        os.write("file" +  experiment.algorithmNameList_[i] +
                "<-paste(file" +  experiment.algorithmNameList_[i] + ", " +
                "indicator, sep=\"/\")" + "\n");
        os.write( experiment.algorithmNameList_[i] + "<-scan(" + "file" +  experiment.algorithmNameList_[i] + ")" + "\n");
        os.write("\n");
      } // for

      os.write("algs<-c(");
      for (int i = 0; i <  experiment.algorithmNameList_.length - 1; i++) {
        os.write("\"" +  experiment.algorithmNameList_[i] + "\",");
      } // for
      os.write("\"" +  experiment.algorithmNameList_[ experiment.algorithmNameList_.length - 1] + "\")" + "\n");

      os.write("boxplot(");
      for (int i = 0; i <  experiment.algorithmNameList_.length; i++) {
        os.write( experiment.algorithmNameList_[i] + ",");
      } // for
      if (notch) {
        os.write("names=algs, notch = TRUE)" + "\n");
      } else {
        os.write("names=algs, notch = FALSE)" + "\n");
      }
      os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
      os.write("title(main=titulo)" + "\n");

      os.write("}" + "\n");

      os.write("par(mfrow=c(" + rows + "," + cols + "))" + "\n");

      os.write("indicator<-\"" +  experiment.indicatorList_[indicator] + "\"" + "\n");

      for (int i = 0; i < problems.length; i++) {
        os.write("qIndicator(indicator, \"" + problems[i] + "\")" + "\n");
      }

      os.close();
    } // for
    } // generateRBoxplotScripts

}