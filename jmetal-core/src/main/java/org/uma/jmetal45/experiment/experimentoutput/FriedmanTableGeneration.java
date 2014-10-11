package org.uma.jmetal45.experiment.experimentoutput;

import org.uma.jmetal45.experiment.ExperimentData;
import org.uma.jmetal45.experiment.ExperimentOutput;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.JMetalLogger;

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

    public Builder setIndicatorList(String[] indicatorList) {
      this.indicatorList = Arrays.copyOf(indicatorList, indicatorList.length) ;

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
    int position;
    double mean[][];
    Pair order[][];
    Pair rank[][];
    boolean found;
    int ig;
    double sum;
    boolean alreadyExplored[];
    Vector pendingToVisit;
    double rj[];
    double friedman;
    double term1, term2;

    String indicator = indic;


    String outFile = outputDirectory + "/FriedmanTest" + indicator + ".tex";

    String output = "";
    output = output + ("\\documentclass{article}\n" +
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
          JMetalLogger.logger.log(Level.SEVERE, "Error", e);
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

    for (int j = 0; j < algorithms.size(); j++) {
      for (int i = 0; i < datasets.size(); i++) {
        mean[i][j] = (Double) ((Vector) data.elementAt(j)).elementAt(i);
      }
    }

    /*We use the Pair structure to compute and order rankings*/
    order = new Pair[datasets.size()][algorithms.size()];
    for (int i = 0; i < datasets.size(); i++) {
      for (int j = 0; j < algorithms.size(); j++) {
        order[i][j] = new Pair(j, mean[i][j]);
      }
      Arrays.sort(order[i]);
    }

    /*building of the rankings table per algorithms and data sets*/
    rank = new Pair[datasets.size()][algorithms.size()];
    position = 0;
    for (int i = 0; i < datasets.size(); i++) {
      for (int j = 0; j < algorithms.size(); j++) {
        found = false;
        for (int k = 0; k < algorithms.size() && !found; k++) {
          if (order[i][k].index == j) {
            found = true;
            position = k + 1;
          }
        }
        rank[i][j] = new Pair(position, order[i][position - 1].value);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (int i = 0; i < datasets.size(); i++) {
      alreadyExplored = new boolean[algorithms.size()];
      pendingToVisit = new Vector();

      Arrays.fill(alreadyExplored, false);
      for (int j = 0; j < algorithms.size(); j++) {
        pendingToVisit.removeAllElements();
        sum = rank[i][j].index;
        alreadyExplored[j] = true;
        ig = 1;
        for (int k = j + 1; k < algorithms.size(); k++) {
          if (rank[i][j].value == rank[i][k].value && !alreadyExplored[k]) {
            sum += rank[i][k].index;
            ig++;
            pendingToVisit.add(new Integer(k));
            alreadyExplored[k] = true;
          }
        }
        sum /= (double) ig;
        rank[i][j].index = sum;
        for (int k = 0; k < pendingToVisit.size(); k++) {
          rank[i][((Integer) pendingToVisit.elementAt(k))].index = sum;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    rj = new double[algorithms.size()];
    for (int i = 0; i < algorithms.size(); i++) {
      rj[i] = 0;
      for (int j = 0; j < datasets.size(); j++) {
        rj[i] += rank[j][i].index / ((double) datasets.size());
      }
    }

    /*Print the average ranking per algorithm*/
    output = output + "\n" + ("\\begin{table}[!htp]\n" +
      "\\centering\n" +
      "\\caption{Average Rankings of the algorithms\n}" +
      // for "+ experiment.problemList[prob] +" problem\n}" +
      "\\begin{tabular}{c|c}\n" +
      "Algorithm&Ranking\\\\\n\\hline");

    for (int i = 0; i < algorithms.size(); i++) {
      output = output + "\n" + (String) algorithms.elementAt(i) + "&" + rj[i] + "\\\\";
    }

    output = output + "\n" +
      "\\end{tabular}\n" +
      "\\end{table}";

    /*Compute the Friedman statistic*/
    term1 =
      (12 * (double) datasets.size()) / ((double) algorithms.size() * ((double) algorithms.size()
        + 1));
    term2 =
      (double) algorithms.size() * ((double) algorithms.size() + 1) * ((double) algorithms.size()
        + 1) / (4.0);
    sum = 0 ;
    for (int i = 0; i < algorithms.size(); i++) {
      sum += rj[i] * rj[i];
    }
    friedman = (sum - term2) * term1;

    output = output + "\n"
      + "\n\nFriedman statistic considering reduction performance (distributed according to chi-square with "
      + (algorithms.size() - 1) + " degrees of freedom: " + friedman + ").\n\n";

    output = output + "\n" + "\\end{document}";
    try {
      File latexOutput;
      latexOutput = new File(outputDirectory);
      if (!latexOutput.exists()) {
        latexOutput.mkdirs();
      }
      FileOutputStream f = new FileOutputStream(outFile);
      DataOutputStream fis = new DataOutputStream((OutputStream) f);

      fis.writeBytes(output);

      fis.close();
      f.close();
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
      throw new RuntimeException();
    }
  }

  private class Pair implements Comparable {

    public double index;
    public double value;

    public Pair(double i, double v) {
      index = i;
      value = v;
    }

    public int compareTo(Object o1) {
      if (Math.abs(this.value) > Math.abs(((Pair) o1).value)) {
        return 1;
      } else if (Math.abs(this.value) < Math.abs(((Pair) o1).value)) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
