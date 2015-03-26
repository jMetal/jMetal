//  ExperimentRunner.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.experiment;

import org.uma.jmetal.util.JMetalException;

import java.io.IOException;

public class ExperimentRunner {
  public static void main(String[] args) throws JMetalException, IOException {
    ExperimentConfiguration configuration = new ExperimentConfigurationBuilder("Experiment")
      //.setAlgorithmList(Arrays.asList("NSGAII", "SMPSO", "MOCell", "GDE3"))
      //.setProblemList(Arrays.asList("ZDT1", "ZDT2", "ZDT3", "ZDT4", "ZDT6"))
      .setExperimentBaseDirectory("/Users/antelverde/Softw/jMetal/jMetalGitHub/pruebas")
      .setOutputParetoFrontFileName("FUN")
      .setOutputParetoSetFileName("VAR")
      .setIndependentRuns(4)
      .build() ;

    ExperimentalStudy study = new ExperimentalStudy.Builder(configuration)
      .build() ;

    study.run();
  }
}
