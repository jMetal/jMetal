package org.uma.jmetal.utility;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
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

/**
 * This utility takes an input file and produces an output file containing only the non-dominated solutions in the
 * input file
 *
 * Each line of the file represents a solution. The format of each of these solutions is rather flexible and the
 * only requirement is that for an n-objective problem, at least the first n columns must be numerical values
 * representing these objectives values. By default columns can be separated by blank spaces, tabs, or commas (cvs)
 *
 * The program receives three parameters:
 * 1. the name of the file containing the data
 * 2. the output file name which will contain the generated front
 * 3. the number of objectives
 *
 */
public class ExtractParetoDominatingSolutionsFromFile {

  private static final String DEFAULT_REGEX = "[ \t,]";
  private static final GenericSolutionAttribute<Solution<?>, String> textRepresentation = new SolutionTextRepresentation();

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      throw new JMetalException("Wrong number of arguments: " + args.length +
        "\nThis program should be called with three arguments:" +
        "\nThe first argument is the name of the file containing the input solutions." +
        "\nThe second argument is the name of the file containing the computed output." +
        "\nThe third argument is the number of objectives of the problem whose front is to be extracted.");
    }

    String inputFileName = args[0];
    String outputFileName = args[1];
    Integer numberOfObjectives = Integer.parseInt(args[2]);

    NonDominatedSolutionListArchive<Solution<?>> archive = null;

    if (Files.isRegularFile(Paths.get(inputFileName))) {
      archive = nonDominatedFromList(readSolutionsFromFile(inputFileName, numberOfObjectives));
    } else {
      throw new JMetalException("Error opening file " + inputFileName);
    }

    writeToOutput(archive, new DefaultFileOutputContext(outputFileName));
  }

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

    return solutions;
  }


  public static NonDominatedSolutionListArchive<Solution<?>> nonDominatedFromList(List<Solution<?>> list) {
    NonDominatedSolutionListArchive<Solution<?>> archive = new NonDominatedSolutionListArchive<>();

    for (Solution<?> s : list)
      archive.add(s);

    return archive;
  }

  public static void writeToOutput(NonDominatedSolutionListArchive<Solution<?>> archive, FileOutputContext context) {
    BufferedWriter bufferedWriter = context.getFileWriter();

    try {
      for (Solution<?> s : archive.getSolutionList()) {
        bufferedWriter.write((String) s.getAttribute(textRepresentation));
        bufferedWriter.newLine();
      }
      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error printing objecives to file: ", e);
    }
  }

  private static class DummyProblem extends AbstractDoubleProblem {

    public DummyProblem(int numberOfObjectives) {
      this.setNumberOfObjectives(numberOfObjectives);
    }

    @Override
    public void evaluate(DoubleSolution solution) {

    }
  }
}