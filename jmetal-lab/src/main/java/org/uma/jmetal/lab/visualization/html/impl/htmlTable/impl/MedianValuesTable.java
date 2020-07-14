package org.uma.jmetal.lab.visualization.html.impl.htmlTable.impl;

import org.uma.jmetal.lab.visualization.html.impl.htmlTable.HtmlTable;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
/**
 * This class shows the mean values of the executions with each algorithm and each problem. It also
 * highlights the best and second best algorithms for each problem.
 *
 * @author Javier Pérez Abad
 */
public class MedianValuesTable extends HtmlTable<Double> {

  private static final String[] INDICATORS_TO_MAXIMIZE = {"HV"};

  private static final String BEST_COLOR = "#41e05c";
  private static final String SECOND_BEST_COLOR = "#a3e6ae";
  private Objective objective = Objective.MINIMIZE;

  public MedianValuesTable(
      Table table,
      String indicator,
      StringColumn algorithms,
      StringColumn problems,
      String indicatorValueColumnName) {
    this.title = "Median values";
    this.headersColumn = algorithms.asObjectArray();
    this.headersRow = problems.asObjectArray();
    this.data = new Double[problems.size()][algorithms.size()];

    for (int row = 0; row < problems.size(); row++) {

      Table tableFilteredByProblem = filterTableBy(table, problems.name(), problems.get(row));

      for (int column = 0; column < algorithms.size(); column++) {
        Table tableCompleteFiltered =
            filterTableBy(tableFilteredByProblem, algorithms.name(), algorithms.get(column));
        DoubleColumn indicatorValues = tableCompleteFiltered.doubleColumn(indicatorValueColumnName);
        this.data[row][column] = indicatorValues.median();
      }
    }

    if (Arrays.asList(INDICATORS_TO_MAXIMIZE).contains(indicator)) {
      objective = Objective.MAXIMIZE;
    }
  }

  private Table filterTableBy(Table table, String columnName, String value) {
    return table.where(table.stringColumn(columnName).isEqualTo(value));
  }

  public MedianValuesTable setObjective(Objective objective) {
    this.objective = objective;
    return this;
  }

  protected StringBuilder createRowOfData(int index) {
    DecimalFormat format = new DecimalFormat("0.#####E0");
    StringBuilder html = new StringBuilder();
    Double[] orderedData = getDataOrderedByObjective(data[index]);
    for (Object elem : data[index]) {
      html.append("<td ");
      if (!orderedData[0].equals(orderedData[orderedData.length - 1])) {
        if (elem == orderedData[0]) html.append(" class='best'");
        else if (elem == orderedData[1]) html.append(" class='secondBest'");
      }
      html.append(">").append(format.format(elem)).append("</td>");
    }
    return html;
  }

  private Double[] getDataOrderedByObjective(Double[] data) {
    Double[] orderedData = data.clone();
    if (objective == Objective.MAXIMIZE) Arrays.sort(orderedData, Collections.reverseOrder());
    if (objective == Objective.MINIMIZE) Arrays.sort(orderedData);
    return orderedData;
  }

  public String getCSS() {
    StringBuilder stringBuilder = new StringBuilder(super.getCSS());
    stringBuilder.append(".best { background-color: ").append(BEST_COLOR).append("; } ");
    stringBuilder
        .append(".secondBest { background-color: ")
        .append(SECOND_BEST_COLOR)
        .append("; } ");
    return stringBuilder.toString();
  }

  public enum Objective {
    MAXIMIZE,
    MINIMIZE
  }
}
