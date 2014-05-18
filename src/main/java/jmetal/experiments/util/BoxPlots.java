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

package jmetal.experiments.util ;

import jmetal.experiments.Experiment;
import jmetal.util.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 18/02/14.
 *
 * Class for generating R scripts to obtain boxplots from the data experiments
 */
public class BoxPlots implements IExperimentOutput{
  Experiment experiment_ ;

  public BoxPlots (Experiment experiment) {
    experiment_ = experiment ;
  }

  @Override
  public void generate() {
    String rDirectory = "R";
    rDirectory = experiment_.experimentBaseDirectory_ + "/" +  rDirectory;
    System.out.println("R    : " + rDirectory);
    File rOutput;
    rOutput = new File(rDirectory);
    if (!rOutput.exists()) {
      new File( rDirectory).mkdirs();
      System.out.println("Creating " +  rDirectory + " directory");
    }

    for (int indicator = 0; indicator <  experiment_.indicatorList_.length; indicator++) {
      System.out.println("Indicator: " +  experiment_.indicatorList_[indicator]);
      String rFile =  rDirectory + "/" +  experiment_.indicatorList_[indicator] + ".Boxplot.R";

      try {
        FileWriter os = new FileWriter(rFile, false);
        os.write("postscript(\"" +
                experiment_.indicatorList_[indicator] +
                ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
                "\n");
        //os.write("resultDirectory<-\"../data/" + experimentName_ +"\"" + "\n");
        os.write("resultDirectory<-\"../data/" + "\"" + "\n");
        os.write("qIndicator <- function(indicator, problem)" + "\n");
        os.write("{" + "\n");

        for (int i = 0; i < experiment_.algorithmNameList_.length; i++) {
          os.write("file" + experiment_.algorithmNameList_[i] +
                  "<-paste(resultDirectory, \"" +
                  experiment_.algorithmNameList_[i] + "\", sep=\"/\")" + "\n");
          os.write("file" + experiment_.algorithmNameList_[i] +
                  "<-paste(file" + experiment_.algorithmNameList_[i] + ", " +
                  "problem, sep=\"/\")" + "\n");
          os.write("file" + experiment_.algorithmNameList_[i] +
                  "<-paste(file" + experiment_.algorithmNameList_[i] + ", " +
                  "indicator, sep=\"/\")" + "\n");
          os.write(experiment_.algorithmNameList_[i] + "<-scan(" + "file" + experiment_.algorithmNameList_[i] + ")" + "\n");
          os.write("\n");
        } // for

        os.write("algs<-c(");
        for (int i = 0; i < experiment_.algorithmNameList_.length - 1; i++) {
          os.write("\"" + experiment_.algorithmNameList_[i] + "\",");
        } // for
        os.write("\"" + experiment_.algorithmNameList_[experiment_.algorithmNameList_.length - 1] + "\")" + "\n");

        os.write("boxplot(");
        for (int i = 0; i < experiment_.algorithmNameList_.length; i++) {
          os.write(experiment_.algorithmNameList_[i] + ",");
        } // for
        if (experiment_.boxplotNotch_) {
          os.write("names=algs, notch = TRUE)" + "\n");
        } else {
          os.write("names=algs, notch = FALSE)" + "\n");
        }
        os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
        os.write("title(main=titulo)" + "\n");

        os.write("}" + "\n");

        os.write("par(mfrow=c(" + experiment_.boxplotRows_ + "," + experiment_.boxplotColumns_ + "))" + "\n");

        os.write("indicator<-\"" + experiment_.indicatorList_[indicator] + "\"" + "\n");

        for (String problem : experiment_.problemList_) {
          os.write("qIndicator(indicator, \"" + problem + "\")" + "\n");
        }

        os.close();
      } catch (IOException e) {
        Configuration.logger_.log(Level.SEVERE, "Error", e);
      }
    }
  }
}
