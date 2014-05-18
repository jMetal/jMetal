package jmetal.experiments.util;

import jmetal.experiments.Experiment;
import jmetal.util.Configuration;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 16/02/14.
 */
public class LatexTables implements IExperimentOutput {
  private Experiment experiment_ ;

  /**
   * Constructor
   * @param experiment
   */
  public LatexTables(Experiment experiment) {
    experiment_ = experiment ;
  }
  @Override
  public void generate() {
    experiment_.latexDirectory_ = experiment_.experimentBaseDirectory_ + "/" + experiment_.latexDirectory_;
      System.out.println("latex directory: " + experiment_.latexDirectory_);

      Vector[][][] data = new Vector[experiment_.indicatorList_.length][][];
      for (int indicator = 0; indicator < experiment_.indicatorList_.length; indicator++) {
        // A data vector per problem
        data[indicator] = new Vector[experiment_.problemList_.length][];

        for (int problem = 0; problem < experiment_.problemList_.length; problem++) {
          data[indicator][problem] = new Vector[experiment_.algorithmNameList_.length];

          for (int algorithm = 0; algorithm < experiment_.algorithmNameList_.length; algorithm++) {
            data[indicator][problem][algorithm] = new Vector();

            String directory = experiment_.experimentBaseDirectory_;
            directory += "/data/";
            directory += "/" + experiment_.algorithmNameList_[algorithm];
            directory += "/" + experiment_.problemList_[problem];
            directory += "/" + experiment_.indicatorList_[indicator];
            // Read values from data files
            FileInputStream fis = null;
            try {
              fis = new FileInputStream(directory);
            } catch (FileNotFoundException e) {
              Configuration.logger_.log(Level.SEVERE, "Error", e);
            }
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            //System.out.println(directory);
            String aux = null;
            try {
              aux = br.readLine();
              while (aux != null) {
                data[indicator][problem][algorithm].add(Double.parseDouble(aux));
                //System.out.println(Double.parseDouble(aux));
                aux = br.readLine();
              } // while
            } catch (IOException e) {
              Configuration.logger_.log(Level.SEVERE, "Error", e);
            }

          } // for
        } // for
      } // for

      double[][][] mean;
      double[][][] median;
      double[][][] stdDeviation;
      double[][][] iqr;
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

      mean = new double[experiment_.indicatorList_.length][][];
      median = new double[experiment_.indicatorList_.length][][];
      stdDeviation = new double[experiment_.indicatorList_.length][][];
      iqr = new double[experiment_.indicatorList_.length][][];
      min = new double[experiment_.indicatorList_.length][][];
      max = new double[experiment_.indicatorList_.length][][];
      numberOfValues = new int[experiment_.indicatorList_.length][][];

      for (int indicator = 0; indicator < experiment_.indicatorList_.length; indicator++) {
        // A data vector per problem
        mean[indicator] = new double[experiment_.problemList_.length][];
        median[indicator] = new double[experiment_.problemList_.length][];
        stdDeviation[indicator] = new double[experiment_.problemList_.length][];
        iqr[indicator] = new double[experiment_.problemList_.length][];
        min[indicator] = new double[experiment_.problemList_.length][];
        max[indicator] = new double[experiment_.problemList_.length][];
        numberOfValues[indicator] = new int[experiment_.problemList_.length][];

        for (int problem = 0; problem < experiment_.problemList_.length; problem++) {
          mean[indicator][problem] = new double[experiment_.algorithmNameList_.length];
          median[indicator][problem] = new double[experiment_.algorithmNameList_.length];
          stdDeviation[indicator][problem] = new double[experiment_.algorithmNameList_.length];
          iqr[indicator][problem] = new double[experiment_.algorithmNameList_.length];
          min[indicator][problem] = new double[experiment_.algorithmNameList_.length];
          max[indicator][problem] = new double[experiment_.algorithmNameList_.length];
          numberOfValues[indicator][problem] = new int[experiment_.algorithmNameList_.length];

          for (int algorithm = 0; algorithm < experiment_.algorithmNameList_.length; algorithm++) {
            Collections.sort(data[indicator][problem][algorithm]);

            String directory = experiment_.experimentBaseDirectory_;
            directory += "/" + experiment_.algorithmNameList_[algorithm];
            directory += "/" + experiment_.problemList_[problem];
            directory += "/" + experiment_.indicatorList_[indicator];

            //System.out.println("----" + directory + "-----");
            //calculateStatistics(data[indicator][problem][algorithm], meanV, medianV, minV, maxV, stdDeviationV, iqrV) ;
            calculateStatistics(data[indicator][problem][algorithm], statValues);
            /*
            System.out.println("Mean: " + statValues.get("mean"));
            System.out.println("Median : " + statValues.get("median"));
            System.out.println("Std : " + statValues.get("stdDeviation"));
            System.out.println("IQR : " + statValues.get("iqr"));
            System.out.println("Min : " + statValues.get("min"));
            System.out.println("Max : " + statValues.get("max"));
            System.out.println("N_values: " + data[indicator][problem][algorithm].size()) ;
             */
            mean[indicator][problem][algorithm] = statValues.get("mean");
            median[indicator][problem][algorithm] = statValues.get("median");
            stdDeviation[indicator][problem][algorithm] = statValues.get("stdDeviation");
            iqr[indicator][problem][algorithm] = statValues.get("iqr");
            min[indicator][problem][algorithm] = statValues.get("min");
            max[indicator][problem][algorithm] = statValues.get("max");
            numberOfValues[indicator][problem][algorithm] = data[indicator][problem][algorithm].size();
          }
        }
      }

      File latexOutput;
      latexOutput = new File(experiment_.latexDirectory_);
      if (!latexOutput.exists()) {
        boolean result = new File(experiment_.latexDirectory_).mkdirs();
        System.out.println("Creating " + experiment_.latexDirectory_ + " directory");
      }
      //System.out.println("Experiment name: " + experimentName_);
      String latexFile = experiment_.latexDirectory_ + "/" + experiment_.experimentName_ + ".tex";
    try {
      printHeaderLatexCommands(latexFile);
      for (int i = 0; i < experiment_.indicatorList_.length; i++) {
        printMeanStdDev(latexFile, i, mean, stdDeviation);
        printMedianIQR(latexFile, i, median, iqr);
      } // for
      printEndLatexCommands(latexFile);
    }
    catch (IOException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    } // generateLatexTables
  }

  /**
   * Calculates statistical values from a vector of Double objects
   *
   * @param vector
   * @param values
   */
  void calculateStatistics(Vector vector,
                           Map<String, Double> values) {

    if (vector.size() > 0) {
      double sum, minimum, maximum, sqsum, min, max, median, mean, iqr, stdDeviation;

      sqsum = 0.0;
      sum = 0.0;
      min = 1E300;
      max = -1E300;
      median = 0;

      for (int i = 0; i < vector.size(); i++) {
        double val = (Double) vector.elementAt(i);

        sqsum += val * val;
        sum += val;
        if (val < min) {
          min = val;
        }
        if (val > max) {
          max = val;
        } // if
      } // for

      // Mean
      mean = sum / vector.size();

      // Standard deviation
      if (sqsum / vector.size() - mean * mean < 0.0) {
        stdDeviation = 0.0;
      } else {
        stdDeviation = Math.sqrt(sqsum / vector.size() - mean * mean);
      } // if

      // Median
      if (vector.size() % 2 != 0) {
        median = (Double) vector.elementAt(vector.size() / 2);
      } else {
        median = ((Double) vector.elementAt(vector.size() / 2 - 1) +
                (Double) vector.elementAt(vector.size() / 2)) / 2.0;
      } // if

      values.put("mean", mean);
      values.put("median", Statistics.calculateMedian(vector, 0, vector.size() - 1));
      values.put("iqr", Statistics.calculateIQR(vector));
      values.put("stdDeviation", stdDeviation);
      values.put("min", (Double) min);
      values.put("max", (Double) max);
    } // if
    else {
      values.put("mean", Double.NaN);
      values.put("median", Double.NaN);
      values.put("iqr", Double.NaN);
      values.put("stdDeviation", Double.NaN);
      values.put("min", Double.NaN);
      values.put("max", Double.NaN);
    } // else
  } // calculateStatistics


  void printHeaderLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, false);
    os.write("\\documentclass{article}" + "\n");
    os.write("\\title{" + experiment_.experimentName_ + "}" + "\n");
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
  } // printEndLatexCommands

  void printMeanStdDev(String fileName, int indicator, double[][][] mean, double[][][] stdDev) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\" + "\n");
    os.write("\\begin{table}" + "\n");
    os.write("\\caption{" + experiment_.indicatorList_[indicator] + ". Mean and standard deviation}" + "\n");
    os.write("\\label{table:mean." + experiment_.indicatorList_[indicator] + "}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (String anAlgorithmNameList_ : experiment_.algorithmNameList_) {
      os.write("l");
    }
    os.write("}\n");

    os.write("\\hline");
    // write table head
    for (int i = -1; i < experiment_.algorithmNameList_.length; i++) {
      if (i == -1) {
        os.write(" & ");
      } else if (i == (experiment_.algorithmNameList_.length - 1)) {
        os.write(" " + experiment_.algorithmNameList_[i] + "\\\\" + "\n");
      } else {
        os.write("" + experiment_.algorithmNameList_[i] + " & ");
      }
    }
    os.write("\\hline" + "\n");

    String m, s;
    // write lines
    for (int i = 0; i < experiment_.problemList_.length; i++) {
      // find the best value and second best value
      double bestValue;
      double bestValueIQR;
      double secondBestValue;
      double secondBestValueIQR;
      int bestIndex = -1;
      int secondBestIndex = -1;
      if ((Boolean) experiment_.indicatorMinimize_.get(experiment_.indicatorList_[indicator])) {// minimize by default
        bestValue = Double.MAX_VALUE;
        bestValueIQR = Double.MAX_VALUE;
        secondBestValue = Double.MAX_VALUE;
        secondBestValueIQR = Double.MAX_VALUE;
        for (int j = 0; j < (experiment_.algorithmNameList_.length); j++) {
          if ((mean[indicator][i][j] < bestValue) ||
                  ((mean[indicator][i][j] == bestValue) && (stdDev[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = mean[indicator][i][j];
            bestValueIQR = stdDev[indicator][i][j];
            bestIndex = j;
          } else if ((mean[indicator][i][j] < secondBestValue) ||
                  ((mean[indicator][i][j] == secondBestValue) && (stdDev[indicator][i][j] < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = mean[indicator][i][j];
            secondBestValueIQR = stdDev[indicator][i][j];
          } // else if
        }
      } // if
      else { // indicator to maximize e.g., the HV
        bestValue = Double.MIN_VALUE;
        bestValueIQR = Double.MIN_VALUE;
        secondBestValue = Double.MIN_VALUE;
        secondBestValueIQR = Double.MIN_VALUE;
        for (int j = 0; j < (experiment_.algorithmNameList_.length); j++) {
          if ((mean[indicator][i][j] > bestValue) ||
                  ((mean[indicator][i][j] == bestValue) && (stdDev[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = mean[indicator][i][j];
            bestValueIQR = stdDev[indicator][i][j];
            bestIndex = j;
          } else if ((mean[indicator][i][j] > secondBestValue) ||
                  ((mean[indicator][i][j] == secondBestValue) && (stdDev[indicator][i][j] < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = mean[indicator][i][j];
            secondBestValueIQR = stdDev[indicator][i][j];
          } // else if
        } // for
      } // else

      os.write(experiment_.problemList_[i].replace("_", "\\_") + " & ");
      for (int j = 0; j < (experiment_.algorithmNameList_.length - 1); j++) {
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
      if (bestIndex == (experiment_.algorithmNameList_.length - 1)) {
        os.write("\\cellcolor{gray95}");
      }
      m = String.format(Locale.ENGLISH, "%10.2e", mean[indicator][i][experiment_.algorithmNameList_.length - 1]);
      s = String.format(Locale.ENGLISH, "%8.1e", stdDev[indicator][i][experiment_.algorithmNameList_.length - 1]);
      os.write("$" + m + "_{" + s + "}$ \\\\" + "\n");
    } // for
    //os.write("" + mean[0][problemList_.length-1][algorithmNameList_.length-1] + "\\\\"+ "\n" ) ;

    os.write("\\hline" + "\n");
    os.write("\\end{tabular}" + "\n");
    os.write("\\end{scriptsize}" + "\n");
    os.write("\\end{table}" + "\n");
    os.close();
  } // printMeanStdDev

  void printMedianIQR(String fileName, int indicator, double[][][] median, double[][][] IQR) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\" + "\n");
    os.write("\\begin{table}" + "\n");
    os.write("\\caption{" + experiment_.indicatorList_[indicator] + ". Median and IQR}" + "\n");
    os.write("\\label{table:median." + experiment_.indicatorList_[indicator] + "}" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{tabular}{l");

    // calculate the number of columns
    for (String anAlgorithmNameList_ : experiment_.algorithmNameList_) {
      os.write("l");
    }
    os.write("}\n");

    os.write("\\hline");
    // write table head
    for (int i = -1; i < experiment_.algorithmNameList_.length; i++) {
      if (i == -1) {
        os.write(" & ");
      } else if (i == (experiment_.algorithmNameList_.length - 1)) {
        os.write(" " + experiment_.algorithmNameList_[i] + "\\\\" + "\n");
      } else {
        os.write("" + experiment_.algorithmNameList_[i] + " & ");
      }
    }
    os.write("\\hline" + "\n");

    String m, s;
    // write lines
    for (int i = 0; i < experiment_.problemList_.length; i++) {
      // find the best value and second best value
      double bestValue;
      double bestValueIQR;
      double secondBestValue;
      double secondBestValueIQR;
      int bestIndex = -1;
      int secondBestIndex = -1;
      if ((Boolean) experiment_.indicatorMinimize_.get(experiment_.indicatorList_[indicator])) {// minimize by default
        bestValue = Double.MAX_VALUE;
        bestValueIQR = Double.MAX_VALUE;
        secondBestValue = Double.MAX_VALUE;
        secondBestValueIQR = Double.MAX_VALUE;
        for (int j = 0; j < (experiment_.algorithmNameList_.length); j++) {
          if ((median[indicator][i][j] < bestValue) ||
                  ((median[indicator][i][j] == bestValue) && (IQR[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = median[indicator][i][j];
            bestValueIQR = IQR[indicator][i][j];
            bestIndex = j;
          } else if ((median[indicator][i][j] < secondBestValue) ||
                  ((median[indicator][i][j] == secondBestValue) && (IQR[indicator][i][j] < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = median[indicator][i][j];
            secondBestValueIQR = IQR[indicator][i][j];
          } // else if
        } // for
      } // if
      else { // indicator to maximize e.g., the HV
        bestValue = Double.MIN_VALUE;
        bestValueIQR = Double.MIN_VALUE;
        secondBestValue = Double.MIN_VALUE;
        secondBestValueIQR = Double.MIN_VALUE;
        for (int j = 0; j < (experiment_.algorithmNameList_.length); j++) {
          if ((median[indicator][i][j] > bestValue) ||
                  ((median[indicator][i][j] == bestValue) && (IQR[indicator][i][j] < bestValueIQR))) {
            secondBestIndex = bestIndex;
            secondBestValue = bestValue;
            secondBestValueIQR = bestValueIQR;
            bestValue = median[indicator][i][j];
            bestValueIQR = IQR[indicator][i][j];
            bestIndex = j;
          } else if ((median[indicator][i][j] > secondBestValue) ||
                  ((median[indicator][i][j] == secondBestValue) && (IQR[indicator][i][j] < secondBestValueIQR))) {
            secondBestIndex = j;
            secondBestValue = median[indicator][i][j];
            secondBestValueIQR = IQR[indicator][i][j];
          } // else if
        } // for
      } // else

      os.write(experiment_.problemList_[i].replace("_", "\\_") + " & ");
      for (int j = 0; j < (experiment_.algorithmNameList_.length - 1); j++) {
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
      if (bestIndex == (experiment_.algorithmNameList_.length - 1)) {
        os.write("\\cellcolor{gray95}");
      }
      m = String.format(Locale.ENGLISH, "%10.2e", median[indicator][i][experiment_.algorithmNameList_.length - 1]);
      s = String.format(Locale.ENGLISH, "%8.1e", IQR[indicator][i][experiment_.algorithmNameList_.length - 1]);
      os.write("$" + m + "_{" + s + "}$ \\\\" + "\n");
    } // for
    //os.write("" + mean[0][problemList_.length-1][algorithmNameList_.length-1] + "\\\\"+ "\n" ) ;

    os.write("\\hline" + "\n");
    os.write("\\end{tabular}" + "\n");
    os.write("\\end{scriptsize}" + "\n");
    os.write("\\end{table}" + "\n");
    os.close();
  } // printMedianIQR
}
