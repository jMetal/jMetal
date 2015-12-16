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
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SolutionListOutput {
  private FileOutputContext varFileContext;
  private FileOutputContext funFileContext;
  private String varFileName = "VAR";
  private String funFileName = "FUN";
  private String separator = "\t";
  private List<? extends Solution<?>> solutionList;
  private boolean selectFeasibleSolutions;

  public SolutionListOutput(List<? extends Solution<?>> solutionList) {
    varFileContext = new DefaultFileOutputContext(varFileName);
    funFileContext = new DefaultFileOutputContext(funFileName);
    varFileContext.setSeparator(separator);
    funFileContext.setSeparator(separator);
    this.solutionList = solutionList;
    selectFeasibleSolutions = false;
  }

  public SolutionListOutput setVarFileOutputContext(FileOutputContext fileContext) {
    varFileContext = fileContext;

    return this;
  }

  public SolutionListOutput setFunFileOutputContext(FileOutputContext fileContext) {
    funFileContext = fileContext;

    return this;
  }

  public SolutionListOutput selectFeasibleSolutions() {
    selectFeasibleSolutions = true ;
    return this;
  }

  public SolutionListOutput setSeparator(String separator) {
    this.separator = separator;
    varFileContext.setSeparator(this.separator);
    funFileContext.setSeparator(this.separator);

    return this;
  }

  public void print()  {
    printObjectivesToFile(funFileContext, solutionList);
    printVariablesToFile(varFileContext, solutionList);
  }

  public void printVariablesToFile(FileOutputContext context, List<? extends Solution<?>> solutionSet) {
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

  void printObjectivesToFile(FileOutputContext context, List<? extends Solution<?>> solutionSet) {
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
  public void printObjectivesToFile(String fileName) throws IOException {
    printObjectivesToFile(new DefaultFileOutputContext(fileName), solutionList);
  }

  public void printVariablesToFile(String fileName) throws IOException {
    printVariablesToFile(new DefaultFileOutputContext(fileName), solutionList);
  }

}
