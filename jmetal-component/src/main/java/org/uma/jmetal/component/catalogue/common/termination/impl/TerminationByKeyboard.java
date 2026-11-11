package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import java.util.Scanner;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.JMetalLogger;

/**
 * Termination condition that is met when the user hits return in the console. A daemon thread
 * waits for the line and then asks for the stop with a {@link TerminationByUserRequest}, so the
 * algorithm sees the request in its next check, and the thread does not keep the JVM alive if the
 * algorithm ends by another condition. If the standard input has no more lines, the condition is
 * never met.
 *
 * @author Antonio J. Nebro
 */
public class TerminationByKeyboard implements Termination {
  private final TerminationByUserRequest userRequest = new TerminationByUserRequest();
  private int evaluations;

  /** Creates the condition and starts the thread that waits for the user. */
  public TerminationByKeyboard() {
    Thread keyboardReader = new Thread(this::waitForAReturn, "TerminationByKeyboard");
    keyboardReader.setDaemon(true);
    keyboardReader.start();
  }

  private void waitForAReturn() {
    System.out.println("Press any key and hit return");
    // The scanner is not closed, because that would close the standard input of the program
    Scanner scanner = new Scanner(System.in);
    if (scanner.hasNextLine()) {
      scanner.nextLine();
      userRequest.requestStop();
    }
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    boolean stopRequested = userRequest.isMet(algorithmStatusData);
    if (stopRequested) {
      this.evaluations = (int) algorithmStatusData.get("EVALUATIONS");
      JMetalLogger.logger.info(() -> "Evaluations: " + evaluations);
    }
    return stopRequested;
  }

  /** The number of evaluations when the stop was detected, or 0 if it has not been detected. */
  public int getEvaluations() {
    return evaluations;
  }
}
