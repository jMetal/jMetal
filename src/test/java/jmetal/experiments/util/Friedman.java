package jmetal.experiments.util;

import jmetal.experiments.Experiment;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

public class Friedman {

  private Experiment exp_;

  public Friedman(Experiment exp){
    exp_ = exp;
  }

  public void executeTest(String indic){
    Vector algoritmos;
    Vector datasets;
    Vector datos;
    String cadena = "";
    StringTokenizer lineas;
    String linea;
    int i, j, k;
    int posicion;
    double mean[][];
    Pareja orden[][];
    Pareja rank[][];
    boolean encontrado;
    int ig;
    double sum;
    boolean visto[];
    Vector porVisitar;
    double Rj[];
    double friedman;
    double sumatoria=0;
    double termino1, termino2;

    String indicator_ = indic;

    /*Read the result file*/

    String outDir = exp_.experimentBaseDirectory_ + "/latex";
    String outFile = outDir +"/FriedmanTest"+indicator_+".tex";

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

    algoritmos = new Vector();
    datasets = new Vector();
    datos = new Vector();

    for(int alg = 0; alg<exp_.algorithmNameList_.length; alg++){
      algoritmos.add(new String(exp_.algorithmNameList_[alg]));
      datos.add(new Vector());
      String rutaAlg = exp_.experimentBaseDirectory_ + "/data/" 
          + exp_.algorithmNameList_[alg] + "/";

      for(int prob = 0; prob<exp_.problemList_.length; prob++){
        if(alg == 0){
          datasets.add(exp_.problemList_[prob]);
        }

        String ruta = rutaAlg + exp_.problemList_[prob] + "/" + indicator_;	

        //Leemos el fichero
        cadena = "";

        try {
          FileInputStream fis = new FileInputStream(ruta);

          byte[] leido = new byte[4096];
          int bytesLeidos = 0;

          while (bytesLeidos != -1) {
            bytesLeidos = fis.read(leido);

            if (bytesLeidos != -1) {
              cadena += new String(leido, 0, bytesLeidos);
            }
          }

          fis.close();
        }
        catch (IOException e) {
          e.printStackTrace();
          System.exit(-1);
        }

        lineas = new StringTokenizer (cadena,"\n\r");

        double valor = 0.0;
        int n = 0;

        while (lineas.hasMoreTokens()) {
          linea = lineas.nextToken();
          valor = valor + Double.parseDouble(linea);
          n++;
        }
        if(n!=0){
          ((Vector)datos.elementAt(alg)).add(new Double(valor/n));
        }else{
          ((Vector)datos.elementAt(alg)).add(new Double(valor));
        }

      } // for
    } // for


    /*Compute the average performance per algorithm for each data set*/		
    mean = new double[datasets.size()][algoritmos.size()];

    for (j=0; j<algoritmos.size(); j++) {
      for (i=0; i<datasets.size(); i++) {
        mean[i][j] = ((Double)((Vector)datos.elementAt(j)).elementAt(i)).doubleValue();				
      }
    }


    /*We use the pareja structure to compute and order rankings*/
    orden = new Pareja[datasets.size()][algoritmos.size()];
    for (i=0; i<datasets.size(); i++) {
      for (j=0; j<algoritmos.size(); j++){					
        orden[i][j] = new Pareja (j,mean[i][j]);
      }
      Arrays.sort(orden[i]);
    }

    /*building of the rankings table per algorithms and data sets*/
    rank = new Pareja[datasets.size()][algoritmos.size()];
    posicion = 0;
    for (i=0; i<datasets.size(); i++) {
      for (j=0; j<algoritmos.size(); j++){
        encontrado = false;
        for (k=0; k<algoritmos.size() && !encontrado; k++) {
          if (orden[i][k].indice == j) {
            encontrado = true;
            posicion = k+1;
          }
        }
        rank[i][j] = new Pareja(posicion,orden[i][posicion-1].valor);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (i=0; i<datasets.size(); i++) {
      visto = new boolean[algoritmos.size()];
      porVisitar= new Vector();

      Arrays.fill(visto,false);
      for (j=0; j<algoritmos.size(); j++) {
        porVisitar.removeAllElements();
        sum = rank[i][j].indice;
        visto[j] = true;
        ig = 1;
        for (k=j+1;k<algoritmos.size();k++) {
          if (rank[i][j].valor == rank[i][k].valor && !visto[k]) {
            sum += rank[i][k].indice;
            ig++;
            porVisitar.add(new Integer(k));
            visto[k] = true;
          }
        }
        sum /= (double)ig;
        rank[i][j].indice = sum;
        for (k=0; k<porVisitar.size(); k++) {
          rank[i][((Integer)porVisitar.elementAt(k)).intValue()].indice = sum;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    Rj = new double[algoritmos.size()];
    for (i=0; i<algoritmos.size(); i++){
      Rj[i] = 0;
      for (j=0; j<datasets.size(); j++) {
        Rj[i] += rank[j][i].indice / ((double)datasets.size());
      }
    }

    /*Print the average ranking per algorithm*/			
    Output = Output + "\n"+("\\begin{table}[!htp]\n" +
        "\\centering\n" +
        "\\caption{Average Rankings of the algorithms\n}"+// for "+ exp_.problemList_[prob] +" problem\n}" +
        "\\begin{tabular}{c|c}\n" +
        "Algorithm&Ranking\\\\\n\\hline");

    for (i=0; i<algoritmos.size();i++) {				
      Output = Output + "\n" + (String)algoritmos.elementAt(i)+"&"+Rj[i]+"\\\\";
    }

    Output = Output + "\n" + 
        "\\end{tabular}\n" +
        "\\end{table}"; 

    /*Compute the Friedman statistic*/
    termino1 = (12*(double)datasets.size())/((double)algoritmos.size()*((double)algoritmos.size()+1));
    termino2 = (double)algoritmos.size()*((double)algoritmos.size()+1)*((double)algoritmos.size()+1)/(4.0);
    for (i=0; i<algoritmos.size();i++) {
      sumatoria += Rj[i]*Rj[i];
    }
    friedman = (sumatoria - termino2) * termino1;

    Output = Output + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to chi-square with "+(algoritmos.size()-1)+" degrees of freedom: "+friedman+").\n\n";

    Output = Output + "\n" + "\\end{document}";		
    try {
      File latexOutput;
      latexOutput = new File(outDir);
      if(!latexOutput.exists()){
        latexOutput.mkdirs();
      }
      FileOutputStream f = new FileOutputStream(outFile);
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