//  QualityIndicatorGeneration.java
//
//  Author:
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/

package org.uma.jmetal.experiment.experimentoutput;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentOutput;
import org.uma.jmetal.qualityIndicator.*;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.FileUtils;
import org.uma.jmetal.util.JMetalException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Antonio J. Nebro on 20/07/14.
 */
public class QualityIndicatorGeneration implements ExperimentOutput {

  private ExperimentData experimentData ;
  private String[] paretoFrontFiles ;
  private String paretoFrontDirectory ;
  private String[] qualityIndicatorList ;


  /** Constructor */
  private QualityIndicatorGeneration(Builder builder) {
    experimentData = builder.experimentData ;
    paretoFrontFiles = builder.paretoFrontFiles ;
    paretoFrontDirectory = builder.paretoFrontDirectory ;
    qualityIndicatorList = builder.qualityIndicatorList ;
  }

  /** Builder class */
  public static class Builder {
    private ExperimentData experimentData ;
    private String[] paretoFrontFiles ;
    private String paretoFrontDirectory ;
    private String[] qualityIndicatorList ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
      /* Default pareto front directory */
      this.paretoFrontDirectory = experimentData.getExperimentBaseDirectory() + "/referenceFronts" ;

      /* Default pareto front files */
      paretoFrontFiles = new String[experimentData.getProblemList().length] ;
      for (int i = 0; i < experimentData.getProblemList().length; i++) {
        //        paretoFrontFiles[i] = paretoFrontDirectory+ "/" + experimentData.getProblemList()[i] + ".pf";
        paretoFrontFiles[i] =  experimentData.getProblemList()[i] + ".pf";
      }

      qualityIndicatorList = new String[0] ;
    }

    public Builder paretoFrontDirectory(String paretoFrontDirectory) {
      this.paretoFrontDirectory = paretoFrontDirectory ;

      return this ;
    }

    public Builder paretoFrontFiles(String[] paretoFrontFiles) {
      if (paretoFrontFiles.length != experimentData.getProblemList().length) {
        System.out.println("problme list lenght: "+ experimentData.getProblemList().length) ;
        System.out.println(experimentData.getProblemList()[0]);
        System.out.println(experimentData.getProblemList()[1]);
        throw new JMetalException("Wrong number of pareto front file names: "
          + paretoFrontFiles.length + " instead of "+experimentData.getProblemList().length) ;
      }

      this.paretoFrontFiles = Arrays.copyOf(paretoFrontFiles, paretoFrontFiles.length) ;

      return this ;
    }

    public Builder qualityIndicatorList(String []qualityIndicatorList) {
      this.qualityIndicatorList = Arrays.copyOf(qualityIndicatorList, qualityIndicatorList.length) ;

      return this ;
    }

    public QualityIndicatorGeneration build() {
      return new QualityIndicatorGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    for (int algorithmIndex = 0;
         algorithmIndex < experimentData.getAlgorithmNameList().length; algorithmIndex++) {

      String algorithmDirectory;
      algorithmDirectory = experimentData.getExperimentBaseDirectory()
        + "/data/" + experimentData.getAlgorithmNameList()[algorithmIndex] + "/";

      for (int problemIndex = 0;
           problemIndex < experimentData.getProblemList().length; problemIndex++) {

        String problemDirectory = algorithmDirectory + experimentData.getProblemList()[problemIndex];

        for (String indicator : qualityIndicatorList) {
          Configuration.logger.info("Experiment - Quality indicator: " + indicator);

          FileUtils.deleteFile(problemDirectory + "/" + indicator);

          for (int numRun = 0; numRun < experimentData.getIndependentRuns(); numRun++) {

            String outputParetoFrontFilePath;
            outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
            String solutionFrontFile = outputParetoFrontFilePath;
            String qualityIndicatorFile = problemDirectory;
            double value = 0;

            String paretoFront = paretoFrontDirectory + "/" + paretoFrontFiles[problemIndex] ;

            double[][] trueFront = FileUtils.readFrontIntoArray(paretoFront) ;
            double[][] solutionFront = FileUtils.readFrontIntoArray(solutionFrontFile);

            if ("HV".equals(indicator)) {
              value = new Hypervolume().hypervolume(solutionFront, trueFront);

              qualityIndicatorFile = qualityIndicatorFile + "/HV";
            } else if ("SPREAD".equals(indicator)) {
              value = new Spread().spread(solutionFront, trueFront);

              qualityIndicatorFile = qualityIndicatorFile + "/SPREAD";
            } else if ("GD".equals(indicator)) {
              value = new GenerationalDistance().generationalDistance(solutionFront, trueFront);

              qualityIndicatorFile = qualityIndicatorFile + "/GD";
            } else if ("IGD".equals(indicator)) {
              value = new InvertedGenerationalDistance()
                .invertedGenerationalDistance(solutionFront, trueFront);

              qualityIndicatorFile = qualityIndicatorFile + "/IGD";
            } else if ("EPSILON".equals(indicator)) {
              value = new Epsilon().epsilon(solutionFront, trueFront);

              qualityIndicatorFile = qualityIndicatorFile + "/EPSILON";
            } else {
              throw new JMetalException("Indicator does not exist: " + indicator) ;
            }


            if (!qualityIndicatorFile.equals(problemDirectory)) {
              FileWriter os;
              try {
                os = new FileWriter(qualityIndicatorFile, true);
                os.write("" + value + "\n");
                os.close();
              } catch (IOException ex) {
                Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
              }
            }
          }
        }
      }
    }
  }
}
