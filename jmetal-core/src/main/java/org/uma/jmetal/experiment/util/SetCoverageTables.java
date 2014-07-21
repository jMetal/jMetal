//  WilcoxonTextTables.java
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
import org.uma.jmetal.qualityIndicator.SetCoverage;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by Antonio J. Nebro on 17/02/14.
 * <p/>
 * This class allows to generate Latex tables with the results of applying the Wilcoxon Rank-Sum org.uma.test
 */
public class SetCoverageTables implements IExperimentOutput {
  private File outputDirectory;
  private String outputDirectoryName;
  private Experiment experiment;
  private FileWriter fileWriter;


  public SetCoverageTables(Experiment experiment) {
    this.experiment = experiment;
    outputDirectory = null;
    outputDirectoryName = "";
    fileWriter = null ;
  }

  @Override
  public void generate() throws IOException, JMetalException {
    createOutputDirectory();

    String outputTexFile = outputDirectoryName + "/" + "setCoverage.tex";
    fileWriter = new FileWriter(outputTexFile, false) ;

    generateLatexHeader() ;
    for (String problem : experiment.getProblemList()) {
      generateTable(problem) ;
    }
    generateLatexTail() ;

    fileWriter.close();
  }

  private void generateLatexHeader() throws IOException {
    //FileWriter os = new FileWriter(fileName, false);
    fileWriter.write("\\documentclass{article}" + "\n");
    fileWriter.write("\\title{" + experiment.getExperimentName() + "}" + "\n");
    fileWriter.write("\\usepackage{colortbl}" + "\n");
    fileWriter.write("\\usepackage[table*]{xcolor}" + "\n");
    fileWriter.write("\\usepackage[margin=0.6in]{geometry}" + "\n");
    fileWriter.write("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    fileWriter.write("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    fileWriter.write("\\author{}" + "\n");
    fileWriter.write("\\begin{document}" + "\n");
    fileWriter.write("\\maketitle" + "\n");
    fileWriter.write("\\section{Tables}" + "\n");
  }

  private void generateTable(String problem) throws IOException, JMetalException {
    writeTableHeader(problem);

    for (int i = 0 ; i < experiment.getAlgorithmNameList().length; i++) {
      writeTableRow(i, problem) ;
    }

    writeTableTail() ;
  }

  private void generateLatexTail() throws IOException {
    fileWriter.write("\\end{document}" + "\n");
  }

  private void writeTableHeader(String problem) throws IOException {
    fileWriter.write("\n\n");
    fileWriter.write("\\begin{table}" + "\n");
    fileWriter.write("\\caption{" + problem + ". Set Coverage}" + "\n");
    fileWriter.write("\\label{table:setCoverage." + problem + "}" + "\n");
    fileWriter.write("\\centering" + "\n");
    fileWriter.write("\\begin{scriptsize}" + "\n");
    fileWriter.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (int i = 0 ; i < experiment.getAlgorithmNameList().length; i++) {
      fileWriter.write("l");
    }
    fileWriter.write("}\n");

    // write table head
    for (int i = -1; i < experiment.getAlgorithmNameList().length; i++) {
      if (i == -1) {
        fileWriter.write(" & ");
      } else if (i == (experiment.getAlgorithmNameList().length - 1)) {
        fileWriter.write(" " + experiment.getAlgorithmNameList()[i] + "\\\\" + "\n");
      } else {
        fileWriter.write("" + experiment.getAlgorithmNameList()[i] + " & ");
      }
    }
    fileWriter.write("\\hline" + "\n");
  }

  private void writeTableRow(int algorithmIndex, String problem) throws IOException,
    JMetalException {
    fileWriter.write("" + experiment.getAlgorithmNameList()[algorithmIndex] + " & ");
    for (int i = 0; i < experiment.getAlgorithmNameList().length; i++) {
      if (i != algorithmIndex) {
        double setCoverageValueAB = calculateSetCoverage(
          problem,
          experiment.getAlgorithmNameList()[algorithmIndex],
          experiment.getAlgorithmNameList()[i]
        ) ;

        String setCoverageAB =  String.format(Locale.ENGLISH, "%.2f",
          setCoverageValueAB);

        fileWriter.write("" + setCoverageAB);
        //String setCoverageBA =  String.format(Locale.ENGLISH, "%.2f",
        //  setCoverageValueBA);
        //fileWriter.write("("+setCoverageAB +", " + setCoverageBA + ")"+ " & ");
      } else
        fileWriter.write("--" );
      if (i < (experiment.getAlgorithmNameList().length-1)) {
        fileWriter.write("&") ;
      }
    }

    fileWriter.write("\\\\");
  }

  private void writeTableTail() throws IOException {
    fileWriter.write("\\hline" + "\n");
    fileWriter.write("\\end{tabular}" + "\n");
    fileWriter.write("\\end{scriptsize}" + "\n");
    fileWriter.write("\\end{table}" + "\n");
  }

  private double calculateSetCoverage(String problem, String algorithmA, String algorithmB)
    throws JMetalException {
    double result = 5.5 ;
    SetCoverage setCoverageMetric = new SetCoverage() ;

    Vector<Double> values  = new Vector<Double>(experiment.getIndependentRuns()) ;

    for (int i = 0; i < experiment.getIndependentRuns(); i++) {
      String front1 = experiment.getExperimentBaseDirectory()+"/data/"+algorithmA+"/"+problem+"/FUN."+i ;
      String front2 = experiment.getExperimentBaseDirectory()+"/data/"+algorithmB+"/"+problem+"/FUN."+i ;
      result = setCoverageMetric.setCoverage(front1, front2) ;
      values.add(i, result);
    }

    return Statistics.calculateMean(values);
  }

  private void createOutputDirectory() {
    outputDirectoryName = "R";
    outputDirectoryName = experiment.getExperimentBaseDirectory() + "/" + outputDirectoryName;
    Configuration.logger.info("R    : " + outputDirectoryName);
    outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      new File(outputDirectoryName).mkdirs();
      Configuration.logger.info("Creating " + outputDirectoryName + " directory");
    }
  }
}
