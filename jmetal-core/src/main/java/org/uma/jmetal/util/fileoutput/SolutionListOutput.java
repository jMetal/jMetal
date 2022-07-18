package org.uma.jmetal.util.fileoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class SolutionListOutput {
  private FileOutputContext varFileContext;
  private FileOutputContext funFileContext;
  private String varFileName = "VAR";
  private @NotNull String funFileName = "FUN";
  private List<? extends Solution<?>> solutionList;
  private List<Boolean> isObjectiveToBeMinimized;

  public SolutionListOutput(List<? extends Solution<?>> solutionList) {
    varFileContext = new DefaultFileOutputContext(varFileName);
    funFileContext = new DefaultFileOutputContext(funFileName);
    this.solutionList = solutionList;
    isObjectiveToBeMinimized = null;
  }

  public SolutionListOutput setVarFileOutputContext(FileOutputContext fileContext) {
    varFileContext = fileContext;

    return this;
  }

  public @NotNull SolutionListOutput setFunFileOutputContext(FileOutputContext fileContext) {
    funFileContext = fileContext;

    return this;
  }

  public SolutionListOutput setObjectiveMinimizingObjectiveList(
      List<Boolean> isObjectiveToBeMinimized) {
    this.isObjectiveToBeMinimized = isObjectiveToBeMinimized;

    return this;
  }

  public void print() {
    if (isObjectiveToBeMinimized == null) {
      printObjectivesToFile(funFileContext, solutionList);
    } else {
      printObjectivesToFile(funFileContext, solutionList, isObjectiveToBeMinimized);
    }
    printVariablesToFile(varFileContext, solutionList);
  }

  public void printVariablesToFile(
      FileOutputContext context, List<? extends Solution<?>> solutionList) {
    var bufferedWriter = context.getFileWriter();

    try {
      if (solutionList.size() > 0) {
        var numberOfVariables = solutionList.get(0).variables().size();
        for (var i = 0; i < solutionList.size(); i++) {
          for (var j = 0; j < numberOfVariables - 1; j++) {
            bufferedWriter.write("" + solutionList.get(i).variables().get(j) + context.getSeparator());
          }
          bufferedWriter.write(
              "" + solutionList.get(i).variables().get(numberOfVariables - 1));

          bufferedWriter.newLine();
        }
      }

      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error writing data ", e);
    }
  }

  public void printObjectivesToFile(
          FileOutputContext context, @NotNull List<? extends Solution<?>> solutionList) {
    var bufferedWriter = context.getFileWriter();

    try {
      if (solutionList.size() > 0) {
        var numberOfObjectives = solutionList.get(0).objectives().length;
        for (var i = 0; i < solutionList.size(); i++) {
          for (var j = 0; j < numberOfObjectives - 1; j++) {
            bufferedWriter.write(solutionList.get(i).objectives()[j] + context.getSeparator());
          }
          bufferedWriter.write("" + solutionList.get(i).objectives()[numberOfObjectives - 1]);

          bufferedWriter.newLine();
        }
      }

      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error printing objectives to file: ", e);
    }
  }

  public void printObjectivesToFile(
          FileOutputContext context,
          List<? extends Solution<?>> solutionList,
          @NotNull List<Boolean> minimizeObjective) {
    var bufferedWriter = context.getFileWriter();

    try {
      if (solutionList.size() > 0) {
        var numberOfObjectives = solutionList.get(0).objectives().length;
        if (numberOfObjectives != minimizeObjective.size()) {
          throw new JMetalException(
              "The size of list minimizeObjective is not correct: " + minimizeObjective.size());
        }
        for (var i = 0; i < solutionList.size(); i++) {
          for (var j = 0; j < numberOfObjectives - 1; j++) {
            if (minimizeObjective.get(j)) {
              bufferedWriter.write(solutionList.get(i).variables().get(j) + context.getSeparator());
            } else {
              bufferedWriter.write(
                  -1.0 * solutionList.get(i).objectives()[j] + context.getSeparator());
            }
          }
          bufferedWriter.write(
              "" + -1.0 * solutionList.get(i).objectives()[numberOfObjectives - 1]);

          bufferedWriter.newLine();
        }
      }

      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error printing objecives to file: ", e);
    }
  }

  /*
   * Wrappers for printing with default configuration
   */
  public void printObjectivesToFile(String fileName) {
    printObjectivesToFile(new DefaultFileOutputContext(fileName), solutionList);
  }

  public void printObjectivesToFile(String fileName, String separator) {
    printObjectivesToFile(new DefaultFileOutputContext(fileName, separator), solutionList);
  }

  public void printObjectivesToFile(String fileName, List<Boolean> minimizeObjective) {
    printObjectivesToFile(new DefaultFileOutputContext(fileName), solutionList, minimizeObjective);
  }

  public void printObjectivesToFile(String fileName, @NotNull List<Boolean> minimizeObjective, String separator) {
    printObjectivesToFile(new DefaultFileOutputContext(fileName, separator), solutionList, minimizeObjective);
  }

  public void printVariablesToFile(String fileName) {
    printVariablesToFile(new DefaultFileOutputContext(fileName), solutionList);
  }

  public void printVariablesToFile(String fileName, String separator) {
    printVariablesToFile(new DefaultFileOutputContext(fileName, separator), solutionList);
  }
}
