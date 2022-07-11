package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import java.util.Scanner;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.JMetalLogger;

/**
 * Class that allows to check the termination condition based on introducing a character by keyboard.
 *
 *  @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TerminationByKeyboard implements Termination {
  public boolean keyHit ;
  private int evaluations ;

  public TerminationByKeyboard() {
    keyHit = false;
    evaluations = 0 ;

    KeyboardReader keyboardReader = new KeyboardReader(this) ;
    keyboardReader.start();
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    if (keyHit) {
      this.evaluations = (int)algorithmStatusData.get("EVALUATIONS") ;
      JMetalLogger.logger.info("Evaluations: " + evaluations);
    }
    return keyHit ;
  }

  private class KeyboardReader extends Thread {
    private TerminationByKeyboard terminationByKeyboard ;

    public KeyboardReader(TerminationByKeyboard terminationByKeyboard) {
      this.terminationByKeyboard = terminationByKeyboard ;
    }

    @Override
    public void run() {
      System.out.println("Press any key and hit return") ;
      try (Scanner scanner = new Scanner(System.in)) {
        scanner.nextLine() ;
      }

      terminationByKeyboard.keyHit = true ;
    }
  }

  public int getEvaluations() {
    return evaluations ;
  }
}
