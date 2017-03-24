package org.uma.jmetal.utility;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import javafx.scene.shape.Arc;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * This utility reads a file or the files in a directory and creates a reference front.
 * The file(s) must contain only objective values.
 *
 * The program receives two parameters:
 * 1. the name of the file or directory containing the data
 * 2. the output file name which will contain the generated front
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceFrontFromFile {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new JMetalException("Wrong number of arguments: two file names are required.");
    }

    String inputFileName = args[0] ;
    String outputFileName = args[1] ;

    NonDominatedSolutionListArchive<DoubleSolution> archive = null;

    if (Files.isRegularFile(Paths.get(inputFileName))) {
      archive = readDataFromFile(inputFileName) ;

    } else if (Files.isDirectory(Paths.get(inputFileName))) {

      List<String> fileNameList = Files
              .list(Paths.get(inputFileName))
              .map(s -> s.toString())
              .collect(toList());

      archive = new NonDominatedSolutionListArchive<>() ;
      for (String fileName: fileNameList) {
        System.out.println(fileName) ;
        archive.join(readDataFromFile(fileName)) ;
      }
    } else {
      throw new JMetalException("Error opening file/directory") ;
    }

    new SolutionListOutput(archive.getSolutionList())
            .setSeparator("\t")
            .setFunFileOutputContext(new DefaultFileOutputContext(outputFileName))
            .print();
  }

  private static NonDominatedSolutionListArchive<DoubleSolution> readDataFromFile(String inputFileName)  {
    Stream<String> lines ;

    try {
      lines = Files.lines(Paths.get(inputFileName), Charset.defaultCharset());
    } catch (IOException e) {
      throw new JMetalException(e) ;
    }

    List<List<Double>> numbers = lines
            .map(line -> {
              String[] textNumbers = line.split(" ") ;
              List<Double> listOfNumbers = new ArrayList<>() ;
              for (String number : textNumbers) {
                listOfNumbers.add(Double.parseDouble(number)) ;
              }

              return listOfNumbers ;
            })
            .collect(toList()) ;

    int numberOfObjectives = numbers.get(0).size() ;
    DummyProblem dummyProblem = new DummyProblem(numberOfObjectives);

    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    numbers
            .stream()
            .forEach(list -> {
              DoubleSolution solution = new DefaultDoubleSolution(dummyProblem);
              for (int i = 0; i < numberOfObjectives; i++) {
                solution.setObjective(i, list.get(i));
              }
              archive.add(solution) ;
            });


    return archive ;
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
