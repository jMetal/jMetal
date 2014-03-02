package jmetal.experiments.util ;

import jmetal.experiments.Experiment;
import jmetal.qualityIndicator.Epsilon;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.InvertedGenerationalDistance;
import jmetal.qualityIndicator.Spread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Antonio J. Nebro on 17/02/14.
 */
public class QualityIndicatorTables implements iExperimentOutput {
  Experiment experiment_ ;

  public QualityIndicatorTables(Experiment experiment) {
    experiment_ = experiment ;
  }


  @Override
  public void generate() {
    String paretoFront[] = new String[experiment_.problemList_.length] ;

    //checkParetoFronts();
    if (experiment_.generateReferenceParetoFronts_){
      new ReferenceParetoFronts(experiment_).generate() ;
    }

    for (int i = 0; i < experiment_.problemList_.length; i++) {
      if (experiment_.generateReferenceParetoFronts_){
        paretoFront[i] = experiment_.experimentBaseDirectory_
                + "/referenceFronts" + "/" + experiment_.problemList_[i] + ".pf" ;
      }
      else {
        paretoFront[i] = experiment_.paretoFrontDirectory_ + "/" + experiment_.paretoFrontFileList_[i];
      }
      System.out.println("Pareto front " + i + ": " + paretoFront[i]) ;
    }


//      for (int i = 0; i < experiment_.problemList_.length; i++) {
//      if (experiment_.generateReferenceParetoFronts_){
//        experiment_.generateReferenceFronts(i);
//        paretoFront[i] = experiment_.experimentBaseDirectory_
//                + "/referenceFronts" + "/" + experiment_.problemList_[i] + ".pf" ;
//      }
//      else {
//        paretoFront[i] = experiment_.paretoFrontDirectory_ + "/" + experiment_.paretoFrontFileList_[i];
//      }
//      System.out.println("Pareto front " + i + ": " + paretoFront[i]) ;
//    }


    if (experiment_.indicatorList_.length > 0) {

      for (int algorithmIndex = 0; algorithmIndex < experiment_.algorithmNameList_.length; algorithmIndex++) {

        String algorithmDirectory;
        algorithmDirectory = experiment_.experimentBaseDirectory_
                + "/data/" + experiment_.algorithmNameList_[algorithmIndex] + "/";

        for (int problemIndex = 0; problemIndex < experiment_.problemList_.length; problemIndex++) {

          String problemDirectory = algorithmDirectory + experiment_.problemList_[problemIndex];
          //String paretoFrontPath = frontPath_[problemIndex];

          for (String anIndicatorList_ : experiment_.indicatorList_) {
            System.out.println("Experiment - Quality indicator: " + anIndicatorList_);

            resetFile(problemDirectory + "/" + anIndicatorList_);

            for (int numRun = 0; numRun < experiment_.independentRuns_; numRun++) {

              String outputParetoFrontFilePath;
              outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
              String solutionFrontFile = outputParetoFrontFilePath;
              String qualityIndicatorFile = problemDirectory;
              double value = 0;

              //double[][] trueFront =  new Hypervolume().utils_.readFront(paretoFrontPath);
              double[][] trueFront =  new Hypervolume().utils_.readFront(paretoFront[problemIndex]);

              if (anIndicatorList_.equals("HV")) {

                Hypervolume indicators = new Hypervolume();
                double[][] solutionFront =
                        indicators.utils_.readFront(solutionFrontFile);
                //double[][] trueFront =
                //        indicators.utils_.readFront(paretoFrontPath);
                value = indicators.hypervolume(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/HV";

              }
              if (anIndicatorList_.equals("SPREAD")) {
                Spread indicators = new Spread();
                double[][] solutionFront =
                        indicators.utils_.readFront(solutionFrontFile);
                //double[][] trueFront =
                //        indicators.utils_.readFront(paretoFrontPath);
                value = indicators.spread(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/SPREAD";
              }
              if (anIndicatorList_.equals("IGD")) {
                InvertedGenerationalDistance indicators = new InvertedGenerationalDistance();
                double[][] solutionFront =
                        indicators.utils_.readFront(solutionFrontFile);
                //double[][] trueFront =
                //        indicators.utils_.readFront(paretoFrontPath);
                value = indicators.invertedGenerationalDistance(solutionFront, trueFront, trueFront[0].length);

                qualityIndicatorFile = qualityIndicatorFile + "/IGD";
              }
              if (anIndicatorList_.equals("EPSILON")) {
                Epsilon indicators = new Epsilon();
                double[][] solutionFront =
                        indicators.utils_.readFront(solutionFrontFile);
                //double[][] trueFront =
                //        indicators.utils_.readFront(paretoFrontPath);
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
              } // if
            } // for
          } // for
        } // for
      } // for
    } // if

  }

  /**
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      System.out.println("File " + file + " exist.");

      if (f.isDirectory()) {
        System.out.println("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          System.out.println("Directory successfully deleted.");
        } else {
          System.out.println("Error deleting directory.");
        }
      } else {
        System.out.println("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          System.out.println("File succesfully deleted.");
        } else {
          System.out.println("Error deleting file.");
        }
      }
    } else {
      ; //System.out.println("File " + file + " does NOT exist.");
    }
  } // resetFile
}
