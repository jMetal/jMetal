package org.uma.jmetal.experiment.experimentoutput;

import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentOutput;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 23/07/14. First version created by Jorge Rodriguez
 */
public class FriedmanTableGeneration implements ExperimentOutput {
  private ExperimentData experimentData ;
  private String[] indicatorList ;
  private String outputDirectory ;

  /** Constructor */
  private FriedmanTableGeneration(Builder builder) {
    experimentData = builder.experimentData;
    indicatorList = builder.indicatorList;
    outputDirectory = builder.outputDirectory;

    if (outputDirectoryDoesNotExist()) {
      createOutputDirectory() ;
    }
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData;
    private String[] indicatorList;
    private String outputDirectory;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData;
      this.indicatorList = null;
      outputDirectory = experimentData.getExperimentBaseDirectory() + "/latex";
    }

    public Builder indicatorList(String[] indicatorList) {
      this.indicatorList = indicatorList;

      return this;
    }

    public Builder outputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;

      return this;
    }

    public FriedmanTableGeneration build() {
      return new FriedmanTableGeneration(this);
    }
  }

  @Override public void generate() {
    for (String indicator : indicatorList) {
      executeTest(indicator);
    }
  }


  private boolean outputDirectoryDoesNotExist() {
    boolean result;

    File directory = new File(outputDirectory);
    if (directory.exists() && directory.isDirectory()) {
      result = false;
    } else {
      result = true;
    }

    return result;
  }

  private void createOutputDirectory() {
    File directory = new File(outputDirectory);

    if (directory.exists()) {
      directory.delete() ;
    }

    boolean result ;
    result = new File(outputDirectory).mkdirs() ;
    if (!result) {
      throw new JMetalException("Error creating experiment directory: " + outputDirectory) ;
    }
  }

  private void executeTest(String indic) {
    Vector algorithms;
    Vector datasets;
    Vector data;
    String string = "";
    StringTokenizer lines;
    String linea;
    int i, j, k;
    int position;
    double mean[][];
    Pair order[][];
    Pair rank[][];
    boolean found;
    int ig;
    double sum;
    boolean visto[];
    Vector pendingToVisit;
    double Rj[];
    double friedman;
    double sumatoria = 0;
    double termino1, termino2;

    String indicator = indic;


    //String outDir = experimentData.getExperimentBaseDirectory() + "/latex";
    String outFile = outputDirectory + "/FriedmanTest" + indicator + ".tex";

    String Output = "";
    Output = Output + ("\\documentclass{article}\n" +
      "\\usepackage{graphicx}\n" +
      "\\title{Results}\n" +
      "\\author{}\n" +
      "\\date{\\today}\n" +
      "\\begin{document}\n" +
      "\\oddsidemargin 0in \\topmargin 0in" +
      "\\maketitle\n" +
      "\\section{Tables of Friedman Tests}");

    algorithms = new Vector();
    datasets = new Vector();
    data = new Vector();

    for (int alg = 0; alg < experimentData.getAlgorithmNameList().length; alg++) {
      algorithms.add(experimentData.getAlgorithmNameList()[alg]);
      data.add(new Vector());
      String rutaAlg = experimentData.getExperimentBaseDirectory() + "/data/"
        + experimentData.getAlgorithmNameList()[alg] + "/";

      for (int prob = 0; prob < experimentData.getProblemList().length; prob++) {
        if (alg == 0) {
          datasets.add(experimentData.getProblemList()[prob]);
        }

        String ruta = rutaAlg + experimentData.getProblemList()[prob] + "/" + indicator;

        string = "";

        try {
          FileInputStream fis = new FileInputStream(ruta);

          byte[] leido = new byte[4096];
          int bytesLeidos = 0;

          while (bytesLeidos != -1) {
            bytesLeidos = fis.read(leido);

            if (bytesLeidos != -1) {
              string += new String(leido, 0, bytesLeidos);
            }
          }

          fis.close();
        } catch (IOException e) {
          Configuration.logger.log(Level.SEVERE, "Error", e);
          throw new RuntimeException();
        }

        lines = new StringTokenizer(string, "\n\r");

        double valor = 0.0;
        int n = 0;

        while (lines.hasMoreTokens()) {
          linea = lines.nextToken();
          valor = valor + Double.parseDouble(linea);
          n++;
        }
        if (n != 0) {
          ((Vector) data.elementAt(alg)).add(new Double(valor / n));
        } else {
          ((Vector) data.elementAt(alg)).add(new Double(valor));
        }
      }
    }

    /*Compute the average performance per algorithm for each data set*/
    mean = new double[datasets.size()][algorithms.size()];

    for (j = 0; j < algorithms.size(); j++) {
      for (i = 0; i < datasets.size(); i++) {
        mean[i][j] = (Double) ((Vector) data.elementAt(j)).elementAt(i);
      }
    }

    /*We use the pareja structure to compute and order rankings*/
    order = new Pair[datasets.size()][algorithms.size()];
    for (i = 0; i < datasets.size(); i++) {
      for (j = 0; j < algorithms.size(); j++) {
        order[i][j] = new Pair(j, mean[i][j]);
      }
      Arrays.sort(order[i]);
    }

    /*building of the rankings table per algorithms and data sets*/
    rank = new Pair[datasets.size()][algorithms.size()];
    position = 0;
    for (i = 0; i < datasets.size(); i++) {
      for (j = 0; j < algorithms.size(); j++) {
        found = false;
        for (k = 0; k < algorithms.size() && !found; k++) {
          if (order[i][k].index_ == j) {
            found = true;
            position = k + 1;
          }
        }
        rank[i][j] = new Pair(position, order[i][position - 1].value_);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (i = 0; i < datasets.size(); i++) {
      visto = new boolean[algorithms.size()];
      pendingToVisit = new Vector();

      Arrays.fill(visto, false);
      for (j = 0; j < algorithms.size(); j++) {
        pendingToVisit.removeAllElements();
        sum = rank[i][j].index_;
        visto[j] = true;
        ig = 1;
        for (k = j + 1; k < algorithms.size(); k++) {
          if (rank[i][j].value_ == rank[i][k].value_ && !visto[k]) {
            sum += rank[i][k].index_;
            ig++;
            pendingToVisit.add(new Integer(k));
            visto[k] = true;
          }
        }
        sum /= (double) ig;
        rank[i][j].index_ = sum;
        for (k = 0; k < pendingToVisit.size(); k++) {
          rank[i][((Integer) pendingToVisit.elementAt(k))].index_ = sum;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    Rj = new double[algorithms.size()];
    for (i = 0; i < algorithms.size(); i++) {
      Rj[i] = 0;
      for (j = 0; j < datasets.size(); j++) {
        Rj[i] += rank[j][i].index_ / ((double) datasets.size());
      }
    }

    /*Print the average ranking per algorithm*/
    Output = Output + "\n" + ("\\begin{table}[!htp]\n" +
      "\\centering\n" +
      "\\caption{Average Rankings of the algorithms\n}" +
      // for "+ experiment.problemList[prob] +" problem\n}" +
      "\\begin{tabular}{c|c}\n" +
      "Algorithm&Ranking\\\\\n\\hline");

    for (i = 0; i < algorithms.size(); i++) {
      Output = Output + "\n" + (String) algorithms.elementAt(i) + "&" + Rj[i] + "\\\\";
    }

    Output = Output + "\n" +
      "\\end{tabular}\n" +
      "\\end{table}";

    /*Compute the Friedman statistic*/
    termino1 =
      (12 * (double) datasets.size()) / ((double) algorithms.size() * ((double) algorithms.size()
        + 1));
    termino2 =
      (double) algorithms.size() * ((double) algorithms.size() + 1) * ((double) algorithms.size()
        + 1) / (4.0);
    for (i = 0; i < algorithms.size(); i++) {
      sumatoria += Rj[i] * Rj[i];
    }
    friedman = (sumatoria - termino2) * termino1;

    Output = Output + "\n"
      + "\n\nFriedman statistic considering reduction performance (distributed according to chi-square with "
      + (algorithms.size() - 1) + " degrees of freedom: " + friedman + ").\n\n";

    Output = Output + "\n" + "\\end{document}";
    try {
      File latexOutput;
      latexOutput = new File(outputDirectory);
      if (!latexOutput.exists()) {
        latexOutput.mkdirs();
      }
      FileOutputStream f = new FileOutputStream(outFile);
      DataOutputStream fis = new DataOutputStream((OutputStream) f);

      fis.writeBytes(Output);

      fis.close();
      f.close();
    } catch (IOException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
      throw new RuntimeException();
    }
  }

  private class Pair implements Comparable {

    public double index_;
    public double value_;

    public Pair() {
    }

    public Pair(double i, double v) {
      index_ = i;
      value_ = v;
    }

    public int compareTo(Object o1) {
      if (Math.abs(this.value_) > Math.abs(((Pair) o1).value_)) {
        return 1;
      } else if (Math.abs(this.value_) < Math.abs(((Pair) o1).value_)) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
