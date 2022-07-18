package org.uma.jmetal.lab.experiment.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

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
          @NotNull StringColumn algorithms,
          @NotNull StringColumn problems,
          String indicatorValueColumnName) {
    this.minimizar = minimizar;
    numberOfAlgorithms = algorithms.size();
    datasetCount = table.rowCount();
    var mean =
        computeAveragePerformancePerAlgorithm(
            table, algorithms, problems, indicatorValueColumnName);
    var order = computeAndOrderRanking(mean, algorithms, problems);
    var rank = buildRanking(order, algorithms, problems);
    results = Table.create();
    results.addColumns(algorithms, DoubleColumn.create("Ranking", rank));
    results = results.sortAscendingOn("Ranking");
  }

  private double[][] computeAveragePerformancePerAlgorithm(
          Table table,
          @NotNull StringColumn algorithms,
          @NotNull StringColumn problems,
          String indicatorValueColumnName) {
    var mean = new double[problems.size()][algorithms.size()];
    for (var i = 0; i < problems.size(); i++) {
      var tableFilteredByProblem = filterTableBy(table, problems.name(), problems.get(i));
      for (var j = 0; j < algorithms.size(); j++) {
        var tableFilteredByProblemAndAlgorithm =
            filterTableBy(tableFilteredByProblem, algorithms.name(), algorithms.get(j));
        var results =
            tableFilteredByProblemAndAlgorithm.doubleColumn(indicatorValueColumnName);
        mean[i][j] = results.mean();
      }
    }
    return mean;
  }

  private Pareja[][] computeAndOrderRanking(
      double[][] mean, StringColumn algorithms, StringColumn problems) {
    var orden = new Pareja[problems.size()][algorithms.size()];
    for (var i = 0; i < problems.size(); i++) {
      for (var j = 0; j < algorithms.size(); j++) {
        orden[i][j] = new Pareja(j, mean[i][j], minimizar);
      }
      Arrays.sort(orden[i]);
    }
    return orden;
  }

  private double[] buildRanking(Pareja[][] orden, @NotNull StringColumn algorithms, @NotNull StringColumn problems) {
    var rank = new Pareja[problems.size()][algorithms.size()];
    var position = 0;
    for (var i = 0; i < problems.size(); i++) {
      for (var j = 0; j < algorithms.size(); j++) {
        var found = false;
        for (var k = 0; k < algorithms.size() && !found; k++) {
          if (orden[i][k].getIndice() == j) {
            found = true;
            position = k + 1;
          }
        }
        rank[i][j] = new Pareja(position, orden[i][position - 1].getValor(), minimizar);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (var i = 0; i < problems.size(); i++) {
      var seen = new boolean[algorithms.size()];
      @NotNull ArrayList<Integer> notVisited = new ArrayList<>();
      Arrays.fill(seen, false);
      for (var j = 0; j < algorithms.size(); j++) {
        notVisited.clear();
        var sum = rank[i][j].getIndice();
        seen[j] = true;
        var ig = 1;
        for (var k = j + 1; k < algorithms.size(); k++) {
          if (rank[i][j].getValor() == rank[i][k].getValor() && !seen[k]) {
            sum += rank[i][k].getIndice();
            ig++;
            notVisited.add(k);
            seen[k] = true;
          }
        }
        sum /= (double) ig;
        rank[i][j].setIndice(sum);
        for (var k = 0; k < notVisited.size(); k++) {
          rank[i][notVisited.get(k)].setIndice(sum);
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    var averageRanking = new double[algorithms.size()];
    for (var i = 0; i < algorithms.size(); i++) {
      averageRanking[i] = 0;
      for (var j = 0; j < problems.size(); j++) {
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
    var SE =
        Math.sqrt(
            (double) numberOfAlgorithms
                * ((double) numberOfAlgorithms + 1)
                / (6.0 * (double) datasetCount));
    var rankingValues = results.doubleColumn("Ranking");
    var pValues = new double[numberOfAlgorithms];
    pValues[0] = 0;
    for (var i = 1; i < numberOfAlgorithms; i++) {
      var z = (rankingValues.get(0) - rankingValues.get(i)) / SE;
      pValues[i] = 2 * CDFNormal.normp((-1) * Math.abs(z));
    }
    var holm = DoubleColumn.create("p-value", pValues);
    results.addColumns(holm);
  }

  private void computeHolmValues() {
    var holmValues = new double[numberOfAlgorithms];
    holmValues[0] = 0;
    for (var i = 1; i < numberOfAlgorithms; i++) {
      holmValues[i] = 0.05 / (double) (i);
    }
    var holm = DoubleColumn.create("Holm", holmValues);
    results.addColumns(holm);
  }

  private void studyHypothesis() {
    var pValues = results.doubleColumn("p-value");
    var holmValues = results.doubleColumn("Holm");
    var hypothesis = new String[numberOfAlgorithms];
    hypothesis[0] = "-";
    for (var i = numberOfAlgorithms - 1; i > 0; i--) {
      if (i < numberOfAlgorithms - 1 && hypothesis[i + 1].equals("Accepted")) {
        hypothesis[i] = "Accepted";
      } else if (pValues.get(i) < holmValues.get(i)) {
        hypothesis[i] = "Rejected";
      } else {
        hypothesis[i] = "Accepted";
      }
    }
    var hypothesisColumn = StringColumn.create("Hypothesis", hypothesis);
    results.addColumns(hypothesisColumn);
  }

  public Table getResults() {
    return results;
  }

  private Table filterTableBy(Table table, String columnName, String value) {
    return table.where(table.stringColumn(columnName).isEqualTo(value));
  }
}
