package org.uma.jmetal.utilities;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.uma.jmetal.util.StoredSolutionsUtils;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.point.PointSolution;

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

    NonDominatedSolutionListArchive<PointSolution> archive = new NonDominatedSolutionListArchive<>();
    List<String> fileNameList = new ArrayList<>();

    if (Files.isRegularFile(Paths.get(inputFileName))) {
      fileNameList.add(inputFileName);

    } else if (Files.isDirectory(Paths.get(inputFileName))) {

      fileNameList.addAll(Files
        .list(Paths.get(inputFileName))
        .map(Path::toString)
        .collect(toList()));
    } else {
      throw new JMetalException("Error opening file/directory") ;
    }

    int numberOfObjectives = determineNumberOfObjectives(fileNameList.get(0));
    for (String fileName: fileNameList) {
      System.out.println(fileName) ;
      archive.addAll(StoredSolutionsUtils.readSolutionsFromFile(fileName,numberOfObjectives)) ;
    }

    StoredSolutionsUtils.writeToOutput(archive.getSolutionList(), new DefaultFileOutputContext(outputFileName));
  }


  private static int determineNumberOfObjectives(String inputFileName) {
    Stream<String> lines ;

    try {
      lines = Files.lines(Paths.get(inputFileName), Charset.defaultCharset());
    } catch (IOException e) {
      throw new JMetalException(e) ;
    }

    int numberOfObjectives = lines.findFirst().get().split(" ").length ;
    lines.close();
    
    return numberOfObjectives;
  }
}
