//  ParetoFrontsGeneration.java
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

package org.uma.jmetal45.experiment.experimentoutput;

import org.uma.jmetal45.experiment.ExperimentData;
import org.uma.jmetal45.experiment.ExperimentOutput;
import org.uma.jmetal45.qualityindicator.util.MetricsUtil;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.NonDominatedSolutionList;
import org.uma.jmetal45.util.fileoutput.SolutionSetOutput;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 19/07/14.
 */
public class ParetoFrontsGeneration implements ExperimentOutput {
  private ExperimentData experimentData ;
  private String referenceFrontDirectory ;

  /** Constructor */
  private ParetoFrontsGeneration(Builder builder) {
    this.experimentData = builder.experimentData ;
    this.referenceFrontDirectory = experimentData.getExperimentBaseDirectory() + "/referenceFronts";
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
    }

    public ParetoFrontsGeneration build() {
      return new ParetoFrontsGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    for (int i = 0; i < experimentData.getProblemList().length; i++) {
      generateReferenceFront(i);
    }
  }

  private void generateReferenceFront(int problemIndex) {
    if (referenceFrontDirectoryDoesNotExist()) {
      createReferenceFrontDirectory() ;
    }

    MetricsUtil metricsUtils = new MetricsUtil();

    NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
    for (String algorithmName : experimentData.getAlgorithmNameList()) {

      String problemDirectory =
        experimentData.getExperimentBaseDirectory() + "/data/" + algorithmName +
          "/" + experimentData.getProblemList()[problemIndex];

      for (int numRun = 0; numRun < experimentData.getIndependentRuns(); numRun++) {

        String outputParetoFrontFilePath;
        outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
        String solutionFrontFile = outputParetoFrontFilePath;

        metricsUtils.readNonDominatedSolutionSet(solutionFrontFile, solutionSet);
      }
    }

    String referenceParetoFrontFile = getParetoFrontFileName(problemIndex) ;

    try {
      SolutionSetOutput.printObjectivesToFile(solutionSet, referenceParetoFrontFile);
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
  }


  private boolean referenceFrontDirectoryDoesNotExist() {
    boolean result ;
    File directory = new File(referenceFrontDirectory) ;

    if (directory.exists()) {
      result = false;
    } else {
      result = true ;
    }

    return result ;
  }

  private void createReferenceFrontDirectory() {
    boolean result = new File(referenceFrontDirectory).mkdirs();

    if (!result) {
      throw new JMetalException("Error creating reference front directory: " + referenceFrontDirectory) ;
    }
  }

  private String getParetoFrontFileName(int problemIndex) {
    String result ;
    result = referenceFrontDirectory + "/" + experimentData.getProblemList()[problemIndex] + ".pf";

    return result ;
  }
}
