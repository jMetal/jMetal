package jmetal.util.fileOutput;

import java.io.BufferedWriter;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public abstract class FileOutputContext {
  protected static final String DEFAULT_SEPARATOR = "," ;

  protected String fileName_ ;
  protected BufferedWriter bufferedWriter_ ;

  protected String separator_ ;

  public FileOutputContext() {
    fileName_ = null ;
    bufferedWriter_ = null ;
    separator_ = DEFAULT_SEPARATOR;
  }

  public FileOutputContext(String fileName) {
    fileName_ = fileName ;
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
