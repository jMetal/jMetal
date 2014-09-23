package org.uma.jmetal3.util.fileoutput;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public abstract class FileOutputContext {
  protected static final String DEFAULT_SEPARATOR = " " ;

  protected String fileName;
  protected String separator;

  public FileOutputContext() {
    separator = DEFAULT_SEPARATOR;
  }

  public abstract BufferedWriter getFileWriter() throws FileNotFoundException;

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }
}
