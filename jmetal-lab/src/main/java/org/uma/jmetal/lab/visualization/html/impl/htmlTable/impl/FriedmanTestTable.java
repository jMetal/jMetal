package org.uma.jmetal.lab.visualization.html.impl.htmlTable.impl;

import org.uma.jmetal.lab.experiment.util.FriedmanTest;
import org.uma.jmetal.lab.visualization.html.impl.htmlTable.HtmlTable;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.text.DecimalFormat;
/**
 * This class transforms the results of FriedmanTest into a {@link HtmlTable}.
 *
 * <p>It creates 5 columns: Algorithm, Ranking, p-value, Holm and Hypothesis
 *
 * @author Javier Pérez Abad
 */
public class FriedmanTestTable extends HtmlTable<String> {

  public FriedmanTestTable(
      Table table, StringColumn algorithms, StringColumn problems, boolean minimizar) {
    this.title = "Friedman ranking and Holm test";
    FriedmanTest test = new FriedmanTest(minimizar, table, algorithms, problems, "IndicatorValue");
    test.computeHolmTest();
    Table ranking = test.getResults();
    this.headersColumn = ranking.columnNames().toArray(new String[0]);
    this.data = new String[algorithms.size()][ranking.columnCount()];
    for (int i = 0; i < algorithms.size(); i++) {
      for (int j = 0; j < ranking.columnCount(); j++) {
        if (j == ranking.columnIndex("Algorithm")) {
          this.data[i][j] = ranking.stringColumn(0).get(i);
        } else if (j == ranking.columnIndex("Hypothesis")) {
          this.data[i][j] = ranking.stringColumn(j).get(i);
        } else if (j == ranking.columnIndex("p-value")) {
          DecimalFormat format = new DecimalFormat("0.###E0");
          this.data[i][j] = format.format(ranking.doubleColumn(j).get(i));
        } else {
          DecimalFormat format = new DecimalFormat("##.###");
          this.data[i][j] = format.format(ranking.doubleColumn(j).get(i));
        }
      }
    }
  }
}
