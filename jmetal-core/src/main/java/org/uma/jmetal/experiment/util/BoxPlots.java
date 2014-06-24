//  Boxplots.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
//

package org.uma.jmetal.experiment.util;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.util.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 18/02/14.
 * <p/>
 * Class for generating R scripts to obtain boxplots from the data experiment
 */
public class BoxPlots implements IExperimentOutput {
  Experiment experiment_;

  public BoxPlots(Experiment experiment) {
    experiment_ = experiment;
  }

  @Override
  public void generate() {
    String rDirectory = "R";
    rDirectory = experiment_.getExperimentBaseDirectory() + "/" + rDirectory;
    Configuration.logger_.info("R    : " + rDirectory);
    File rOutput;
    rOutput = new File(rDirectory);
    if (!rOutput.exists()) {
      new File(rDirectory).mkdirs();
      Configuration.logger_.info("Creating " + rDirectory + " directory");
    }

    for (int indicator = 0; indicator < experiment_.getIndicatorList().length; indicator++) {
      Configuration.logger_.info("Indicator: " + experiment_.getIndicatorList()[indicator]);
      String rFile = rDirectory + "/" + experiment_.getIndicatorList()[indicator] + ".Boxplot.R";

      try {
        FileWriter os = new FileWriter(rFile, false);
        os.write("postscript(\"" +
          experiment_.getIndicatorList()[indicator] +
          ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
          "\n");
        os.write("resultDirectory<-\"../data/" + "\"" + "\n");
        os.write("qIndicator <- function(indicator, problem)" + "\n");
        os.write("{" + "\n");

        for (int i = 0; i < experiment_.getAlgorithmNameList().length; i++) {
          os.write("file" + experiment_.getAlgorithmNameList()[i] +
            "<-paste(resultDirectory, \"" +
            experiment_.getAlgorithmNameList()[i] + "\", sep=\"/\")" + "\n");
          os.write("file" + experiment_.getAlgorithmNameList()[i] +
            "<-paste(file" + experiment_.getAlgorithmNameList()[i] + ", " +
            "problem, sep=\"/\")" + "\n");
          os.write("file" + experiment_.getAlgorithmNameList()[i] +
            "<-paste(file" + experiment_.getAlgorithmNameList()[i] + ", " +
            "indicator, sep=\"/\")" + "\n");
          os.write(experiment_.getAlgorithmNameList()[i] + "<-scan(" + "file" + experiment_
            .getAlgorithmNameList()[i] + ")" + "\n");
          os.write("\n");
        }

        os.write("algs<-c(");
        for (int i = 0; i < experiment_.getAlgorithmNameList().length - 1; i++) {
          os.write("\"" + experiment_.getAlgorithmNameList()[i] + "\",");
        }
        os.write(
          "\"" + experiment_.getAlgorithmNameList()[experiment_.getAlgorithmNameList().length - 1]
            + "\")" + "\n"
        );

        os.write("boxplot(");
        for (int i = 0; i < experiment_.getAlgorithmNameList().length; i++) {
          os.write(experiment_.getAlgorithmNameList()[i] + ",");
        }
        if (experiment_.getBoxplotNotch()) {
          os.write("names=algs, notch = TRUE)" + "\n");
        } else {
          os.write("names=algs, notch = FALSE)" + "\n");
        }
        os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
        os.write("title(main=titulo)" + "\n");

        os.write("}" + "\n");

        os.write(
          "par(mfrow=c(" + experiment_.getBoxplotRows() + "," + experiment_.getBoxplotColumns()
            + "))" + "\n"
        );

        os.write("indicator<-\"" + experiment_.getIndicatorList()[indicator] + "\"" + "\n");

        for (String problem : experiment_.getProblemList()) {
          os.write("qIndicator(indicator, \"" + problem + "\")" + "\n");
        }

        os.close();
      } catch (IOException e) {
        Configuration.logger_.log(Level.SEVERE, "Error", e);
      }
    }
  }
}
