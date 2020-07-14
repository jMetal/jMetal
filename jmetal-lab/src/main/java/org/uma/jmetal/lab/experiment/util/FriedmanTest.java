package org.uma.jmetal.lab.experiment.util;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.Arrays;

public class FriedmanTest {

  public static final boolean MINIMIZAR = true;
  public static final boolean MAXIMIZAR = false;

  private Table results;
  private boolean minimizar;
  private int numberOfAlgorithms;
  private int datasetCount;

  public FriedmanTest(
      boolean minimizar,
      Table table,
      StringColumn algorithms,
      StringColumn problems,
      String indicatorValueColumnName) {
    this.minimizar = minimizar;
    numberOfAlgorithms = algorithms.size();
    datasetCount = table.rowCount();
    double[][] mean =
        computeAveragePerformancePerAlgorithm(
            table, algorithms, problems, indicatorValueColumnName);
    Pareja[][] order = computeAndOrderRanking(mean, algorithms, problems);
    double[] rank = buildRanking(order, algorithms, problems);
    results = Table.create();
    results.addColumns(algorithms, DoubleColumn.create("Ranking", rank));
    results = results.sortAscendingOn("Ranking");
  }

  private double[][] computeAveragePerformancePerAlgorithm(
      Table table,
      StringColumn algorithms,
      StringColumn problems,
      String indicatorValueColumnName) {
    double[][] mean = new double[problems.size()][algorithms.size()];
    for (int i = 0; i < problems.size(); i++) {
      Table tableFilteredByProblem = filterTableBy(table, problems.name(), problems.get(i));
      for (int j = 0; j < algorithms.size(); j++) {
        Table tableFilteredByProblemAndAlgorithm =
            filterTableBy(tableFilteredByProblem, algorithms.name(), algorithms.get(j));
        DoubleColumn results =
            tableFilteredByProblemAndAlgorithm.doubleColumn(indicatorValueColumnName);
        mean[i][j] = results.mean();
      }
    }
    return mean;
  }

  private Pareja[][] computeAndOrderRanking(
      double[][] mean, StringColumn algorithms, StringColumn problems) {
    Pareja[][] orden = new Pareja[problems.size()][algorithms.size()];
    for (int i = 0; i < problems.size(); i++) {
      for (int j = 0; j < algorithms.size(); j++) {
        orden[i][j] = new Pareja(j, mean[i][j], minimizar);
      }
      Arrays.sort(orden[i]);
    }
    return orden;
  }

  private double[] buildRanking(Pareja[][] orden, StringColumn algorithms, StringColumn problems) {
    Pareja[][] rank = new Pareja[problems.size()][algorithms.size()];
    int position = 0;
    for (int i = 0; i < problems.size(); i++) {
      for (int j = 0; j < algorithms.size(); j++) {
        boolean found = false;
        for (int k = 0; k < algorithms.size() && !found; k++) {
          if (orden[i][k].getIndice() == j) {
            found = true;
            position = k + 1;
          }
        }
        rank[i][j] = new Pareja(position, orden[i][position - 1].getValor(), minimizar);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (int i = 0; i < problems.size(); i++) {
      boolean[] seen = new boolean[algorithms.size()];
      ArrayList<Integer> notVisited = new ArrayList<>();
      Arrays.fill(seen, false);
      for (int j = 0; j < algorithms.size(); j++) {
        notVisited.clear();
        double sum = rank[i][j].getIndice();
        seen[j] = true;
        int ig = 1;
        for (int k = j + 1; k < algorithms.size(); k++) {
          if (rank[i][j].getValor() == rank[i][k].getValor() && !seen[k]) {
            sum += rank[i][k].getIndice();
            ig++;
            notVisited.add(k);
            seen[k] = true;
          }
        }
        sum /= (double) ig;
        rank[i][j].setIndice(sum);
        for (int k = 0; k < notVisited.size(); k++) {
          rank[i][notVisited.get(k)].setIndice(sum);
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    double[] averageRanking = new double[algorithms.size()];
    for (int i = 0; i < algorithms.size(); i++) {
      averageRanking[i] = 0;
      for (int j = 0; j < problems.size(); j++) {
        averageRanking[i] += rank[j][i].getIndice() / ((double) problems.size());
      }
    }

    return averageRanking;
  }

  public void computeHolmTest() {
    computePValues();
    computeHolmValues();
    studyHypothesis();
  }

  private void computePValues() {
    double SE =
        Math.sqrt(
            (double) numberOfAlgorithms
                * ((double) numberOfAlgorithms + 1)
                / (6.0 * (double) datasetCount));
    DoubleColumn rankingValues = results.doubleColumn("Ranking");
    double[] pValues = new double[numberOfAlgorithms];
    pValues[0] = 0;
    for (int i = 1; i < numberOfAlgorithms; i++) {
      double z = (rankingValues.get(0) - rankingValues.get(i)) / SE;
      pValues[i] = 2 * CDFNormal.normp((-1) * Math.abs(z));
    }
    DoubleColumn holm = DoubleColumn.create("p-value", pValues);
    results.addColumns(holm);
  }

  private void computeHolmValues() {
    double[] holmValues = new double[numberOfAlgorithms];
    holmValues[0] = 0;
    for (int i = 1; i < numberOfAlgorithms; i++) {
      holmValues[i] = 0.05 / (double) (i);
    }
    DoubleColumn holm = DoubleColumn.create("Holm", holmValues);
    results.addColumns(holm);
  }

  private void studyHypothesis() {
    DoubleColumn pValues = results.doubleColumn("p-value");
    DoubleColumn holmValues = results.doubleColumn("Holm");
    String[] hypothesis = new String[numberOfAlgorithms];
    hypothesis[0] = "-";
    for (int i = numberOfAlgorithms - 1; i > 0; i--) {
      if (i < numberOfAlgorithms - 1 && hypothesis[i + 1].equals("Accepted")) {
        hypothesis[i] = "Accepted";
      } else if (pValues.get(i) < holmValues.get(i)) {
        hypothesis[i] = "Rejected";
      } else {
        hypothesis[i] = "Accepted";
      }
    }
    StringColumn hypothesisColumn = StringColumn.create("Hypothesis", hypothesis);
    results.addColumns(hypothesisColumn);
  }

  public Table getResults() {
    return results;
  }

  private Table filterTableBy(Table table, String columnName, String value) {
    return table.where(table.stringColumn(columnName).isEqualTo(value));
  }
}
