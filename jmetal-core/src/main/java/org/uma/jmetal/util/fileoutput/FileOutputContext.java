package org.uma.jmetal.util.fileoutput;

import java.io.BufferedWriter;
import java.io.Serializable;

/**
 * This interface represents output contexts, which are classes providing a mean for getting
 * a buffer reader object.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface FileOutputContext extends Serializable {
  public BufferedWriter getFileWriter() ;
  public String getSeparator() ;
  public void setSeparator(String separator) ;
  public String getFileName() ;
}
