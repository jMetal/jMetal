package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by ajnebro on 18/12/15.
 */
public class GenerateFriedmanTestTables<Result> implements ExperimentComponent {
  private static final String DEFAULT_LATEX_DIRECTORY = "latex";

  private final ExperimentConfiguration<?, Result> configuration;
  private List<GenericIndicator<? extends Solution<?>>> indicatorList ;

  private String latexDirectoryName ;

  public GenerateFriedmanTestTables(ExperimentConfiguration experimentConfiguration) {
    this.configuration = experimentConfiguration ;
    this.indicatorList = experimentConfiguration.getIndicatorList() ;

    configuration.removeDuplicatedAlgorithms();
  }


  @Override
  public void run() throws IOException {
    latexDirectoryName = configuration.getExperimentBaseDirectory() + "/" + DEFAULT_LATEX_DIRECTORY;

    //List<List<List<List<Double>>>> data = readDataFromFiles() ;
    computeDataStatistics() ;
    //generateLatexScript() ;
  }

  private void computeDataStatistics() {
    for (int indicator = 0; indicator < configuration.getIndicatorList().size(); indicator++) {
      Vector<String> algorithms = new Vector<String>();
      Vector<String> datasets = new Vector<String>();
      Vector data = new Vector();

      String outputFile =
          latexDirectoryName +"/FriedmanTest"+configuration.getIndicatorList().get(indicator).getName()+".tex";

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
              "/" + configuration.getIndicatorList().get(indicator).getName();

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
            if (order[i][k].indice == j) {
              found = true;
              position = k+1;
            }
          }
          rank[i][j] = new Pair(position,order[i][position-1].valor);
        }
      }

          /*In the case of having the same performance, the rankings are equal*/
      for (int i=0; i<datasets.size(); i++) {
        boolean[] visto = new boolean[algorithms.size()];
        Vector porVisitar= new Vector();

        Arrays.fill(visto,false);
        for (int j=0; j<algorithms.size(); j++) {
          porVisitar.removeAllElements();
          double sum = rank[i][j].indice;
          visto[j] = true;
          int ig = 1;
          for (int k=j+1;k<algorithms.size();k++) {
            if (rank[i][j].valor == rank[i][k].valor && !visto[k]) {
              sum += rank[i][k].indice;
              ig++;
              porVisitar.add(new Integer(k));
              visto[k] = true;
            }
          }
          sum /= (double)ig;
          rank[i][j].indice = sum;
          for (int k=0; k<porVisitar.size(); k++) {
            rank[i][((Integer)porVisitar.elementAt(k)).intValue()].indice = sum;
          }
        }
      }
          /*compute the average ranking for each algorithm*/
      double []Rj = new double[algorithms.size()];
      for (int i=0; i<algorithms.size(); i++){
        Rj[i] = 0;
        for (int j=0; j<datasets.size(); j++) {
          Rj[i] += rank[j][i].indice / ((double)datasets.size());
        }
      }

          /*Print the average ranking per algorithm*/
      Output = Output + "\n"+("\\begin{table}[!htp]\n" +
          "\\centering\n" +
          "\\caption{Average Rankings of the algorithms\n}"+// for "+ exp_.problemList_[prob] +" problem\n}" +
          "\\begin{tabular}{c|c}\n" +
          "Algorithm&Ranking\\\\\n\\hline");

      for (int i=0; i<algorithms.size();i++) {
        Output = Output + "\n" + algorithms.elementAt(i)+"&"+Rj[i]+"\\\\";
      }

      Output = Output + "\n" +
          "\\end{tabular}\n" +
          "\\end{table}";

          /*Compute the Friedman statistic*/
      double termino1 = (12*(double)datasets.size())/((double)algorithms.size()*((double)algorithms.size()+1));
      double termino2 = (double)algorithms.size()*((double)algorithms.size()+1)*((double)algorithms.size()+1)/(4.0);
      double sumatoria = 0;
      for (int i=0; i<algorithms.size();i++) {
        sumatoria += Rj[i]*Rj[i];
      }
      double friedman = (sumatoria - termino2) * termino1;

      Output = Output + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to chi-square with "+(algorithms.size()-1)+" degrees of freedom: "+friedman+").\n\n";

      Output = Output + "\n" + "\\end{document}";
      try {
        File latexOutput;
        latexOutput = new File(latexDirectoryName);
        if(!latexOutput.exists()){
          latexOutput.mkdirs();
        }
        FileOutputStream f = new FileOutputStream(outputFile);
        DataOutputStream fis = new DataOutputStream((OutputStream) f);

        fis.writeBytes(Output);

        fis.close();
        f.close();
      }
      catch (IOException e) {
        e.printStackTrace();
        System.exit(-1);
      }
    }
  }

  private void generateLatexScript() {
  }

}

class Pair implements Comparable {
  public double indice;
  public double valor;

  public Pair() {
  }

  public Pair(double i, double v) {
    indice = i;
    valor = v;
  }

  public int compareTo (Object o1) { //ordena por valor absoluto

    if (Math.abs(this.valor) > Math.abs(((Pair)o1).valor)){
      //return -1;
      return 1;
    }else if (Math.abs(this.valor) < Math.abs(((Pair)o1).valor)){
      //return 1;
      return -1;
    }else return 0;
  }
}
