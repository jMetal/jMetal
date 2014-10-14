package org.uma.jmetal.util.fileoutput;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;

/**
 * Created by Antonio J. Nebro on 13/10/14.
 */
public interface FileOutputContext {
  public BufferedWriter getFileWriter() throws FileNotFoundException;
  public String getSeparator() ;
  public void setSeparator(String separator) ;
}
