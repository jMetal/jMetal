package org.uma.jmetal.lab.visualization;

import java.io.IOException;
import java.util.LinkedList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.visualization.html.Html;
import org.uma.jmetal.lab.visualization.html.impl.HtmlFigure;
import org.uma.jmetal.lab.visualization.html.impl.HtmlGridView;
import org.uma.jmetal.lab.visualization.html.impl.htmlTable.impl.FriedmanTestTable;
import org.uma.jmetal.lab.visualization.html.impl.htmlTable.impl.MedianValuesTable;
import org.uma.jmetal.lab.visualization.html.impl.htmlTable.impl.WilcoxonTestTable;
import org.uma.jmetal.util.errorchecking.JMetalException;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BoxTrace;
import tech.tablesaw.plotly.traces.Scatter3DTrace;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;
/**
 * This class generates HTML files to visualize and analyze the results of a experiment.
 *
 * <p>As argument, it needs the path to the experiment base directory. The results are created in
 * the directory {@link Experiment * #getExperimentBaseDirectory()}/html. There it creates a HTML
 * file for each indicator computed.
 *
 * <p>Each HTML file is composed of: - A table with the mean value of the executions witch each
 * algorithm and problem - Wilcoxon test - Friedman ranking and Holm test - A boxplot for each
 * problem. - If a second argument is provided, it shows the best or the median front for each
 * algorithm and each problem.
 *
 * @author Javier Pérez
 */
public class StudyVisualizer {
  public enum TYPE_OF_FRONT_TO_SHOW {
    NONE,
    BEST,
    MEDIAN
  }

  private static final String INDICATOR_SUMMARY_CSV = "QualityIndicatorSummary.csv";
  // NAMES OF CSV COLUMNS
  private static final String ALGORITHM = "Algorithm";
  private static final String PROBLEM = "Problem";
  private static final String INDICATOR_NAME = "IndicatorName";
  private static final String INDICATOR_VALUE = "IndicatorValue";
  private String folderPath;
  private Table table;
  private TYPE_OF_FRONT_TO_SHOW typeOfFrontToShow;

  public StudyVisualizer(String path, TYPE_OF_FRONT_TO_SHOW typeOfFrontToShow) throws IOException {
    folderPath = path;
    table = Table.read().csv(path + "/" + INDICATOR_SUMMARY_CSV);
    this.typeOfFrontToShow = typeOfFrontToShow;
  }

  public StudyVisualizer(String path) throws IOException {
    this(path, null);
  }

  public static void main(String @NotNull [] args) throws IOException {
    String directory;

    if (args.length != 1) {
      throw new JMetalException("Argument required: name of the experiment base directory");
    } else {
      directory = args[0];
    }

    var visualizer = new StudyVisualizer(directory, TYPE_OF_FRONT_TO_SHOW.NONE);
    visualizer.createHTMLPageForEachIndicator();
  }

  public void createHTMLPageForEachIndicator() throws IOException {
    var indicators = getUniquesValuesOfStringColumn(INDICATOR_NAME);
    for (var indicator : indicators) {
      @NotNull Html htmlPage = createHtmlPage(indicator);
      htmlPage.setPathFolder(folderPath + "/html");
      htmlPage.save();
    }
  }

  Html createHtmlPage(String indicator) throws IOException {
    @NotNull Html htmlPage = new Html(indicator);

    @NotNull HtmlGridView tablesGridView = createTestTables(indicator);
    htmlPage.addComponent(tablesGridView);

    var problems = getUniquesValuesOfStringColumn(PROBLEM);
    if (typeOfFrontToShow == TYPE_OF_FRONT_TO_SHOW.NONE) {
      var boxPlotsGrid = new HtmlGridView();
      for (var problem : problems) {
        var figure = createBoxPlot(indicator, problem);
        boxPlotsGrid.addComponent(figure);
      }
      htmlPage.addComponent(boxPlotsGrid);
    } else {
      for (var problem : problems) {
        @NotNull HtmlGridView gridView = createInformationForEachProblem(indicator, problem);
        htmlPage.addComponent(gridView);
      }
    }
    return htmlPage;
  }

  private HtmlGridView createTestTables(@NotNull String indicator) {
    var algorithms = getUniquesValuesOfStringColumn(ALGORITHM);
    var problems = getUniquesValuesOfStringColumn(PROBLEM);
    var tableFilteredByIndicator = filterTableByIndicator(table, indicator);

    var minimize = true;

    var medianValuesTable =
        new MedianValuesTable(
            tableFilteredByIndicator, indicator, algorithms, problems, INDICATOR_VALUE);
    var wilcoxonTable =
        new WilcoxonTestTable(
            tableFilteredByIndicator, indicator, algorithms, problems, INDICATOR_VALUE);
    if (indicator.equals("HV")) {
      minimize = false;
    }
    @NotNull FriedmanTestTable friedmanTable =
        new FriedmanTestTable(tableFilteredByIndicator, algorithms, problems, minimize);

    var htmlGridView = new HtmlGridView();
    htmlGridView.addComponent(medianValuesTable);
    htmlGridView.addComponent(wilcoxonTable);
    htmlGridView.addComponent(friedmanTable);
    return htmlGridView;
  }

  private HtmlGridView createInformationForEachProblem(String indicator, String problem)
      throws IOException {
    var gridView = new HtmlGridView(problem);
    @NotNull HtmlFigure boxPlot = createBoxPlot(indicator, problem);
    gridView.addComponent(boxPlot);
    var algorithms = getUniquesValuesOfStringColumn(ALGORITHM);
    for (var algorithm : algorithms) {
      @NotNull HtmlFigure frontPlot = createFrontPlot(indicator, problem, algorithm);
      gridView.addComponent(frontPlot);
    }
    return gridView;
  }

  private HtmlFigure createBoxPlot(String indicator, String problem) {
    var tableFilteredByIndicator = filterTableByIndicator(table, indicator);
    var tableFilteredByIndicatorAndProblem =
        filterTableByProblem(tableFilteredByIndicator, problem);
    var trace =
        BoxTrace.builder(
                tableFilteredByIndicatorAndProblem.categoricalColumn(ALGORITHM),
                tableFilteredByIndicatorAndProblem.doubleColumn(INDICATOR_VALUE))
            .build();
    var layout = Layout.builder().title(problem).build();
    var figure = new Figure(layout, trace);
    return new HtmlFigure(figure);
  }

  private HtmlFigure createFrontPlot(String indicator, String problem, String algorithm)
      throws IOException {
    var frontFileName = "";
    if (typeOfFrontToShow == TYPE_OF_FRONT_TO_SHOW.BEST) {
      frontFileName = "BEST";
    } else if (typeOfFrontToShow == TYPE_OF_FRONT_TO_SHOW.MEDIAN) {
      frontFileName = "MEDIAN";
    }
    var csv = algorithm + "/" + problem + "/" + frontFileName + "_" + indicator + "_FUN.csv";
    var path = folderPath + "/data/" + csv;
    var csvReader = CsvReadOptions.builder(path).header(false).build();
    var funTable = Table.read().usingOptions(csvReader);
    var numberOfObjectives = funTable.columnCount();
    @Nullable Trace scatterTrace = null;
    if (numberOfObjectives == 2) {
      scatterTrace = ScatterTrace.builder(funTable.column(0), funTable.column(1)).build();
    } else if (numberOfObjectives == 3) {
      scatterTrace =
          Scatter3DTrace.builder(
                  funTable.numberColumn(0), funTable.numberColumn(1), funTable.numberColumn(2))
              .build();
    }
    var layout = Layout.builder().title(algorithm).build();
    var figure = new Figure(layout, scatterTrace);
    return new HtmlFigure(figure);
  }

  private StringColumn getUniquesValuesOfStringColumn(String columnName) {
    return dropDuplicateRowsInColumn(table.stringColumn(columnName));
  }

  public @NotNull StringColumn dropDuplicateRowsInColumn(StringColumn column) {
    var result = new LinkedList<String>();
    for (var row = 0; row < column.size(); row++) {
      if (!result.contains(column.get(row))) {
        result.add(column.get(row));
      }
    }
    return StringColumn.create(column.name(), result);
  }

  private Table filterTableByIndicator(Table table, String indicator) {
    return table.where(table.stringColumn(INDICATOR_NAME).isEqualTo(indicator));
  }

  private Table filterTableByProblem(Table table, String problem) {
    return table.where(table.stringColumn(PROBLEM).isEqualTo(problem));
  }
}
