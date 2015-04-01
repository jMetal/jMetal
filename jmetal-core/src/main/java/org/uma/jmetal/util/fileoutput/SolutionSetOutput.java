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

package org.uma.jmetal.util.fileoutput;

import org.uma.jmetal.core.SolutionSet;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class SolutionSetOutput {

  public static class Printer {
    FileOutputContext varFileContext;
    FileOutputContext funFileContext;
    private String varFileName = "VAR";
    private String funFileName = "FUN";
    String separator = "\t";
    SolutionSet solutionSet;
    boolean selectFeasibleSolutions;

    public Printer(SolutionSet solutionSet) throws FileNotFoundException {
      varFileContext = new DefaultFileOutputContext(varFileName);
      funFileContext = new DefaultFileOutputContext(funFileName);
      varFileContext.separator = separator;
      funFileContext.separator = separator;
      this.solutionSet = solutionSet;
      selectFeasibleSolutions = false;
    }

    public Printer setVarFileOutputContext(FileOutputContext fileContext) {
      varFileContext = fileContext;

      return this;
    }

    public Printer setFunFileOutputContext(FileOutputContext fileContext) {
      funFileContext = fileContext;

      return this;
    }

    public Printer selectFeasibleSolutions() {
      solutionSet = solutionSet.getFeasibleSolutions() ;

      return this;
    }

    public Printer setSeparator(String separator) {
      this.separator = separator;
      varFileContext.separator = this.separator;
      funFileContext.separator = this.separator;

      return this;
    }

    public void print() throws IOException {
      printObjectivesToFile(funFileContext, solutionSet);
      printVariablesToFile(varFileContext, solutionSet);
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
