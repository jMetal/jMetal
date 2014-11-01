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

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
@SuppressWarnings("ALL")
public class SolutionSetOutput {

  public static class Printer {
    private FileOutputContext varFileContext;
    private FileOutputContext funFileContext;
    private String varFileName = "VAR";
    private String funFileName = "FUN";
    private String separator = "\t";
    private List<Solution> solutionSet;
    private boolean selectFeasibleSolutions;

    public Printer(List<? extends Solution> solutionSet)  {
      varFileContext = new DefaultFileOutputContext(varFileName);
      funFileContext = new DefaultFileOutputContext(funFileName);
      varFileContext.setSeparator(separator);
      funFileContext.setSeparator(separator);
      this.solutionSet = (List<Solution>) solutionSet;
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

      return this;
    }

    public Printer setSeparator(String separator) {
      this.separator = separator;
      varFileContext.setSeparator(this.separator);
      funFileContext.setSeparator(this.separator);

      return this;
    }

    public void print()  {
        printObjectivesToFile(funFileContext, solutionSet);
        printVariablesToFile(varFileContext, solutionSet);
    }
  }

  static public void printVariablesToFile(FileOutputContext context, List<Solution> solutionSet) {
    BufferedWriter bufferedWriter = context.getFileWriter();

    int numberOfVariables = solutionSet.get(0).getNumberOfVariables();
    try {
      for (int i = 0; i < solutionSet.size(); i++) {
        for (int j = 0; j < numberOfVariables; j++) {
          bufferedWriter.write(solutionSet.get(i).getVariableValueString(j) + context.getSeparator());
        }
        bufferedWriter.newLine();
      }
      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Exception when printing variables to file", e) ;
    }
  }

  static public void printObjectivesToFile(FileOutputContext context, List<Solution> solutionSet) {
    BufferedWriter bufferedWriter = context.getFileWriter();

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives();
    try {
      for (int i = 0; i < solutionSet.size(); i++) {
        for (int j = 0; j < numberOfObjectives; j++) {
          bufferedWriter.write(solutionSet.get(i).getObjective(j) + context.getSeparator());
        }
        bufferedWriter.newLine();
      }
      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Exception when printing objectives to file", e) ;
    }
  }

  /*
   * Wrappers for printing with default configuration
   */
  public static void printObjectivesToFile(List<Solution> solutionSet, String fileName) throws IOException {
    printObjectivesToFile(new DefaultFileOutputContext(fileName), solutionSet);
  }

  public static void printVariablesToFile(List<Solution> solutionSet, String fileName) throws IOException {
    printVariablesToFile(new DefaultFileOutputContext(fileName), solutionSet);
  }

}
