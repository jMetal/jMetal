package org.uma.jmetal.util.fileoutput.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class  DefaultFileOutputContext implements FileOutputContext {
  private static final String DEFAULT_SEPARATOR = " " ;

  protected String fileName;
  protected String separator;

  public DefaultFileOutputContext(String fileName) {
    this.fileName = fileName ;
    this.separator = DEFAULT_SEPARATOR ;
  }

  @Override
  public BufferedWriter getFileWriter() {
    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(fileName);
    } catch (FileNotFoundException e) {
      throw new JMetalException("Exception when calling method getFileWriter()", e) ;
    }
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

    return new BufferedWriter(outputStreamWriter);
  }

  @Override
  public String getSeparator() {
    return separator;
  }

  @Override
  public void setSeparator(String separator) {
    this.separator = separator;
  }
}
