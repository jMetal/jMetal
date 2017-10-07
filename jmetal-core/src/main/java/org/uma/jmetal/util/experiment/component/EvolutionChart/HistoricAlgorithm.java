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

import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public interface HistoricAlgorithm {
    public HistoryData getHistory(String indicator);
    public void setRunNumber(Integer runsNumber);
    
    
    
    public static boolean testToCalculate(Integer evaluations, Integer maxEvaluations){
        return (evaluations%(maxEvaluations/100)==0); 
    }
    
    public static void calculateIndicators(Integer evaluations, Integer maxEvaluations, String problemName, List<DoubleSolution> population, Map<String, HistoryData> history) {
        int timeIndex = (int) (evaluations / (maxEvaluations / 100)) - 1;
        Map<String, Double> indicators = HistoryData.calculateQualityIndicator(population, problemName, history.keySet());
        for (String str : history.keySet()) {//for each indicator in history
            history.get(str).addData(indicators.get(str), timeIndex);//add data
        }
    }
}
