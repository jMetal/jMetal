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

package jmetal.experiments.util;

import jmetal.experiments.Experiment;
import jmetal.qualityIndicator.SetCoverage;
import jmetal.util.Configuration;
import jmetal.util.Statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by Antonio J. Nebro on 17/02/14.
 * <p/>
 * This class allows to generate Latex tables with the results of applying the Wilcoxon Rank-Sum test
 */
public class SetCoverageTables implements IExperimentOutput {
  private File outputDirectory_;
  private String outputDirectoryName_;
  private Experiment experiment_;
  private FileWriter fileWriter_ ;


  public SetCoverageTables(Experiment experiment) {
    experiment_ = experiment;
    outputDirectory_ = null;
    outputDirectoryName_ = "";
    fileWriter_ = null ;
  }

  @Override
  public void generate() throws IOException {
    createOutputDirectory();

    String outputTexFile = outputDirectoryName_ + "/" + "setCoverage.tex";
    fileWriter_ = new FileWriter(outputTexFile, false) ;

    generateLatexHeader() ;
    for (String problem : experiment_.getProblemList()) {
      generateTable(problem) ;
    }
    generateLatexTail() ;

    fileWriter_.close();
  }

  private void generateLatexHeader() throws IOException {
    //FileWriter os = new FileWriter(fileName, false);
    fileWriter_.write("\\documentclass{article}" + "\n");
    fileWriter_.write("\\title{" + experiment_.getExperimentName() + "}" + "\n");
    fileWriter_.write("\\usepackage{colortbl}" + "\n");
    fileWriter_.write("\\usepackage[table*]{xcolor}" + "\n");
    fileWriter_.write("\\usepackage[margin=0.6in]{geometry}" + "\n");
    fileWriter_.write("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    fileWriter_.write("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    fileWriter_.write("\\author{}" + "\n");
    fileWriter_.write("\\begin{document}" + "\n");
    fileWriter_.write("\\maketitle" + "\n");
    fileWriter_.write("\\section{Tables}" + "\n");
  }

  private void generateTable(String problem) throws IOException {
    writeTableHeader(problem);

    for (int i = 0 ; i < experiment_.getAlgorithmNameList().length; i++) {
      writeTableRow(i, problem) ;
    }

    writeTableTail() ;
  }

  private void generateLatexTail() throws IOException {
    fileWriter_.write("\\end{document}" + "\n");
  }

  private void writeTableHeader(String problem) throws IOException {
    fileWriter_.write("\n\n");
    fileWriter_.write("\\begin{table}" + "\n");
    fileWriter_.write("\\caption{" + problem + ". Set Coverage}" + "\n");
    fileWriter_.write("\\label{table:setCoverage." + problem + "}" + "\n");
    fileWriter_.write("\\centering" + "\n");
    fileWriter_.write("\\begin{scriptsize}" + "\n");
    fileWriter_.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (int i = 0 ; i < experiment_.getAlgorithmNameList().length; i++) {
      fileWriter_.write("l");
    }
    fileWriter_.write("}\n");

    // write table head
    for (int i = -1; i < experiment_.getAlgorithmNameList().length; i++) {
      if (i == -1) {
        fileWriter_.write(" & ");
      } else if (i == (experiment_.getAlgorithmNameList().length - 1)) {
        fileWriter_.write(" " + experiment_.getAlgorithmNameList()[i] + "\\\\" + "\n");
      } else {
        fileWriter_.write("" + experiment_.getAlgorithmNameList()[i] + " & ");
      }
    }
    fileWriter_.write("\\hline" + "\n");
  }

  private void writeTableRow(int algorithmIndex, String problem) throws IOException {
    fileWriter_.write("" + experiment_.getAlgorithmNameList()[algorithmIndex] + " & ");
    for (int i = 0; i < experiment_.getAlgorithmNameList().length; i++) {
      if (i != algorithmIndex) {
        double setCoverageValueAB = calculateSetCoverage(
          problem,
          experiment_.getAlgorithmNameList()[algorithmIndex],
          experiment_.getAlgorithmNameList()[i]
        ) ;

        String setCoverageAB =  String.format(Locale.ENGLISH, "%.2f",
          setCoverageValueAB);

        fileWriter_.write("" + setCoverageAB);
        //String setCoverageBA =  String.format(Locale.ENGLISH, "%.2f",
        //  setCoverageValueBA);
        //fileWriter_.write("("+setCoverageAB +", " + setCoverageBA + ")"+ " & ");
      } else
        fileWriter_.write("--" );
      if (i < (experiment_.getAlgorithmNameList().length-1)) {
        fileWriter_.write("&") ;
      }
    }

    fileWriter_.write("\\\\" );
  }

  private void writeTableTail() throws IOException {
    fileWriter_.write("\\hline" + "\n");
    fileWriter_.write("\\end{tabular}" + "\n");
    fileWriter_.write("\\end{scriptsize}" + "\n");
    fileWriter_.write("\\end{table}" + "\n");
  }

  private double calculateSetCoverage(String problem, String algorithmA, String algorithmB) {
    double result = 5.5 ;
    SetCoverage setCoverageMetric = new SetCoverage() ;

    Vector<Double> values  = new Vector<Double>(experiment_.getIndependentRuns()) ;

    for (int i = 0; i < experiment_.getIndependentRuns(); i++) {
      String front1 = experiment_.getExperimentBaseDirectory()+"/data/"+algorithmA+"/"+problem+"/FUN."+i ;
      String front2 = experiment_.getExperimentBaseDirectory()+"/data/"+algorithmB+"/"+problem+"/FUN."+i ;
      result = setCoverageMetric.setCoverage(front1, front2) ;
      values.add(i, result);
    }

    return Statistics.calculateMean(values);
  }

  private void createOutputDirectory() {
    outputDirectoryName_ = "R";
    outputDirectoryName_ = experiment_.getExperimentBaseDirectory() + "/" + outputDirectoryName_;
    Configuration.logger_.info("R    : " + outputDirectoryName_);
    outputDirectory_ = new File(outputDirectoryName_);
    if (!outputDirectory_.exists()) {
      new File(outputDirectoryName_).mkdirs();
      Configuration.logger_.info("Creating " + outputDirectoryName_ + " directory");
    }
  }
}
