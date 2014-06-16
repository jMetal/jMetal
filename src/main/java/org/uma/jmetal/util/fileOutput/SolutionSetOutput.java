package org.uma.jmetal.util.fileOutput;

import org.uma.jmetal.core.SolutionSet;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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

  public static class Printer {
    FileOutputContext varFileContext_ ;
    FileOutputContext funFileContext_ ;
    private String varFileName_ = "VAR" ;
    private String funFileName_ = "FUN" ;
    String separator_ ="\t" ;
    SolutionSet solutionSet_ ;

    public Printer(SolutionSet solutionSet) throws FileNotFoundException {
      varFileContext_ = new DefaultFileOutputContext(varFileName_) ;
      funFileContext_ = new DefaultFileOutputContext(funFileName_) ;
      varFileContext_.separator_ = separator_ ;
      funFileContext_.separator_ = separator_ ;
      solutionSet_ = solutionSet ;
    }

    public Printer varFileOutputContext(FileOutputContext fileContext) {
      varFileContext_ = fileContext ;

      return this ;
    }

    public Printer funFileOutputContext(FileOutputContext fileContext) {
      funFileContext_ = fileContext ;

      return this ;
    }

    public Printer separator(String separator) {
      separator_ = separator ;
      varFileContext_.separator_ = separator_ ;
      funFileContext_.separator_ = separator_ ;

      return this ;
    }

    public void print() throws IOException {
      printObjectivesToFile(funFileContext_, solutionSet_);
      printVariablesToFile(varFileContext_, solutionSet_);
    }
  }
}
