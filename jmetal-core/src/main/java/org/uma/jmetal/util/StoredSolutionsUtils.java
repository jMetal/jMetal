package org.uma.jmetal.util;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.SolutionTextRepresentation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class StoredSolutionsUtils {
  private static final String DEFAULT_REGEX = "[ \t,]";
  private static final GenericSolutionAttribute<Solution<?>, String> textRepresentation = SolutionTextRepresentation.getAttribute();


  public static List<Solution<?>> readSolutionsFromFile(String inputFileName, int numberOfObjectives) {
    Stream<String> lines;

    try {
      lines = Files.lines(Paths.get(inputFileName), Charset.defaultCharset());
    } catch (IOException e) {
      throw new JMetalException(e);
    }

    DummyProblem dummyProblem = new DummyProblem(numberOfObjectives);

    List<Solution<?>> solutions = lines
      .map(line -> {
        String[] textNumbers = line.split(DEFAULT_REGEX, numberOfObjectives + 1);
        DoubleSolution solution = new DefaultDoubleSolution(dummyProblem);
        for (int i = 0; i < numberOfObjectives; i++) {
          solution.setObjective(i, Double.parseDouble(textNumbers[i]));
        }
        solution.setAttribute(textRepresentation, line);

        return solution;
      })
      .collect(toList());
    
    lines.close();

    return solutions;
  }

  public static void writeToOutput(NonDominatedSolutionListArchive<Solution<?>> archive, FileOutputContext context) {
    BufferedWriter bufferedWriter = context.getFileWriter();

    try {
      for (Solution<?> s : archive.getSolutionList()) {
        String formatedTextRepresentation = (String)s.getAttribute(SolutionTextRepresentation.getAttribute());
        if (formatedTextRepresentation != null) {
          bufferedWriter.write((String) s.getAttribute(SolutionTextRepresentation.getAttribute()));
          bufferedWriter.newLine();
        } else {
          throw new JMetalException("Formated text representation of the solution not stored as attribute");
        }
      }
      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error printing objecives to file: ", e);
    }
  }


  @SuppressWarnings("serial")
  private static class DummyProblem extends AbstractDoubleProblem {

    public DummyProblem(int numberOfObjectives) {
      this.setNumberOfObjectives(numberOfObjectives);
    }

    @Override
    public void evaluate(DoubleSolution solution) {

    }
  }
}
