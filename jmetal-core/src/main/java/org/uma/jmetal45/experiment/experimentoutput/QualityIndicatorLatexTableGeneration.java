package org.uma.jmetal45.experiment.experimentoutput;

import org.uma.jmetal45.experiment.ExperimentData;
import org.uma.jmetal45.experiment.ExperimentOutput;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.Statistics;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 23/07/14.
 */
public class QualityIndicatorLatexTableGeneration implements ExperimentOutput {
  private ExperimentData experimentData ;
  private String[] indicatorList ;
  private String outputDirectory ;

  private Vector[][][] data;
  private double[][][] mean;
  private double[][][] median;
  private double[][][] stdDeviation;
  private double[][][] iqr;

  /** Constructor */
  private QualityIndicatorLatexTableGeneration(Builder builder) {
    experimentData = builder.experimentData ;
    indicatorList = builder.indicatorList ;
    outputDirectory = builder.outputDirectory ;

    if (outputDirectoryDoesNotExist()) {
      createOutputDirectory() ;
    }
  }

  /** Builder class */
  public static class Builder {
    private final ExperimentData experimentData ;
    private String[] indicatorList ;
    private String outputDirectory ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
      this.indicatorList = null ;
      outputDirectory = experimentData.getExperimentBaseDirectory()+"/latex" ;
    }

    public Builder setIndicatorList(String[] indicatorList) {
      this.indicatorList = Arrays.copyOf(indicatorList, indicatorList.length) ;

      return this ;
    }

    public Builder outputDirectory(String outputDirectory) {
      this.outputDirectory = outputDirectory;

      return this;
    }

    public QualityIndicatorLatexTableGeneration build() {
      return new QualityIndicatorLatexTableGeneration(this) ;
    }
  }

  @Override public void generate() {
    readIndicatorData();

    double[][][] max;
    double[][][] min;
    int[][][] numberOfValues;

    Map<String, Double> statValues = new HashMap<String, Double>();

    statValues.put("mean", 0.0);
    statValues.put("median", 0.0);
    statValues.put("stdDeviation", 0.0);
    statValues.put("iqr", 0.0);
    statValues.put("max", 0.0);
    statValues.put("min", 0.0);

    mean = new double[indicatorList.length][][];
    median = new double[indicatorList.length][][];
    stdDeviation = new double[indicatorList.length][][];
    iqr = new double[indicatorList.length][][];
    min = new double[indicatorList.length][][];
    max = new double[indicatorList.length][][];
    numberOfValues = new int[indicatorList.length][][];

    for (int indicator = 0; indicator < indicatorList.length; indicator++) {
      // A data vector per problem
      mean[indicator] = new double[experimentData.getProblemList().length][];
      median[indicator] = new double[experimentData.getProblemList().length][];
      stdDeviation[indicator] = new double[experimentData.getProblemList().length][];
      iqr[indicator] = new double[experimentData.getProblemList().length][];
      min[indicator] = new double[experimentData.getProblemList().length][];
      max[indicator] = new double[experimentData.getProblemList().length][];
      numberOfValues[indicator] = new int[experimentData.getProblemList().length][];

      for (int problem = 0; problem < experimentData.getProblemList().length; problem++) {
        mean[indicator][problem] = new double[experimentData.getAlgorithmNameList().length];
        median[indicator][problem] = new double[experimentData.getAlgorithmNameList().length];
        stdDeviation[indicator][problem] = new double[experimentData.getAlgorithmNameList().length];
        iqr[indicator][problem] = new double[experimentData.getAlgorithmNameList().length];
        min[indicator][problem] = new double[experimentData.getAlgorithmNameList().length];
        max[indicator][problem] = new double[experimentData.getAlgorithmNameList().length];
        numberOfValues[indicator][problem] = new int[experimentData.getAlgorithmNameList().length];

        for (int algorithm = 0;
             algorithm < experimentData.getAlgorithmNameList().length; algorithm++) {
          Collections.sort(data[indicator][problem][algorithm]);

          String directory = experimentData.getExperimentBaseDirectory();
          directory += "/" + experimentData.getAlgorithmNameList()[algorithm];
          directory += "/" + experimentData.getProblemList()[problem];
          directory += "/" + indicatorList[indicator];

          calculateStatistics(data[indicator][problem][algorithm], statValues);

          mean[indicator][problem][algorithm] = statValues.get("mean");
          median[indicator][problem][algorithm] = statValues.get("median");
          stdDeviation[indicator][problem][algorithm] = statValues.get("stdDeviation");
          iqr[indicator][problem][algorithm] = statValues.get("iqr");
          min[indicator][problem][algorithm] = statValues.get("min");
          max[indicator][problem][algorithm] = statValues.get("max");
          numberOfValues[indicator][problem][algorithm] =
            data[indicator][problem][algorithm].size();
        }
      }
    }
    /*
    File latexOutput;
    latexOutput = new File(experimentData.getLatexDirectory());
    if (!latexOutput.exists()) {
      boolean result = new File(experimentData.getLatexDirectory()).mkdirs();
      Configuration.logger.info("Creating " + experimentData.getLatexDirectory() + " directory");
    }
    */
    String latexFile =
      outputDirectory + "/" + experimentData.getExperimentName() + ".tex";
    try {
      printHeaderLatexCommands(latexFile);
      for (int i = 0; i < indicatorList.length; i++) {
        printMeanStdDev(latexFile, i, mean, stdDeviation);
        printMedianIQR(latexFile, i, median, iqr);
      }
      printEndLatexCommands(latexFile);
    } catch (IOException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
  }

  /**
   * Calculates statistical values from a vector of Double objects
   *
   * @param vector
   * @param values
   */
  void calculateStatistics(Vector<Double> vector,
    Map<String, Double> values) {

    if (vector.size() > 0) {
      double sum, sqsum, min, max, mean, stdDeviation;

      sqsum = 0.0;
      sum = 0.0;
      min = 1E300;
      max = -1E300;

      for (int i = 0; i < vector.size(); i++) {
        double val = vector.elementAt(i);

        sqsum += val * val;
        sum += val;
        if (val < min) {
          min = val;
        }
        if (val > max) {
          max = val;
        }
      }

      // Mean
      mean = sum / vector.size();

      // Standard deviation
      if (sqsum / vector.size() - mean * mean < 0.0) {
        stdDeviation = 0.0;
      } else {
        stdDeviation = Math.sqrt(sqsum / vector.size() - mean * mean);
      }

      values.put("mean", mean);
      values.put("median", Statistics.calculateMedian(vector, 0, vector.size() - 1));
      values.put("iqr", Statistics.calculateIQR(vector));
      values.put("stdDeviation", stdDeviation);
      values.put("min", min);
      values.put("max", max);
    }
    else {
      values.put("mean", Double.NaN);
      values.put("median", Double.NaN);
      values.put("iqr", Double.NaN);
      values.put("stdDeviation", Double.NaN);
      values.put("min", Double.NaN);
      values.put("max", Double.NaN);
    }
  }


  void printHeaderLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, false);
    os.write("\\documentclass{article}" + "\n");
    os.write("\\title{" + experimentData.getExperimentName() + "}" + "\n");
    os.write("\\usepackage{colortbl}" + "\n");
    os.write("\\usepackage[table*]{xcolor}" + "\n");
    os.write("\\usepackage[margin=0.6in]{geometry}" + "\n");
    os.write("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.write("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.write("\\author{}" + "\n");
    os.write("\\begin{document}" + "\n");
    os.write("\\maketitle" + "\n");
    os.write("\\section{Tables}" + "\n");

    os.close();
  }

  void printEndLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\end{document}" + "\n");
    os.close();
  }

  void printMeanStdDev(String fileName, int indicator, double[][][] mean, double[][][] stdDev)
    throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\" + "\n");
    os.write("\\begin{table}" + "\n");
    os.write(
      "\\caption{" + indicatorList[indicator] + ". Mean and standard deviation}"
        + "\n"
    );
    os.write("\\label{table:mean." + indicatorList[indicator] + "}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (String anAlgorithmNameList_ : experimentData.getAlgorithmNameList()) {
      os.write("l");
    }
    os.write("}\n");

    os.write("\\hline");
    // write table head
    for (int i = -1; i < experimentData.getAlgorithmNameList().length; i++) {
      if (i == -1) {
        os.write(" & ");
      } else if (i == (experimentData.getAlgorithmNameList().length - 1)) {
        os.write(" " + experimentData.getAlgorithmNameList()[i] + "\\\\" + "\n");
      } else {
        os.write("" + experimentData.getAlgorithmNameList()[i] + " & ");
      }
    }
    os.write("\\hline" + "\n");

    String m, s;
    // write lines
    for (int i = 0; i < experimentData.getProblemList().length; i++) {
      // find the best value and second best value
      double bestValue;
      double bestValueIQR;
      double secondBestValue;
      double secondBestValueIQR;
      int bestIndex = -1;
      int secondBestIndex = -1;
      if (!indicatorList[indicator].equals("HV")) {
        // minimize by default
        bestValue = Double.MAX_VALUE;
        bestValueIQR = Double.MAX_VALUE;
        secondBestValue = Double.MAX_VALUE;
        secondBestValueIQR = Double.MAX_VALUE;
        for (int j = 0; j < (experimentData.getAlgorithmNameList().length); j++) {
          if ((mean[indicator][i][j] < bestValue) ||
            ((mean[indicator][i][j] == bestValue) && (stdDev[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = mean[indicator][i][j];
            bestValueIQR = stdDev[indicator][i][j];
            bestIndex = j;
          } else if ((mean[indicator][i][j] < secondBestValue) ||
            ((mean[indicator][i][j] == secondBestValue) && (stdDev[indicator][i][j]
              < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = mean[indicator][i][j];
            secondBestValueIQR = stdDev[indicator][i][j];
          }
        }
      } else {
        // indicator to maximize e.g., the HV
        bestValue = Double.MIN_VALUE;
        bestValueIQR = Double.MIN_VALUE;
        secondBestValue = Double.MIN_VALUE;
        secondBestValueIQR = Double.MIN_VALUE;
        for (int j = 0; j < (experimentData.getAlgorithmNameList().length); j++) {
          if ((mean[indicator][i][j] > bestValue) ||
            ((mean[indicator][i][j] == bestValue) && (stdDev[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = mean[indicator][i][j];
            bestValueIQR = stdDev[indicator][i][j];
            bestIndex = j;
          } else if ((mean[indicator][i][j] > secondBestValue) ||
            ((mean[indicator][i][j] == secondBestValue) && (stdDev[indicator][i][j]
              < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = mean[indicator][i][j];
            secondBestValueIQR = stdDev[indicator][i][j];
          }
        }
      }

      os.write(experimentData.getProblemList()[i].replace("_", "\\_") + " & ");
      for (int j = 0; j < (experimentData.getAlgorithmNameList().length - 1); j++) {
        if (j == bestIndex) {
          os.write("\\cellcolor{gray95}");
        }
        if (j == secondBestIndex) {
          os.write("\\cellcolor{gray25}");
        }

        m = String.format(Locale.ENGLISH, "%10.2e", mean[indicator][i][j]);
        s = String.format(Locale.ENGLISH, "%8.1e", stdDev[indicator][i][j]);
        os.write("$" + m + "_{" + s + "}$ & ");
      }
      if (bestIndex == (experimentData.getAlgorithmNameList().length - 1)) {
        os.write("\\cellcolor{gray95}");
      }
      m = String.format(Locale.ENGLISH, "%10.2e",
        mean[indicator][i][experimentData.getAlgorithmNameList().length - 1]);
      s = String.format(Locale.ENGLISH, "%8.1e",
        stdDev[indicator][i][experimentData.getAlgorithmNameList().length - 1]);
      os.write("$" + m + "_{" + s + "}$ \\\\" + "\n");
    }

    os.write("\\hline" + "\n");
    os.write("\\end{tabular}" + "\n");
    os.write("\\end{scriptsize}" + "\n");
    os.write("\\end{table}" + "\n");
    os.close();
  }

  void printMedianIQR(String fileName, int indicator, double[][][] median, double[][][] IQR)
    throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\" + "\n");
    os.write("\\begin{table}" + "\n");
    os.write("\\caption{" + indicatorList[indicator] + ". Median and IQR}" + "\n");
    os.write("\\label{table:median." + indicatorList[indicator] + "}" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (String anAlgorithmNameList_ : experimentData.getAlgorithmNameList()) {
      os.write("l");
    }
    os.write("}\n");

    os.write("\\hline");
    // write table head
    for (int i = -1; i < experimentData.getAlgorithmNameList().length; i++) {
      if (i == -1) {
        os.write(" & ");
      } else if (i == (experimentData.getAlgorithmNameList().length - 1)) {
        os.write(" " + experimentData.getAlgorithmNameList()[i] + "\\\\" + "\n");
      } else {
        os.write("" + experimentData.getAlgorithmNameList()[i] + " & ");
      }
    }
    os.write("\\hline" + "\n");

    String m, s;
    // write lines
    for (int i = 0; i < experimentData.getProblemList().length; i++) {
      // find the best value and second best value
      double bestValue;
      double bestValueIQR;
      double secondBestValue;
      double secondBestValueIQR;
      int bestIndex = -1;
      int secondBestIndex = -1;
      if (!indicatorList[indicator].equals("HV")) {
        // minimize by default
        bestValue = Double.MAX_VALUE;
        bestValueIQR = Double.MAX_VALUE;
        secondBestValue = Double.MAX_VALUE;
        secondBestValueIQR = Double.MAX_VALUE;
        for (int j = 0; j < (experimentData.getAlgorithmNameList().length); j++) {
          if ((median[indicator][i][j] < bestValue) ||
            ((median[indicator][i][j] == bestValue) && (IQR[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = median[indicator][i][j];
            bestValueIQR = IQR[indicator][i][j];
            bestIndex = j;
          } else if ((median[indicator][i][j] < secondBestValue) ||
            ((median[indicator][i][j] == secondBestValue) && (IQR[indicator][i][j]
              < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = median[indicator][i][j];
            secondBestValueIQR = IQR[indicator][i][j];
          }
        }
      } else {
        // indicator to maximize e.g., the HV
        bestValue = Double.MIN_VALUE;
        bestValueIQR = Double.MIN_VALUE;
        secondBestValue = Double.MIN_VALUE;
        secondBestValueIQR = Double.MIN_VALUE;
        for (int j = 0; j < (experimentData.getAlgorithmNameList().length); j++) {
          if ((median[indicator][i][j] > bestValue) ||
            ((median[indicator][i][j] == bestValue) && (IQR[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = median[indicator][i][j];
            bestValueIQR = IQR[indicator][i][j];
            bestIndex = j;
          } else if ((median[indicator][i][j] > secondBestValue) ||
            ((median[indicator][i][j] == secondBestValue) && (IQR[indicator][i][j]
              < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = median[indicator][i][j];
            secondBestValueIQR = IQR[indicator][i][j];
          }
        }
      }

      os.write(experimentData.getProblemList()[i].replace("_", "\\_") + " & ");
      for (int j = 0; j < (experimentData.getAlgorithmNameList().length - 1); j++) {
        if (j == bestIndex) {
          os.write("\\cellcolor{gray95}");
        }
        if (j == secondBestIndex) {
          os.write("\\cellcolor{gray25}");
        }
        m = String.format(Locale.ENGLISH, "%10.2e", median[indicator][i][j]);
        s = String.format(Locale.ENGLISH, "%8.1e", IQR[indicator][i][j]);
        os.write("$" + m + "_{" + s + "}$ & ");
      }
      if (bestIndex == (experimentData.getAlgorithmNameList().length - 1)) {
        os.write("\\cellcolor{gray95}");
      }
      m = String.format(Locale.ENGLISH, "%10.2e",
        median[indicator][i][experimentData.getAlgorithmNameList().length - 1]);
      s = String.format(Locale.ENGLISH, "%8.1e",
        IQR[indicator][i][experimentData.getAlgorithmNameList().length - 1]);
      os.write("$" + m + "_{" + s + "}$ \\\\" + "\n");
    }
    os.write("\\hline" + "\n");
    os.write("\\end{tabular}" + "\n");
    os.write("\\end{scriptsize}" + "\n");
    os.write("\\end{table}" + "\n");
    os.close();
  }

  void readIndicatorData() {
    data = new Vector[indicatorList.length][][];
    for (int indicator = 0; indicator < indicatorList.length; indicator++) {
      // A data vector per problem
      data[indicator] = new Vector[experimentData.getProblemList().length][];

      for (int problem = 0; problem < experimentData.getProblemList().length; problem++) {
        data[indicator][problem] = new Vector[experimentData.getAlgorithmNameList().length];

        for (int algorithm = 0;
             algorithm < experimentData.getAlgorithmNameList().length; algorithm++) {
          data[indicator][problem][algorithm] = new Vector();

          String directory = experimentData.getExperimentBaseDirectory();
          directory += "/data/";
          directory += "/" + experimentData.getAlgorithmNameList()[algorithm];
          directory += "/" + experimentData.getProblemList()[problem];
          directory += "/" + indicatorList[indicator];
          // Read values from data files
          FileInputStream fis = null;
          try {
            fis = new FileInputStream(directory);
          } catch (FileNotFoundException e) {
            JMetalLogger.logger.log(Level.SEVERE, "Error", e);
          }
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader br = new BufferedReader(isr);
          //Configuration.logger.info(directory);
          String aux = null;
          try {
            aux = br.readLine();
            while (aux != null) {
              data[indicator][problem][algorithm].add(Double.parseDouble(aux));
              aux = br.readLine();
            }
          } catch (IOException e) {
            JMetalLogger.logger.log(Level.SEVERE, "Error", e);
          }
        }
      }
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
}
