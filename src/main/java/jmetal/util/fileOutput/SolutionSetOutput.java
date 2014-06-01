package jmetal.util.fileOutput;

import jmetal.core.SolutionSet;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class SolutionSetOutput {

  static public void printVariablesToFile(FileOutputContext context, SolutionSet solutionSet) throws IOException {
    BufferedWriter bufferedWriter = context.getFileWriter() ;

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

  static public void printObjectivesToFile(FileOutputContext context, SolutionSet solutionSet) throws IOException {
    BufferedWriter bufferedWriter = context.getFileWriter() ;

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        bufferedWriter.write(solutionSet.get(i).getObjective(j) + context.getSeparator());
      }
      bufferedWriter.newLine();
    }
    bufferedWriter.close();
  }
}
