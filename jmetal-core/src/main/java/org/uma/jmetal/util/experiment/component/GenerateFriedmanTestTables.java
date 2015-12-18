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
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.util.Pair;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

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
  private List<GenericIndicator<? extends Solution<?>>> indicatorList ;

  private String latexDirectoryName ;

  @SuppressWarnings("unchecked")
  public GenerateFriedmanTestTables(ExperimentConfiguration experimentConfiguration) {
    this.configuration = experimentConfiguration ;
    this.indicatorList = experimentConfiguration.getIndicatorList() ;

    configuration.removeDuplicatedAlgorithms();
  }


  @Override
  public void run() throws IOException {
    latexDirectoryName = configuration.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;

    for (QualityIndicator indicator : configuration.getIndicatorList()) {
      computeDataStatistics(indicator);
    }
  }

  private void computeDataStatistics(QualityIndicator indicator) {
    Vector<String> algorithms = new Vector<String>();
    Vector<String> datasets = new Vector<String>();
    Vector data = new Vector();

    String outputFile =
        latexDirectoryName +"/FriedmanTest"+indicator.getName()+".tex";

    for (int algorithm = 0; algorithm < configuration.getAlgorithmList().size(); algorithm++) {
      String algorithmName = configuration.getAlgorithmList().get(algorithm).getTag();
      algorithms.add(new String(algorithmName));
      data.add(new Vector());
      String algorithmPath = configuration.getExperimentBaseDirectory() + "/data/"
          + algorithmName + "/";

      for (int problem = 0; problem < configuration.getProblemList().size(); problem++) {
        if (algorithm == 0) {
          datasets.add(configuration.getProblemList().get(problem).getName());
        }

        String path = algorithmPath + configuration.getProblemList().get(problem).getName() +
            "/" + indicator.getName();

        //Leemos el fichero
        String string = "";

        try {
          FileInputStream fis = new FileInputStream(path);

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
          ((Vector) data.elementAt(algorithm)).add(new Double(valor / n));
        } else {
          ((Vector) data.elementAt(algorithm)).add(new Double(valor));
        }
      }
    }

      /*Compute the average performance per algorithm for each data set*/
    double[][] mean = new double[datasets.size()][algorithms.size()];

    for (int j = 0; j < algorithms.size(); j++) {
      for (int i = 0; i < datasets.size(); i++) {
        mean[i][j] = ((Double)((Vector)data.elementAt(j)).elementAt(i)).doubleValue();
      }
    }

          /*We use the pareja structure to compute and order rankings*/
    Pair[][] order = new Pair[datasets.size()][algorithms.size()];

    for (int i=0; i<datasets.size(); i++) {
      for (int j=0; j<algorithms.size(); j++){
        order[i][j] = new Pair (j,mean[i][j]);
      }
      Arrays.sort(order[i]);
    }

          /*building of the rankings table per algorithms and data sets*/
    Pair[][] rank = new Pair[datasets.size()][algorithms.size()];
    int position = 0;
    for (int i=0; i<datasets.size(); i++) {
      for (int j=0; j<algorithms.size(); j++){
        boolean found  = false;
        for (int k=0; k<algorithms.size() && !found; k++) {
          if (order[i][k].index == j) {
            found = true;
            position = k+1;
          }
        }
        rank[i][j] = new Pair(position,order[i][position-1].value);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (int i=0; i<datasets.size(); i++) {
      boolean[] visto = new boolean[algorithms.size()];
      Vector porVisitar= new Vector();

      Arrays.fill(visto,false);
      for (int j=0; j<algorithms.size(); j++) {
        porVisitar.removeAllElements();
        double sum = rank[i][j].index;
        visto[j] = true;
        int ig = 1;
        for (int k=j+1;k<algorithms.size();k++) {
          if (rank[i][j].value == rank[i][k].value && !visto[k]) {
            sum += rank[i][k].index;
            ig++;
            porVisitar.add(new Integer(k));
            visto[k] = true;
          }
        }
        sum /= (double)ig;
        rank[i][j].index = sum;
        for (int k=0; k<porVisitar.size(); k++) {
          rank[i][((Integer)porVisitar.elementAt(k)).intValue()].index = sum;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    double []averageRanking = new double[algorithms.size()];
    for (int i=0; i<algorithms.size(); i++){
      averageRanking[i] = 0;
      for (int j=0; j<datasets.size(); j++) {
        averageRanking[i] += rank[j][i].index / ((double)datasets.size());
      }
    }

    String fileContents = writeLatexHeader();
    fileContents = printTableHeader(fileContents) ;
    fileContents = printTableLines(fileContents, algorithms, averageRanking) ;
    fileContents = printTableTail(fileContents) ;
    fileContents = printDocumentFooter(fileContents, datasets, averageRanking) ;
    writeLatexFile(outputFile, fileContents);
  }

  /**
   * Write the file contents in the output file
   * @param outputFile
   * @param fileContents
   */
  private void writeLatexFile(String outputFile, String fileContents) {
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
    String latexHeader = ("\\documentclass{article}\n" +
        "\\usepackage{graphicx}\n" +
        "\\title{Results}\n" +
        "\\author{}\n" +
        "\\date{\\today}\n" +
        "\\begin{document}\n" +
        "\\oddsidemargin 0in \\topmargin 0in" +
        "\\maketitle\n" +
        "\\\n" +
        "\\section{Tables}");

    return latexHeader ;
  }

  private String printTableLines(String fileContents, Vector<String> algorithms, double[] averageRanking) {
    String output = fileContents ;
    for (int i=0; i<algorithms.size();i++) {
      output += "\n" + algorithms.elementAt(i)+"&"+averageRanking[i]+"\\\\";
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

  private String printDocumentFooter(String fileContents, Vector<String> datasets, double[] averageRanking) {
    double numberOfAlgorithms = configuration.getAlgorithmList().size() ;
    double termino1 = (12*(double)datasets.size())/(numberOfAlgorithms*(numberOfAlgorithms+1));
    double termino2 = numberOfAlgorithms*(numberOfAlgorithms+1)*(numberOfAlgorithms+1)/(4.0);
    double sumatoria = 0;
    for (int i=0; i<numberOfAlgorithms;i++) {
      sumatoria += averageRanking[i] * averageRanking[i];
    }
    double friedman = (sumatoria - termino2) * termino1;

    String output = fileContents + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to " +
        "chi-square with "+(numberOfAlgorithms-1)+" degrees of freedom: "+friedman+").\n\n";
    output = output + "\n" + "\\end{document}";

    return output ;
  }
}


