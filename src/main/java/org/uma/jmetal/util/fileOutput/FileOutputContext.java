package org.uma.jmetal.util.fileOutput;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public abstract class FileOutputContext {
  protected static final String DEFAULT_SEPARATOR = " " ;

  protected String fileName_ ;
  protected String separator_ ;

  public FileOutputContext() {
    separator_ = DEFAULT_SEPARATOR;
  }

  public abstract BufferedWriter getFileWriter() throws FileNotFoundException;

  public String getSeparator() {
    return separator_;
  }

  public void setSeparator(String separator) {
    separator_ = separator;
  }
}
