package org.uma.jmetal.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 * This utility takes an input file containing non-dominated solutions and extract a number N of
 * uniformly distributed solutions from them. The input data must be a CSV file, where each line is
 * a list of the values of each objective.
 *
 * The program receives three parameters: 1. the name of the file containing the data, 2. the output
 * file name 3. the number of solutions to select
 *
 * The output is a CSV file with the selected solutions
 */
public class ExtractSubsetOfParetoDominatedSolutionsFromFile {

  public static void main(String[] args) throws IOException {
    Check.that(args.length == 3,
        "Wrong number of arguments: "
            + args.length
            + "\nThis program should be called with three arguments:"
            + "\nThe first argument is the name of the file containing the input solutions."
            + "\nThe second argument is the name of the file containing the computed output."
            + "\nThe third argument is the number of solutions to select from the input file");

    String inputFileName = args[0];
    String outputFileName = args[1];
    int numberOfSolutions = Integer.parseInt(args[2]);

    Stream<String> lines = Files.lines(Path.of(inputFileName));

    List<PointSolution> solutions = lines
        .filter(line -> !line.equals(""))
        .map(line -> parseLineContainingObjectives(line))
        .map(vector -> new PointSolution(new ArrayPoint(vector).values()))
        .collect(
            Collectors.toList());

    Archive<PointSolution> archive = new BestSolutionsArchive<>(
        new NonDominatedSolutionListArchive<>(), numberOfSolutions);
    solutions.forEach(archive::add);

    new SolutionListOutput(archive.getSolutionList()).printObjectivesToFile(outputFileName, ",");
  }

  private static double[] parseLineContainingObjectives(String line) {
    String[] stringValues = line.split(",");
    double[] doubles = Arrays.stream(stringValues).mapToDouble(Double::parseDouble).toArray();

    return doubles;
  }
}
