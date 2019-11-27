package org.uma.jmetal.util.observer.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This observer stores a solution list in files. Concretely, the variables and objectives are written in files called
 * VAR.x.tsv and VAR.x.tsv, respectively (x is an iteration counter). The frequency of the writes are set by a
 * parameter.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class WriteSolutionsToFilesObserver implements Observer<Map<String, Object>> {

  private Integer frequency ;
  private int counter ;
  private String outputDirectory = "" ;
  /**
   * Constructor
   */

  public WriteSolutionsToFilesObserver(Integer frequency, String outputDirectory) {
    this.frequency = frequency ;
    this.counter = 0 ;
    this.outputDirectory = outputDirectory ;

    File file = new File(outputDirectory);

    if (file.exists()) {
      if (file.isFile()) {
        throw new RuntimeException(outputDirectory + " exists and it is a file");
      } else if (file.isDirectory()) {
        throw new RuntimeException(outputDirectory+ " exists and it is a directory");
      }
    } else {
      if (!file.mkdir()) {
        throw new RuntimeException("Unable to create the directory") ;
      }
    }
  }

  public WriteSolutionsToFilesObserver() {
    this(1, "outputDirectory") ;
  }

  /**
   * This method gets the population
   * @param data Map of pairs (key, value)
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    List<?> population = (List<?>) data.get("POPULATION");

    if (population!=null) {
      if (counter % frequency == 0) {
        new SolutionListOutput((List<? extends Solution<?>>) population)
            .setVarFileOutputContext(new DefaultFileOutputContext(outputDirectory + "/VAR." + counter + ".tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext(outputDirectory + "/FUN." + counter + ".tsv"))
            .print();
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName()+ ": The POPULATION is null");
    }

    counter ++ ;
  }

  public String getName() {
    return "Print objectives observer";
  }

  @Override
  public String toString() {
    return getName() ;
  }
}
