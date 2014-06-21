//  SolutionSetOutput.Java
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

package org.uma.jmetal.util.fileOutput;

import org.uma.jmetal.core.SolutionSet;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class SolutionSetOutput {

  public static class Printer {
    FileOutputContext varFileContext_;
    FileOutputContext funFileContext_;
    private String varFileName_ = "VAR";
    private String funFileName_ = "FUN";
    String separator_ = "\t";
    SolutionSet solutionSet_;
    boolean selectFeasibleSolutions_;

    public Printer(SolutionSet solutionSet) throws FileNotFoundException {
      varFileContext_ = new DefaultFileOutputContext(varFileName_);
      funFileContext_ = new DefaultFileOutputContext(funFileName_);
      varFileContext_.separator_ = separator_;
      funFileContext_.separator_ = separator_;
      solutionSet_ = solutionSet;
      selectFeasibleSolutions_ = false;
    }

    public Printer varFileOutputContext(FileOutputContext fileContext) {
      varFileContext_ = fileContext;

      return this;
    }

    public Printer funFileOutputContext(FileOutputContext fileContext) {
      funFileContext_ = fileContext;

      return this;
    }

    public Printer selectFeasibleSolutions() {
      solutionSet_ = solutionSet_.getFeasibleSolutions() ;

      return this;
    }

    public Printer separator(String separator) {
      separator_ = separator;
      varFileContext_.separator_ = separator_;
      funFileContext_.separator_ = separator_;

      return this;
    }

    public void print() throws IOException {
      printObjectivesToFile(funFileContext_, solutionSet_);
      printVariablesToFile(varFileContext_, solutionSet_);
    }
  }

  static public void printVariablesToFile(FileOutputContext context, SolutionSet solutionSet)
    throws IOException {
    BufferedWriter bufferedWriter = context.getFileWriter();

    int numberOfVariables = solutionSet.get(0).getDecisionVariables().length;
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < numberOfVariables; j++) {
        bufferedWriter.write(solutionSet.get(i).getDecisionVariables()[j].toString() +
          context.getSeparator());
      }
      bufferedWriter.newLine();
    }
    bufferedWriter.close();
  }

  static public void printObjectivesToFile(FileOutputContext context, SolutionSet solutionSet)
    throws IOException {
    BufferedWriter bufferedWriter = context.getFileWriter();

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives();
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        bufferedWriter.write(solutionSet.get(i).getObjective(j) + context.getSeparator());
      }
      bufferedWriter.newLine();
    }
    bufferedWriter.close();
  }

  /*
   * Wrappers for printing with default configuration
   */
  public static void printObjectivesToFile(SolutionSet solutionSet, String fileName) throws IOException {
    printObjectivesToFile(new DefaultFileOutputContext(fileName), solutionSet);
  }

  public static void printVariablesToFile(SolutionSet solutionSet, String fileName) throws IOException {
    printVariablesToFile(new DefaultFileOutputContext(fileName), solutionSet);
  }

  public static void printFeasibleObjectivesToFile(SolutionSet solutionSet, String fileName) throws IOException {
    printObjectivesToFile(new DefaultFileOutputContext(fileName), solutionSet.getFeasibleSolutions());
  }

  public static void printFeasibleVariablesToFile(SolutionSet solutionSet, String fileName) throws IOException {
    printVariablesToFile(new DefaultFileOutputContext(fileName), solutionSet.getFeasibleSolutions());
  }
}
