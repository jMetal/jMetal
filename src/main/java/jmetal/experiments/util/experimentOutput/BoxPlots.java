package jmetal.experiments.util.experimentOutput;

import jmetal.experiments.Experiment2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 18/02/14.
 */
public class BoxPlots implements iExperimentOutput{
  Experiment2 experiment_ ;

  public BoxPlots (Experiment2 experiment) {
    experiment_ = experiment ;
  }

  @Override
  public void generate() {
    String rDirectory = "R";
    rDirectory = experiment_.experimentBaseDirectory_ + "/" +  rDirectory;
    System.out.println("R    : " + rDirectory);
    File rOutput;
    rOutput = new File(rDirectory);
    if (!rOutput.exists()) {
      new File( rDirectory).mkdirs();
      System.out.println("Creating " +  rDirectory + " directory");
    }

    for (int indicator = 0; indicator <  experiment_.indicatorList_.length; indicator++) {
      System.out.println("Indicator: " +  experiment_.indicatorList_[indicator]);
      String rFile =  rDirectory + "/" +  experiment_.indicatorList_[indicator] + ".Boxplot.R";

      try {
        FileWriter os = new FileWriter(rFile, false);
        os.write("postscript(\""  +
                experiment_.indicatorList_[indicator] +
                ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
                "\n");
        //os.write("resultDirectory<-\"../data/" + experimentName_ +"\"" + "\n");
        os.write("resultDirectory<-\"../data/" + "\"" + "\n");
        os.write("qIndicator <- function(indicator, problem)" + "\n");
        os.write("{" + "\n");

        for (int i = 0; i <  experiment_.algorithmNameList_.length; i++) {
          os.write("file" +  experiment_.algorithmNameList_[i] +
                  "<-paste(resultDirectory, \"" +
                  experiment_.algorithmNameList_[i] + "\", sep=\"/\")" + "\n");
          os.write("file" +  experiment_.algorithmNameList_[i] +
                  "<-paste(file" +  experiment_.algorithmNameList_[i] + ", " +
                  "problem, sep=\"/\")" + "\n");
          os.write("file" +  experiment_.algorithmNameList_[i] +
                  "<-paste(file" +  experiment_.algorithmNameList_[i] + ", " +
                  "indicator, sep=\"/\")" + "\n");
          os.write( experiment_.algorithmNameList_[i] + "<-scan(" + "file" +  experiment_.algorithmNameList_[i] + ")" + "\n");
          os.write("\n");
        } // for

        os.write("algs<-c(");
        for (int i = 0; i <  experiment_.algorithmNameList_.length - 1; i++) {
          os.write("\"" +  experiment_.algorithmNameList_[i] + "\",");
        } // for
        os.write("\"" +  experiment_.algorithmNameList_[ experiment_.algorithmNameList_.length - 1] + "\")" + "\n");

        os.write("boxplot(");
        for (int i = 0; i <  experiment_.algorithmNameList_.length; i++) {
          os.write( experiment_.algorithmNameList_[i] + ",");
        } // for
        if (experiment_.boxplotNotch_) {
          os.write("names=algs, notch = TRUE)" + "\n");
        } else {
          os.write("names=algs, notch = FALSE)" + "\n");
        }
        os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
        os.write("title(main=titulo)" + "\n");

        os.write("}" + "\n");

        os.write("par(mfrow=c(" + experiment_.boxplotRows_ + "," + experiment_.boxplotColumns_ + "))" + "\n");

        os.write("indicator<-\"" +  experiment_.indicatorList_[indicator] + "\"" + "\n");

        for (String problem : experiment_.problemList_) {
          os.write("qIndicator(indicator, \"" + problem + "\")" + "\n");
        }

        os.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    } // for

  }
}
