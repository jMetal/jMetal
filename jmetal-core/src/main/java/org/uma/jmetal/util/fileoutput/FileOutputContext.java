package org.uma.jmetal.util.fileoutput;

import java.io.BufferedWriter;
import java.io.Serializable;

/**
 * Created by Antonio J. Nebro on 13/10/14.
 */
public interface FileOutputContext extends Serializable {
  public BufferedWriter getFileWriter() ;
  public String getSeparator() ;
  public void setSeparator(String separator) ;
}
