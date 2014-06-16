package org.uma.jmetal.util.fileOutput;

import java.io.BufferedWriter;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public abstract class FileOutputContext {
  protected static final String DEFAULT_SEPARATOR = " " ;

  protected BufferedWriter bufferedWriter_ ;

  protected String separator_ ;

  public FileOutputContext() {
    bufferedWriter_ = null ;
    separator_ = DEFAULT_SEPARATOR;
  }

  public BufferedWriter getFileWriter() {
    return bufferedWriter_ ;
  }

  public String getSeparator() {
    return separator_;
  }

  public void setSeparator(String separator) {
    separator_ = separator;
  }
}
