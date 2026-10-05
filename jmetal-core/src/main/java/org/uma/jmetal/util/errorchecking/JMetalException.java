package org.uma.jmetal.util.errorchecking;

import java.io.Serializable;

/**
 * jMetal exception class
 *
 * <p>The constructors that take a cause keep it, and keep the message: whoever catches the
 * exception (or prints it, if nobody does) gets what went wrong and why.
 *
 * @author Antonio J. Nebro
 */
public class JMetalException extends RuntimeException implements Serializable {
  public JMetalException(String message) {
    super(message);
  }

  public JMetalException(Exception e) {
    super(e);
  }

  public JMetalException(String message, Exception e) {
    super(message, e);
  }
}
