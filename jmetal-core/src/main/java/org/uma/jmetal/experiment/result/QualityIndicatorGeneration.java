package org.uma.jmetal.experiment.result;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentResult;
import org.uma.jmetal.qualityIndicator.Epsilon;
import org.uma.jmetal.qualityIndicator.Hypervolume;
import org.uma.jmetal.qualityIndicator.InvertedGenerationalDistance;
import org.uma.jmetal.qualityIndicator.Spread;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Antonio J. Nebro on 20/07/14.
 */
public class QualityIndicatorGeneration implements ExperimentResult {

  private ExperimentData experimentData ;
  private String[] paretoFrontFiles ;
  private String paretoFrontDirectory ;
  private String[] qualityIndicatorList ;


  /** Constructor */
  private QualityIndicatorGeneration(Builder builder) {
    experimentData = builder.experimentData ;
    paretoFrontFiles = builder.paretoFrontFiles ;
    paretoFrontDirectory = builder.paretoFrontDirectory ;
    qualityIndicatorList = builder.qualityIndicatorList ;
  }

  /** Builder class */
  public static class Builder {
    private ExperimentData experimentData ;
    private String[] paretoFrontFiles ;
    private String paretoFrontDirectory ;
    private String[] qualityIndicatorList ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
      /* Default pareto front directory */
      this.paretoFrontDirectory = experimentData.getExperimentBaseDirectory() + "/referenceFronts" ;

      /* Default pareto front files */
      paretoFrontFiles = new String[experimentData.getProblemList().length] ;
      for (int i = 0; i < experimentData.getProblemList().length; i++) {
//        paretoFrontFiles[i] = paretoFrontDirectory+ "/" + experimentData.getProblemList()[i] + ".pf";
        paretoFrontFiles[i] =  experimentData.getProblemList()[i] + ".pf";
      }

      qualityIndicatorList = new String[0] ;
    }

    public Builder paretoFrontDirectory(String paretoFrontDirectory) {
      this.paretoFrontDirectory = paretoFrontDirectory ;

      return this ;
    }

    public Builder paretoFrontFiles(String[] paretoFrontFiles) {
      if (paretoFrontFiles.length != experimentData.getProblemList().length) {
        System.out.println("problme list lenght: "+ experimentData.getProblemList().length) ;
        System.out.println(experimentData.getProblemList()[0]);
        System.out.println(experimentData.getProblemList()[1]);
        throw new JMetalException("Wrong number of pareto front file names: "
          + paretoFrontFiles.length + " instead of "+experimentData.getProblemList().length) ;
      }

      this.paretoFrontFiles = Arrays.copyOf(paretoFrontFiles, paretoFrontFiles.length) ;

      return this ;
    }

    public Builder qualityIndicatorList(String []qualityIndicatorList) {
      this.qualityIndicatorList = Arrays.copyOf(qualityIndicatorList, qualityIndicatorList.length) ;

      return this ;
    }

    public QualityIndicatorGeneration build() {
      return new QualityIndicatorGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    for (int algorithmIndex = 0;
         algorithmIndex < experimentData.getAlgorithmNameList().length; algorithmIndex++) {

      String algorithmDirectory;
      algorithmDirectory = experimentData.getExperimentBaseDirectory()
        + "/data/" + experimentData.getAlgorithmNameList()[algorithmIndex] + "/";

      for (int problemIndex = 0;
           problemIndex < experimentData.getProblemList().length; problemIndex++) {

        String problemDirectory = algorithmDirectory + experimentData.getProblemList()[problemIndex];

        for (String indicator : qualityIndicatorList) {
          Configuration.logger.info("Experiment - Quality indicator: " + indicator);

          resetFile(problemDirectory + "/" + indicator);

          for (int numRun = 0; numRun < experimentData.getIndependentRuns(); numRun++) {

            String outputParetoFrontFilePath;
            outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
            String solutionFrontFile = outputParetoFrontFilePath;
            String qualityIndicatorFile = problemDirectory;
            double value = 0;

            String paretoFront = paretoFrontDirectory + "/" + paretoFrontFiles[problemIndex] ;

            double[][] trueFront = new Hypervolume().utils_.readFront(paretoFront);
            //double[][] trueFront = new Hypervolume().utils_.readFront(paretoFront[problemIndex]);

            if ("HV".equals(indicator)) {

              Hypervolume indicators = new Hypervolume();
              double[][] solutionFront =
                indicators.utils_.readFront(solutionFrontFile);
              value = indicators.hypervolume(solutionFront, trueFront, trueFront[0].length);

              qualityIndicatorFile = qualityIndicatorFile + "/HV";
            }
            if ("SPREAD".equals(indicator)) {
              Spread indicators = new Spread();
              double[][] solutionFront =
                indicators.utils_.readFront(solutionFrontFile);
              value = indicators.spread(solutionFront, trueFront, trueFront[0].length);

              qualityIndicatorFile = qualityIndicatorFile + "/SPREAD";
            }
            if ("IGD".equals(indicator)) {
              InvertedGenerationalDistance indicators = new InvertedGenerationalDistance();
              double[][] solutionFront =
                indicators.utils_.readFront(solutionFrontFile);
              value = indicators
                .invertedGenerationalDistance(solutionFront, trueFront, trueFront[0].length);

              qualityIndicatorFile = qualityIndicatorFile + "/IGD";
            }
            if ("EPSILON".equals(indicator)) {
              Epsilon indicators = new Epsilon();
              double[][] solutionFront =
                indicators.utils_.readFront(solutionFrontFile);
              value = indicators.epsilon(solutionFront, trueFront, trueFront[0].length);

              qualityIndicatorFile = qualityIndicatorFile + "/EPSILON";
            }

            if (!qualityIndicatorFile.equals(problemDirectory)) {
              FileWriter os;
              try {
                os = new FileWriter(qualityIndicatorFile, true);
                os.write("" + value + "\n");
                os.close();
              } catch (IOException ex) {
                Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
              }
            }
          }
        }
      }
    }
  }

  /**
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      Configuration.logger.info("File " + file + " exist.");

      if (f.isDirectory()) {
        Configuration.logger.info("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          Configuration.logger.info("Directory successfully deleted.");
        } else {
          Configuration.logger.info("Error deleting directory.");
        }
      } else {
        Configuration.logger.info("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          Configuration.logger.info("File succesfully deleted.");
        } else {
          Configuration.logger.info("Error deleting file.");
        }
      }
    } else {
    }
  }
}
