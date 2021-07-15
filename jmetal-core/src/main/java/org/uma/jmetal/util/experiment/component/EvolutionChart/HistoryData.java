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

package org.uma.jmetal.util.experiment.component.EvolutionChart;

import org.uma.jmetal.util.experiment.component.GenerateEvolutionChart;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

/**
 *
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public class HistoryData {
    public static final String DEFAULT_BASE = "history";
    private Integer testId;
    
    private Integer numberOfData;
    private Integer numberOfTest;
    
    /**
     * Number of data (%) / number of test
     */
    private Double[][] history;
   

    public HistoryData(Integer numberOfTest) {
        this.testId = -1;
        this.numberOfData = 100;
        this.numberOfTest = numberOfTest;
        history = new Double[this.numberOfData][this.numberOfTest];

        for (int i = 0; i < this.numberOfData; i++) {
            for (int j = 0; j < this.numberOfTest; j++) {
                history[i][j] = 0.0;
            }
        }
        
    }
    
    public void addData(Double data, Integer timeIndex){
        if(timeIndex==0)testId++;
        history[timeIndex][testId] = data;
    }
    
    /**
     * Will save each line how an test
     * @param baseDir will create the directory if doesn't exist
     * @param indicator for file name
     * @param nameAlgorithm for file name
     * @param problemName  for file name
     */
    public void printAllHistory(String baseDir, String indicator, String nameAlgorithm, String problemName){
        String outputDir = baseDir + "/"+DEFAULT_BASE+"/"+nameAlgorithm+"/";
        File f = new File(outputDir);
        if(!f.exists()){
            f.mkdirs();
        }
        try (OutputStream os = new FileOutputStream(outputDir+"data_"+indicator+"_"+nameAlgorithm+"_"+problemName+".dat")) {
            PrintStream ps = new PrintStream(os);
            for (int data = 0; data < numberOfData; data++) {
                ps.print(data);
                if(data<numberOfData-1){
                    ps.print("\t");
                }
            }
            ps.print("\n");
            for (int test = 0; test < numberOfTest; test++) {
                for (int data = 0; data < numberOfData; data++) {
                    ps.print(history[data][test]);
                    if(data<numberOfData-1){
                        ps.print("\t");
                    }
                }
                ps.print("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(GenerateEvolutionChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static Map<String, Double> calculateQualityIndicator(List<DoubleSolution> population,
                                                                String problemName,
                                                                Set<String> indicatorToSave) {
        Front referenceFront;
        Map<String, Double> indicators = new HashMap();
        try {
            referenceFront = new ArrayFront("/resources/pareto_fronts/" + problemName + ".pf");
            FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
            Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
            Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population));
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront);
            //List<String> ind = new ArrayList(indicatorToSave);
            for (String string : indicatorToSave) {
                if(string.equals("HV")){
                    indicators.put("HV", new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
                } else if(string.equals("Epsilon")){
                    indicators.put("Epsilon", new Epsilon<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
                } else if(string.equals("IGD")){
                    indicators.put("IGD", new InvertedGenerationalDistancePlus<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
                } else if(string.equals("Spread")){
                    indicators.put("Spread", new Spread<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation));
                }
            }
            
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(GenerateEvolutionChart.class.getName()).log(Level.SEVERE, null, ex);
        }
        return indicators;
    }
    
    
    
    public static void printAllDataInstances(Experiment<?,?> experiment, List<String> indicators){
        System.out.println("Generating data files of algorithms evolution...");
        for (ExperimentAlgorithm<?, ?> experimentAlgorithm : experiment.getAlgorithmList()) {  
            for (String indicator : indicators) {
                HistoryData hd = ((HistoricAlgorithm) experimentAlgorithm.getAlgorithm()).getHistory(indicator);
                if (hd != null) {
                    hd.printAllHistory(experiment.getExperimentBaseDirectory(), indicator,
                            experimentAlgorithm.getAlgorithmTag(),
                            experimentAlgorithm.getProblemTag());
                }
            }
        }
    }
    
}
