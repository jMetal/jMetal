//  Friedman.java
//
//  THIS CLASS IS NOT FINISHED.
//
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

package jmetal.experiments.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import jmetal.experiments.Experiment;

public class Friedman {

	Experiment exp_;

	public Friedman(Experiment exp){
		exp_ = exp;
	}

	public void executeFriedmanTest(String indic){
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

		String Output = new String();
		Output = Output + ("\\documentclass{article}\n" +
				"\\usepackage{graphicx}\n" +
				//"\\usepackage{lscape}\n" +
				"\\title{Results}\n" +
				"\\author{}\n" +
				"\\date{\\today}\n" +
				"\\begin{document}\n" +
				//"\\begin{landscape}\n" +
				"\\oddsidemargin 0in \\topmargin 0in" +
				"\\maketitle\n" +
				"\\section{Tables of Friedman Tests}");

		for(int prob = 0; prob<exp_.problemList_.length; prob++){		
			algoritmos = new Vector();
			datasets = new Vector();
			datos = new Vector();


			for(int alg = 0; alg<exp_.algorithmNameList_.length; alg++){
				algoritmos.add(new String(exp_.algorithmNameList_[alg]));
				datos.add(new Vector());

				String ruta = exp_.experimentBaseDirectory_ + "/data/" 
						+ exp_.algorithmNameList_[alg] + "/"
						+ exp_.problemList_[prob] + "/" + indicator_;	

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
				int fila = 1;
				while (lineas.hasMoreTokens()) {
					if(alg == 0){
						datasets.add(new String(""+fila));
					}
					linea = lineas.nextToken();

					((Vector)datos.elementAt(alg)).add(new Double(linea));
					fila++;
				}
			}

			mean = new double[datasets.size()][algoritmos.size()];

			/*Compute the average performance per algorithm for each data set*/		
			for (i=0; i<datasets.size(); i++) {
				for (j=0; j<algoritmos.size(); j++) {
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
					"\\caption{Average Rankings of the algorithms for "+ exp_.problemList_[prob] +" problem\n}" +
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

		}
		//Output = Output + "\n" + "\\end{landscape}";
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