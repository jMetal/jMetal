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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.experiment.component.EvolutionChart.HistoryData;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;

/**
 * Generate a line chart of evolution process of indicators
 *     The line is the means value of the runs, and dotted lines is the first and third quartiles
 * Default: Execute the indicator at each 1% of evaluations;
 * Use "HV" "IGD" "Spread" "Epsilon"
 *  Attention! To many objectives can be very slow with HV
 * For use this you need:
 * <b>1. (at Algorithm) Implement "HistoricAlgorithm.java" in you algorithm</b>
 * 
 * <b>2. (at Algorithm) Add "HistoricData.java" how an attribute and implement methods</b>
 *      for example: "Map<String, HistoryData> history;" Name of indicator and data
 *      @Override
 *       public HistoryData getHistory(String indicator) {
 *           return history.get(indicator);
 *       }
 *
 *       @Override
 *       public void setRunNumber(Integer runsNumber) {
 *           history = new HashMap<>();
 *           history.put("HV", new HistoryData(runsNumber));
 *           history.put("IGD", new HistoryData(runsNumber));
 *           history.put("Epsilon", new HistoryData(runsNumber));
 *           history.put("Spread", new HistoryData(runsNumber)); 
 *       }
 * 
 * <b>3. (at Algorithm) Execute indicators</b>
 *      for example:
 *          if(HistoricAlgorithm.testToCalculate(evaluations,maxEvaluations)){
 *               HistoricAlgorithm.calculateIndicators(evaluations, maxEvaluations, problem.getName(), population,history);
 *           }
 * 
 * <b>4. (at Experiment) Generate data files and R scripts</b>
 *      for example:
 *          HistoryData.printAllDataInstances(experiment, indicators);
 *          new GenerateEvolutionChart(experiment, indicators).run();
 *          
 * 
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public class GenerateEvolutionChart implements ExperimentComponent{
    
    Experiment<?,?> experiment;
    List<String> indicators;
    String outputDir;
    
    public GenerateEvolutionChart(Experiment experiment, List<String> indicators) {
        this.experiment = experiment;
        this.indicators = indicators;
        this.outputDir = experiment.getExperimentBaseDirectory() + "/EvolutionProcess/";
    }
    
    

    @Override
    public void run() throws IOException {
        for (int i = 0; i < experiment.getProblemList().size(); i++) {
            String problem = (experiment.getProblemList().get(i)).getTag() ;

            File f = new File(outputDir);
            if(!f.exists()){
                f.mkdirs();
            }
            List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>> > algorithmList = new ArrayList();
            //Set algorithm of this problem
            for (Object algorithm : experiment.getAlgorithmList()) {
                ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>> alg = (ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>) algorithm;
                if(alg.getProblemTag().equals(problem)){
                    algorithmList.add(alg);
                }
            }
            String mainScript= "pdf(\"plot_"+problem+".pdf\")\n";
            for (String indicator : indicators) {
                String script = generateRscript(algorithmList, indicator, problem);
                try (OutputStream os = new FileOutputStream(outputDir+"Rscript_chart_"+indicator+"_"+problem+".R")) {
                    PrintStream ps = new PrintStream(os);
                    ps.print(script);
                    ps.close();
                }
                mainScript+="source(\"Rscript_chart_"+indicator+"_"+problem+".R\")\n";
            }
            try (OutputStream os = new FileOutputStream(outputDir+"main_"+problem+".R")) {
                PrintStream ps = new PrintStream(os);
                ps.print(mainScript);
                ps.close();
            }
        }
    }
    
    
    
    
    private String generateRscript(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>> > algorithmList, String indicator, String problem){
        int i = 1;
        String str = "# Read data file\n" ;
        for (ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>> algorithm : algorithmList) {
            str += "algorithm"+i+" <- read.table(\"../"+HistoryData.DEFAULT_BASE+"/"+algorithm.getAlgorithmTag()+"/data_"+indicator+"_"+algorithm.getAlgorithmTag()+"_"+problem+".dat\", header=T, sep=\"\\t\") \n" ;
            i++;
        }
        str+="# Compute the max and min y \n" ;
        str += "max_y <- max(";
        for (i = 1; i<algorithmList.size()+1;i++) {
            str+="algorithm"+i;
            if(i<algorithmList.size()){
                str+=", ";
            }
        }
        str+=")\n";
        str += "min_y <- min(";
        for (i = 1; i<algorithmList.size()+1;i++) {
            str+="algorithm"+i;
            if(i<algorithmList.size()){
                str+=", ";
            }
        }
        str+=")\n\n";
        for (i = 1; i<algorithmList.size()+1;i++) {
            str += "algorithm"+i+"mean <- c()\n" +
            "algorithm"+i+"Q <- c()\n" +
            "\n" +
            "#calcules mean, 1o quartil and 3 quartil\n" +
            "for(n in 1:100){ \n" +
            "	algorithm"+i+"mean<-append(algorithm"+i+"mean,mean(algorithm"+i+"[,n]))\n" +
            "	algorithm"+i+"Q <-rbind(algorithm"+i+"Q,quantile(algorithm"+i+"[,n], c(0.25,0.75),type=1))#rbind is to merge, or, add new line in data frame\n" +
            "}\n" +
            "\n" ;
        }
        str+=
        "# Define colors to be used\n" +
        "plot_colors <- c(\"blue\",\"red\",\"forestgreen\", \"gray60\",\"yellow\", \"brown\", \"darkorange\", \"deepskyblue\")\n" +
        "\n" +
        "plot(algorithm1mean, type=\"o\", col=plot_colors[1],  ylim=c(min_y,max_y), axes=FALSE, ann=FALSE, pch='.', lty=1)\n" +
        "\n" +
        "lines(algorithm1Q[,1], type=\"o\", pch='.', lty=2, col=plot_colors[1])\n" +
        "lines(algorithm1Q[,2], type=\"o\", pch='.', lty=2, col=plot_colors[1])\n\n" ;
        
        for (i = 2; i<algorithmList.size()+1;i++) {
            str+= 
            "lines(algorithm"+i+"mean, type=\"o\", pch='.', lty=1, col=plot_colors["+(i)+"])\n" +
            "lines(algorithm"+i+"Q[,1], type=\"o\", pch='.', lty=2, col=plot_colors["+(i)+"])\n" +
            "lines(algorithm"+i+"Q[,2], type=\"o\", pch='.', lty=2, col=plot_colors["+(i)+"])\n\n" ;
        }
        str+=
        "\n" +
        "# Make x,y axis \n" +
        "axis(1, at=seq(0,100,5))\n" +
        "axis(2, at=seq(min_y, max_y, 0.05))\n" +
        "\n" +
        "# Create box around plot\n" +
        "box()\n" +
        "\n" +
        "# Create a title bold/italic font\n" +
        "title(main=\"Quality Indicator "+indicator+" for "+problem+"\", font.main=4)\n" +
        "\n" +
        "# Label the x and y axes\n" +
        "title(xlab= \"% of Evolutions\")\n" +
        "title(ylab= \""+indicator+" value\")\n" +
        "\n" +
        "# Create a legend\n" +
        "legend(\"bottomright\", c(";
        i=0;
        for (ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>> algorithm : algorithmList) {
            str+= "\""+algorithm.getAlgorithmTag()+"\"";
            if(i<algorithmList.size()-1){
                str+=", ";
            }
            i++;
        }
        str+="), cex=0.8, col=plot_colors, pch=21:23, lty=1:3);";
        return str;
    }   
}
