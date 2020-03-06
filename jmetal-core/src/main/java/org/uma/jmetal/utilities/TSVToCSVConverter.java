package org.uma.jmetal.utilities;

import org.uma.jmetal.util.JMetalException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * This utility reads a TSV file and generates another file in CSV format
 *
 * <p>The program receives two parameters: 1. The name of the input TSV file 2. The name of the
 * output CSV file
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TSVToCSVConverter {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new JMetalException("Wrong number of arguments: two file names are required.");
    }

    String inputFileName = args[0];
    String outputFileName = args[1];

    Stream<String> lines;
    BufferedWriter outputFile;

    try {
      lines = Files.lines(Paths.get(inputFileName), Charset.defaultCharset());
      outputFile = Files.newBufferedWriter(Paths.get(outputFileName));
      lines.forEach(
          line -> {
            // List<String> values = Arrays.asList(l.split("\\s+")) ;
            String values = line.replaceAll("\\s+", ",");

            if (values.substring(values.length() - 1).equals(",")) {
              values = values.substring(0, values.length() - 1);
            }
            try {
              outputFile.write(values);
              outputFile.write("\n");
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
    } catch (IOException e) {
      throw new JMetalException(e);
    }

    lines.close();
    outputFile.close();
  }
}
