//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.util.Pair;

import java.io.*;
import java.util.*;

/**
 * This class computes the Friedman test ranking and generates a Latex script that produces a table per
 * quality indicator containing the ranking
 *
 * The results are a set of Latex files that are written in the directory
 * {@link ExperimentConfiguration #getExperimentBaseDirectory()}/latex. Each file is called as
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

public class GenerateFriedmanTestTables<Result> implements ExperimentComponent {
  private static final String DEFAULT_LATEX_DIRECTORY = "latex";

  private final ExperimentConfiguration<?, Result> configuration;

  private String latexDirectoryName ;
  private int numberOfAlgorithms ;
  private int numberOfProblems ;

  @SuppressWarnings("unchecked")
  public GenerateFriedmanTestTables(ExperimentConfiguration experimentConfiguration) {
    this.configuration = experimentConfiguration ;

    configuration.removeDuplicatedAlgorithms();

    numberOfAlgorithms = configuration.getAlgorithmList().size() ;
    numberOfProblems = configuration.getProblemList().size() ;
  }


  @Override
  public void run() throws IOException {
    latexDirectoryName = configuration.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;

    for (QualityIndicator indicator : configuration.getIndicatorList()) {
      Vector<Vector<Double>> data = readData(indicator);
      double []averageRanking = computeAverageRanking(data) ;
      String fileContents = prepareFileOutputContents(averageRanking) ;
      writeLatexFile(indicator, fileContents);
    }
  }

  private Vector<Vector<Double>> readData(QualityIndicator indicator) {
    Vector<Vector<Double>> data = new Vector<Vector<Double>>() ;

    for (int algorithm = 0; algorithm < configuration.getAlgorithmList().size(); algorithm++) {
      String algorithmName = configuration.getAlgorithmList().get(algorithm).getTag();

      data.add(new Vector<Double>());
      String algorithmPath = configuration.getExperimentBaseDirectory() + "/data/"
          + algorithmName + "/";

      for (int problem = 0; problem < configuration.getProblemList().size(); problem++) {
        String path = algorithmPath + configuration.getProblemList().get(problem).getName() +
            "/" + indicator.getName();

        readDataFromFile(path, data, algorithm) ;
      }
    }

    return data ;
  }

  private void readDataFromFile(String path, Vector<Vector<Double>> data, int algorithmIndex) {
    String string = "";

    try {
      FileInputStream fis = new FileInputStream(path);

      byte[] bytes = new byte[4096];
      int readBytes = 0;

      while (readBytes != -1) {
        readBytes = fis.read(bytes);

        if (readBytes != -1) {
          string += new String(bytes, 0, readBytes);
        }
      }

      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
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
        mean[i][j] = (Double)((Vector) data.elementAt(j)).elementAt(i);
      }
    }

    /*We use the Pair class to compute and order rankings*/
    Pair[][] order = new Pair[numberOfProblems][numberOfAlgorithms];

    for (int i=0; i<numberOfProblems; i++) {
      for (int j=0; j<numberOfAlgorithms; j++){
        order[i][j] = new Pair(j,mean[i][j]);
      }
      Arrays.sort(order[i]);
    }

    /*building of the rankings table per algorithms and data sets*/
    Pair[][] rank = new Pair[numberOfProblems][numberOfAlgorithms];
    int position = 0;
    for (int i=0; i<numberOfProblems; i++) {
      for (int j=0; j<numberOfAlgorithms; j++){
        boolean found  = false;
        for (int k=0; k<numberOfAlgorithms && !found; k++) {
          if (order[i][k].index == j) {
            found = true;
            position = k+1;
          }
        }
        rank[i][j] = new Pair(position,order[i][position-1].value);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (int i=0; i<numberOfProblems; i++) {
      boolean[] hasBeenVisited = new boolean[numberOfAlgorithms];
      Vector<Integer> pendingToVisit= new Vector<Integer>();

      Arrays.fill(hasBeenVisited,false);
      for (int j=0; j<numberOfAlgorithms; j++) {
        pendingToVisit.removeAllElements();
        double sum = rank[i][j].index;
        hasBeenVisited[j] = true;
        int ig = 1;
        for (int k=j+1;k<numberOfAlgorithms;k++) {
          if (rank[i][j].value == rank[i][k].value && !hasBeenVisited[k]) {
            sum += rank[i][k].index;
            ig++;
            pendingToVisit.add(k);
            hasBeenVisited[k] = true;
          }
        }
        sum /= (double)ig;
        rank[i][j].index = sum;
        for (int k=0; k<pendingToVisit.size(); k++) {
          rank[i][pendingToVisit.elementAt(k)].index = sum;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    double []averageRanking = new double[numberOfAlgorithms];
    for (int i=0; i<numberOfAlgorithms; i++){
      averageRanking[i] = 0;
      for (int j=0; j<numberOfProblems; j++) {
        averageRanking[i] += rank[j][i].index / ((double)numberOfProblems);
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
  private void writeLatexFile(QualityIndicator indicator, String fileContents) {
    String outputFile = latexDirectoryName +"/FriedmanTest"+indicator.getName()+".tex";

    try {
      File latexOutput;
      latexOutput = new File(latexDirectoryName);
      if(!latexOutput.exists()){
        latexOutput.mkdirs();
      }
      FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
      DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

      dataOutputStream.writeBytes(fileContents);

      dataOutputStream.close();
      fileOutputStream.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private String writeLatexHeader() {

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
    for (int i=0; i<configuration.getAlgorithmList().size();i++) {
      output += "\n" + configuration.getAlgorithmList().get(i).getTag()+"&"+averageRanking[i]+"\\\\";
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
    double sum = 0;
    for (int i=0; i<numberOfAlgorithms;i++) {
      sum += averageRanking[i] * averageRanking[i];
    }
    double friedman = (sum - term2) * term1;

    String output = fileContents + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to " +
        "chi-square with "+(numberOfAlgorithms-1)+" degrees of freedom: "+friedman+").\n\n";
    output = output + "\n" + "\\end{document}";

    return output ;
  }
}


