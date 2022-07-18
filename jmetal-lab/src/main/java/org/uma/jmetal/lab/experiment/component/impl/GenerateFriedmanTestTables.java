package org.uma.jmetal.lab.experiment.component.impl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * This class computes the Friedman test ranking and generates a Latex script that produces a table per
 * quality indicator containing the ranking
 *
 * The results are a set of Latex files that are written in the directory
 * {@link Experiment #getExperimentBaseDirectory()}/latex. Each file is called as
 * FriedmanTest[indicatorName].tex
 *
 * The implementation is based on the one included in Keel:
 * J. Alcalá-Fdez, L. Sánchez, S. García, M.J. del Jesus, S. Ventura, J.M. Garrell, J. Otero, C. Romero, J. Bacardit,
 * V.M. Rivas, J.C. Fernández, F. Herrera.
 * KEEL: A Software Tool to Assess Evolutionary Algorithms to Data Mining Problems. Soft Computing 13:3 (2009) 307-318
 * Doi: 10.1007/s00500-008-0323-y
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class GenerateFriedmanTestTables<Result extends List<? extends Solution<?>>> implements ExperimentComponent {
  private static final String DEFAULT_LATEX_DIRECTORY = "latex";

  private final Experiment<?, Result> experiment;

  private String latexDirectoryName ;
  private int numberOfAlgorithms ;
  private int numberOfProblems ;

  public GenerateFriedmanTestTables(Experiment<?, Result> experimentConfiguration) {
    this.experiment = experimentConfiguration ;

    numberOfAlgorithms = experiment.getAlgorithmList().size() ;
    numberOfProblems = experiment.getProblemList().size() ;

    experiment.removeDuplicatedAlgorithms();
  }

  @Override
  public void run() throws IOException {
    latexDirectoryName = experiment.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;

    for (QualityIndicator indicator : experiment.getIndicatorList()) {
      Vector<Vector<Double>> data = readData(indicator);
      double @NotNull []averageRanking = computeAverageRanking(data) ;
      @NotNull String fileContents = prepareFileOutputContents(averageRanking) ;
      writeLatexFile(indicator, fileContents);
    }
  }

  private @NotNull Vector<Vector<Double>> readData(QualityIndicator indicator) {
    Vector<Vector<Double>> data = new Vector<Vector<Double>>() ;

    for (int algorithm = 0; algorithm < experiment.getAlgorithmList().size(); algorithm++) {
      String algorithmName = experiment.getAlgorithmList().get(algorithm).getAlgorithmTag();

      data.add(new Vector<Double>());
      @NotNull String algorithmPath = experiment.getExperimentBaseDirectory() + "/data/"
          + algorithmName + "/";

      for (int problem = 0; problem < experiment.getProblemList().size(); problem++) {
        String path = algorithmPath + experiment.getProblemList().get(problem).getTag() +
            "/" + indicator.getName();

        readDataFromFile(path, data, algorithm) ;
      }
    }

    return data ;
  }

  private void readDataFromFile(String path, @NotNull Vector<Vector<Double>> data, int algorithmIndex) {
    String string = "";

    try(@NotNull FileInputStream fis = new FileInputStream(path)) {
      

      byte @NotNull [] bytes = new byte[4096];
      int readBytes = 0;

      while (readBytes != -1) {
        readBytes = fis.read(bytes);

        if (readBytes != -1) {
          string += new String(bytes, 0, readBytes);
        }
      }

    } catch (IOException e) {
      throw new JMetalException("Error reading data ", e) ;
    }

    StringTokenizer lines = new StringTokenizer(string, "\n\r");

    double valor = 0.0;
    int n = 0;

    while (lines.hasMoreTokens()) {
      valor = valor + Double.parseDouble(lines.nextToken());
      n++;
    }
    if (n != 0) {
      (data.elementAt(algorithmIndex)).add(valor / n);
    } else {
      (data.elementAt(algorithmIndex)).add(valor);
    }
  }

  private double[] computeAverageRanking(Vector<Vector<Double>> data) {
    /*Compute the average performance per algorithm for each data set*/
    double[][] mean = new double[numberOfProblems][numberOfAlgorithms];

    for (int j = 0; j < numberOfAlgorithms; j++) {
      for (int i = 0; i < numberOfProblems; i++) {
        mean[i][j] = data.elementAt(j).elementAt(i);
      }
    }

    /*We use the Pair class to compute and order rankings*/
    @NotNull List<List<Pair<Integer, Double>>> order = new ArrayList<List<Pair<Integer, Double>>>(numberOfProblems);

    for (int i=0; i<numberOfProblems; i++) {
      order.add(new ArrayList<>(numberOfAlgorithms)) ;
      for (int j=0; j<numberOfAlgorithms; j++){
        order.get(i).add(new ImmutablePair<>(j, mean[i][j]));
      }
      order.get(i).sort(Comparator.comparingDouble(pair -> Math.abs(pair.getValue())));
    }

    /*building of the rankings table per algorithms and data sets*/
    @NotNull List<List<MutablePair<Double, Double>>> rank = new ArrayList<List<MutablePair<Double, Double>>>(numberOfProblems);

    int position = 0;
    for (int i=0; i<numberOfProblems; i++) {
      rank.add(new ArrayList<MutablePair<Double, Double>>(numberOfAlgorithms)) ;
      for (int j=0; j<numberOfAlgorithms; j++){
        boolean found  = false;
        for (int k=0; k<numberOfAlgorithms && !found; k++) {
          if (order.get(i).get(k).getKey() == j) {
            found = true;
            position = k+1;
          }
        }
        rank.get(i).add(new MutablePair<Double, Double>((double)position, order.get(i).get(position-1).getValue())) ;
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (int i=0; i<numberOfProblems; i++) {
      boolean[] hasBeenVisited = new boolean[numberOfAlgorithms];
      Vector<Integer> pendingToVisit= new Vector<Integer>();

      Arrays.fill(hasBeenVisited,false);
      for (int j=0; j<numberOfAlgorithms; j++) {
        pendingToVisit.removeAllElements();
        double sum = rank.get(i).get(j).getKey();
        hasBeenVisited[j] = true;
        int ig = 1;
        for (int k=j+1;k<numberOfAlgorithms;k++) {
          if (rank.get(i).get(j).getValue().equals(rank.get(i).get(k).getValue()) && !hasBeenVisited[k]) {
            sum += rank.get(i).get(k).getKey();
            ig++;
            pendingToVisit.add(k);
            hasBeenVisited[k] = true;
          }
        }
        sum /= (double)ig;
        rank.get(i).get(j).setLeft(sum);
        for (int k=0; k<pendingToVisit.size(); k++) {
          rank.get(i).get(pendingToVisit.elementAt(k)).setLeft(sum) ;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    double @NotNull []averageRanking = new double[numberOfAlgorithms];
    for (int i=0; i<numberOfAlgorithms; i++){
      averageRanking[i] = 0;
      for (int j=0; j<numberOfProblems; j++) {
        averageRanking[i] += rank.get(j).get(i).getKey() / ((double)numberOfProblems);
      }
    }

    return averageRanking ;
  }

  public String prepareFileOutputContents(double[] averageRanking) {
    String fileContents = writeLatexHeader();
    fileContents = printTableHeader(fileContents) ;
    fileContents = printTableLines(fileContents, averageRanking) ;
    fileContents = printTableTail(fileContents) ;
    fileContents = printDocumentFooter(fileContents, averageRanking) ;

    return fileContents ;
  }

  /**
   * Write the file contents in the output file
   * @param indicator
   * @param fileContents
   */
  private void writeLatexFile(QualityIndicator indicator, @NotNull String fileContents) {
    String outputFile = latexDirectoryName +"/FriedmanTest"+indicator.getName()+".tex";

    File latexOutput;
    latexOutput = new File(latexDirectoryName);
    if(!latexOutput.exists()){
      latexOutput.mkdirs();
    }
    
    try(DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(outputFile))) {
      dataOutputStream.writeBytes(fileContents);
    } catch (IOException e) {
      throw new JMetalException("Error writing data ", e) ;
    }
  }

  private @NotNull String writeLatexHeader() {

    return ("\\documentclass{article}\n" +
        "\\usepackage{graphicx}\n" +
        "\\title{Results}\n" +
        "\\author{}\n" +
        "\\date{\\today}\n" +
        "\\begin{document}\n" +
        "\\oddsidemargin 0in \\topmargin 0in" +
        "\\maketitle\n" +
        "\\\n" +
        "\\section{Tables}");
  }

  private String printTableLines(String fileContents, double[] averageRanking) {
    String output = fileContents ;
    for (int i = 0; i< experiment.getAlgorithmList().size(); i++) {
      output += "\n" + experiment.getAlgorithmList().get(i).getAlgorithmTag()+"&"+averageRanking[i]+"\\\\";
    }

    return output ;
  }

  private String printTableTail(String fileContents) {
    return fileContents + "\n" +
        "\\end{tabular}\n" +
        "\\end{table}";
  }

  private String printTableHeader(String fileContents) {
    return fileContents + "\n"+("\\begin{table}[!htp]\n" +
        "\\centering\n" +
        "\\caption{Average ranking of the algorithms}\n"+
        "\\begin{tabular}{c|c}\n" +
        "Algorithm&Ranking\\\\\n\\hline");
  }

  private String printDocumentFooter(String fileContents, double[] averageRanking) {
    double term1 = (12*(double)numberOfProblems)/(numberOfAlgorithms*(numberOfAlgorithms+1));
    double term2 = numberOfAlgorithms*(numberOfAlgorithms+1)*(numberOfAlgorithms+1)/(4.0);
      double sum = 0.0;
      int bound = numberOfAlgorithms;
      for (int i = 0; i < bound; i++) {
          double v = averageRanking[i] * averageRanking[i];
          sum += v;
      }
      double friedman = (sum - term2) * term1;

    String output = fileContents + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to " +
        "chi-square with "+(numberOfAlgorithms-1)+" degrees of freedom: "+friedman+").\n\n";
    output = output + "\n" + "\\end{document}";

    return output ;
  }
}


