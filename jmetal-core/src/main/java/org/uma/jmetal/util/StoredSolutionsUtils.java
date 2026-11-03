package org.uma.jmetal.util;

import static java.util.stream.Collectors.toList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.SolutionTextRepresentation;

public class StoredSolutionsUtils {
  private static final String DEFAULT_REGEX = "[ \t,]";
  private static final GenericSolutionAttribute<Solution<?>, String> textRepresentation = SolutionTextRepresentation.getAttribute();

  public static List<PointSolution> readSolutionsFromFile(String inputFileName, int numberOfObjectives) {
    // The stream is closed even if a line cannot be read; blank lines are skipped, and the spaces
    // at the beginning of a line are ignored
    try (Stream<String> lines = Files.lines(Paths.get(inputFileName), Charset.defaultCharset())) {
      return lines
          .filter(line -> !line.isBlank())
          .map(
              line -> {
                String[] textNumbers = line.strip().split(DEFAULT_REGEX, numberOfObjectives + 1);
                PointSolution solution = new PointSolution(numberOfObjectives);
                for (int i = 0; i < numberOfObjectives; i++) {
                  solution.objectives()[i] = Double.parseDouble(textNumbers[i]);
                }
                solution.attributes().put(textRepresentation, line);

                return solution;
              })
          .collect(toList());
    } catch (IOException e) {
      throw new JMetalException(e);
    }
  }

  public static <S extends Solution<?>> void writeToOutput(List<S> solutionList, FileOutputContext context) {
    BufferedWriter bufferedWriter = context.getFileWriter();

    try {
      for (Solution<?> s : solutionList) {
        String formatedTextRepresentation = (String)s.attributes().get(SolutionTextRepresentation.getAttribute());
        if (formatedTextRepresentation != null) {
          bufferedWriter.write((String) s.attributes().get(SolutionTextRepresentation.getAttribute()));
          bufferedWriter.newLine();
        } else {
          throw new JMetalException("Formatted text representation of the solution not stored as attribute");
        }
      }
      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error printing objecives to file: ", e);
    }
  }
}
