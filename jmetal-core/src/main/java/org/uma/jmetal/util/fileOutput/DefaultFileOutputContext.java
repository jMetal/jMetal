package org.uma.jmetal.util.fileOutput;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class DefaultFileOutputContext extends FileOutputContext {
  public DefaultFileOutputContext(String fileName) {
    this.fileName = fileName ;
  }

  @Override
  public BufferedWriter getFileWriter() throws FileNotFoundException {
    FileOutputStream outputStream = new FileOutputStream(fileName);
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter) ;

    return bufferedWriter;
  }
}
