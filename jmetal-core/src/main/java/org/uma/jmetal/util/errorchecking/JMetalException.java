package org.uma.jmetal.util.errorchecking;

import java.io.Serializable;
import java.util.logging.Level;
import org.uma.jmetal.util.JMetalLogger;

/**
 * jMetal exception class
 *
 * @author Antonio J. Nebro
 */
public class JMetalException extends RuntimeException implements Serializable {
  public JMetalException(String message) {
    super(message);
  }
  public JMetalException(Exception e) {
    JMetalLogger.logger.log(Level.SEVERE, "Error", e);
  }
  public JMetalException(String message, Exception e) {
    JMetalLogger.logger.log(Level.SEVERE, message, e);
  }

}
