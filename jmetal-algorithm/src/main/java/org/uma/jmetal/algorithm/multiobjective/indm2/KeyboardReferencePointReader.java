package org.uma.jmetal.algorithm.multiobjective.indm2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Reads the reference points of the decision maker of an {@link InDM2} from a text input (the
 * console by default), one line per change: the values separated by commas, as many as objectives
 * times the number of points (e.g. "0.2, 0.5, 0.4, 1.0" gives two points with two objectives). A
 * line with "q" stops the algorithm. A wrong line is reported and ignored. It reads in a daemon
 * thread, so it does not keep the JVM alive.
 */
public class KeyboardReferencePointReader {
  private final InDM2<?> indm2;
  private final InputStream input;

  /** Creates a reader of the console; it starts with {@link #start()}. */
  public KeyboardReferencePointReader(InDM2<?> indm2) {
    this(indm2, System.in);
  }

  /** Creates a reader of the given input; it starts with {@link #start()}. */
  public KeyboardReferencePointReader(InDM2<?> indm2, InputStream input) {
    Check.notNull(indm2);
    Check.notNull(input);
    this.indm2 = indm2;
    this.input = input;
  }

  /** Starts reading in a daemon thread, which is returned. */
  public Thread start() {
    Thread thread = new Thread(this::read, "KeyboardReferencePointReader");
    thread.setDaemon(true);
    thread.start();
    return thread;
  }

  private void read() {
    // The reader is not closed, because that would close the input, which may be System.in
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    System.out.println(
        "Type a new reference point (values separated by commas), or q to stop the algorithm:");
    try {
      String line = reader.readLine();
      while (line != null) {
        if (line.strip().equalsIgnoreCase("q")) {
          indm2.requestStop();
          return;
        }
        if (!line.isBlank()) {
          take(line);
        }
        line = reader.readLine();
      }
    } catch (IOException exception) {
      JMetalLogger.logger.warning("The reference points cannot be read: " + exception);
    }
  }

  private void take(String line) {
    try {
      indm2.updateReferencePoints(parse(line, indm2.problem().numberOfObjectives()));
    } catch (RuntimeException exception) {
      JMetalLogger.logger.warning(
          "Wrong reference point \"" + line + "\": " + exception.getMessage());
    }
  }

  /** The reference points of a line with the values separated by commas. */
  static List<List<Double>> parse(String line, int numberOfObjectives) {
    List<Double> values =
        Arrays.stream(line.split(",")).map(String::strip).map(Double::valueOf).toList();
    Check.that(
        !values.isEmpty() && values.size() % numberOfObjectives == 0,
        () -> "The number of values must be a multiple of " + numberOfObjectives);
    List<List<Double>> points = new ArrayList<>();
    for (int i = 0; i < values.size(); i += numberOfObjectives) {
      points.add(values.subList(i, i + numberOfObjectives));
    }
    return points;
  }
}
