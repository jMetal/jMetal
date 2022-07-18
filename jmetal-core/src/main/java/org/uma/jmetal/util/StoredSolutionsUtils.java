package org.uma.jmetal.util;

import static java.util.stream.Collectors.toList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.SolutionTextRepresentation;

public class StoredSolutionsUtils {
  private static final String DEFAULT_REGEX = "[ \t,]";
  private static final GenericSolutionAttribute<Solution<?>, String> textRepresentation = SolutionTextRepresentation.getAttribute();

  public static @NotNull List<PointSolution> readSolutionsFromFile(String inputFileName, int numberOfObjectives) {
    Stream<String> lines;

    try {
      lines = Files.lines(Paths.get(inputFileName), Charset.defaultCharset());
    } catch (IOException e) {
      throw new JMetalException(e);
    }

    @NotNull List<PointSolution> solutions = lines
      .map(line -> {
        var textNumbers = line.split(DEFAULT_REGEX, numberOfObjectives + 1);
        var solution = new PointSolution(numberOfObjectives);
        for (var i = 0; i < numberOfObjectives; i++) {
          solution.objectives()[i] = Double.parseDouble(textNumbers[i]);
        }
        solution.attributes().put(textRepresentation, line);

        return solution;
      })
      .collect(toList());
    
    lines.close();

    return solutions;
  }

  public static <S extends Solution<?>> void writeToOutput(List<S> solutionList, @NotNull FileOutputContext context) {
    var bufferedWriter = context.getFileWriter();

    try {
      for (Solution<?> s : solutionList) {
        var formatedTextRepresentation = (String)s.attributes().get(SolutionTextRepresentation.getAttribute());
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
