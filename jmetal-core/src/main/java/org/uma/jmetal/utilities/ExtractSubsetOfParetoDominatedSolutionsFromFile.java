package org.uma.jmetal.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.point.PointSolution;
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

  public static void main(String @NotNull [] args) throws IOException {
    Check.that(args.length == 3,
        "Wrong number of arguments: "
            + args.length
            + "\nThis program should be called with three arguments:"
            + "\nThe first argument is the name of the file containing the input solutions."
            + "\nThe second argument is the name of the file containing the computed output."
            + "\nThe third argument is the number of solutions to select from the input file");

    var inputFileName = args[0];
    var outputFileName = args[1];
    var numberOfSolutions = Integer.parseInt(args[2]);

    var lines = Files.lines(Path.of(inputFileName));

    @NotNull List<PointSolution> solutions = lines
        .filter(line -> !line.equals(""))
        .map(ExtractSubsetOfParetoDominatedSolutionsFromFile::parseLineContainingObjectives)
        .map(vector -> new PointSolution(new ArrayPoint(vector)))
        .collect(
            Collectors.toList());

    Archive<PointSolution> archive = new BestSolutionsArchive<>(
        new NonDominatedSolutionListArchive<>(), numberOfSolutions);
    for (var solution : solutions) {
      archive.add(solution);
    }

    new SolutionListOutput(archive.getSolutionList()).printObjectivesToFile(outputFileName, ",");
  }

  private static double[] parseLineContainingObjectives(String line) {
    var stringValues = line.split(",");
    var doubles = new double[10];
    var count = 0;
    for (var stringValue : stringValues) {
      var v = Double.parseDouble(stringValue);
      if (doubles.length == count) doubles = Arrays.copyOf(doubles, count * 2);
      doubles[count++] = v;
    }
    doubles = Arrays.copyOfRange(doubles, 0, count);

    return doubles;
  }
}
