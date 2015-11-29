package org.uma.jmetal.util.experiment.component;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.*;
import java.util.*;

/**
 * Created by ajnebro on 29/11/15.
 */
public class GenerateLatexTablesWithStatistics implements ExperimentComponent {
  private static final String DEFAULT_OUTPUT_DIRECTORY = "latex" ;

  private final ExperimentConfiguration<?, ?> configuration;

  private double[][][] mean;
  private double[][][] median;
  private double[][][] stdDeviation;
  private double[][][] iqr;
  private double[][][] max;
  private double[][][] min;
  private int[][][] numberOfValues;

  public GenerateLatexTablesWithStatistics(ExperimentConfiguration<?, ?> configuration) {
    this.configuration = configuration ;
  }

  @Override
  public void run() throws IOException {
    String latexDirectoryName = configuration.getExperimentBaseDirectory() + "/" + DEFAULT_OUTPUT_DIRECTORY;

    List<List<List<List<Double>>>> data = readDataFromFiles() ;
    computeDataStatistics(data) ;

  }

  private List<List<List<List<Double>>>> readDataFromFiles() throws IOException {
    List<List<List<List<Double>>>> data = new ArrayList<List<List<List<Double>>>>(configuration.getIndicatorList().size()) ;

    for (int indicator = 0; indicator < configuration.getIndicatorList().size(); indicator++ ) {
      // A data vector per problem
      data.add(indicator, new ArrayList<List<List<Double>>>()) ;
      for (int problem = 0; problem < configuration.getProblemList().size(); problem++) {
        data.get(indicator).add(problem, new ArrayList[configuration.getAlgorithmList().size());

        for (int algorithm = 0; algorithm < configuration.getAlgorithmList().size(); algorithm++) {
          data.get(indicator).get(problem).add(algorithm, new ArrayList());

          String directory = configuration.getExperimentBaseDirectory();
          directory += "/data/";
          directory += "/" + configuration.getAlgorithmList().get(algorithm);
          directory += "/" + configuration.getProblemList().get(problem);
          directory += "/" + configuration.getIndicatorList().get(indicator);
          // Read values from data files
          FileInputStream fis = new FileInputStream(directory);
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader br = new BufferedReader(isr);
          //System.out.println(directory);
          String aux = br.readLine();
          while (aux != null) {
            data.get(indicator).get(problem).get(algorithm).add(Double.parseDouble(aux));
            aux = br.readLine();
          }
        }
      }
    }

    return data ;
  }

  private void computeDataStatistics(List<List<List<List<Double>>>> data) {
    int indicatorListSize = configuration.getIndicatorList().size() ;
    mean = new double[indicatorListSize][][];
    median = new double[indicatorListSize][][];
    stdDeviation = new double[indicatorListSize][][];
    iqr = new double[indicatorListSize][][];
    min = new double[indicatorListSize][][];
    max = new double[indicatorListSize][][];
    numberOfValues = new int[indicatorListSize][][];

    int problemListSize = configuration.getProblemList().size() ;
    for (int indicator = 0; indicator < indicatorListSize; indicator++) {
      // A data vector per problem
      mean[indicator] = new double[problemListSize][];
      median[indicator] = new double[problemListSize][];
      stdDeviation[indicator] = new double[problemListSize][];
      iqr[indicator] = new double[problemListSize][];
      min[indicator] = new double[problemListSize][];
      max[indicator] = new double[problemListSize][];
      numberOfValues[indicator] = new int[problemListSize][];

      int algorithmListSize = configuration.getAlgorithmList().size() ;
      for (int problem = 0; problem < problemListSize; problem++) {
        mean[indicator][problem] = new double[algorithmListSize];
        median[indicator][problem] = new double[algorithmListSize];
        stdDeviation[indicator][problem] = new double[algorithmListSize];
        iqr[indicator][problem] = new double[algorithmListSize];
        min[indicator][problem] = new double[algorithmListSize];
        max[indicator][problem] = new double[algorithmListSize];
        numberOfValues[indicator][problem] = new int[algorithmListSize];

        for (int algorithm = 0; algorithm < algorithmListSize; algorithm++) {
          Collections.sort(data.get(indicator).get(problem).get(algorithm));

          Map<String, Double> statValues = computeStatistics(data.get(indicator).get(problem).get(algorithm)) ;

          mean[indicator][problem][algorithm] = statValues.get("mean");
          median[indicator][problem][algorithm] = statValues.get("median");
          stdDeviation[indicator][problem][algorithm] = statValues.get("stdDeviation");
          iqr[indicator][problem][algorithm] = statValues.get("iqr");
          min[indicator][problem][algorithm] = statValues.get("min");
          max[indicator][problem][algorithm] = statValues.get("max");
          numberOfValues[indicator][problem][algorithm] = statValues.get("numberOfElements").intValue();
        }
      }
    }
  }

  /**
   * Computes the statistical values
   * @param values
   * @return
   */
  private Map<String, Double> computeStatistics(List<Double> values) {
    Map<String, Double> results = new HashMap<>() ;

    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (Double value : values) {
      stats.addValue(value);
    }

    results.put("mean", stats.getMean()) ;
    results.put("median", stats.getPercentile(50.0)) ;
    results.put("stdDeviation", stats.getStandardDeviation());
    results.put("iqr", stats.getPercentile(75) - stats.getPercentile(25));
    results.put("max", stats.getMax());
    results.put("min", stats.getMean());
    results.put("numberOfElements", (double)values.size()) ;

    return results ;
  }
}
