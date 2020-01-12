package org.uma.jmetal.lab.experiment.component;

import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentComponent;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a R script that generates an eps file containing boxplots
 *
 * <p>The results are a set of R files that are written in the directory {@link Experiment
 * #getExperimentBaseDirectory()}/R. Each file is called as indicatorName.Wilcoxon.R
 *
 * <p>To run the R script: Rscript indicatorName.Wilcoxon.R To generate the resulting Latex file:
 * pdflatex indicatorName.Wilcoxon.tex
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateBoxplotsWithTablesawAndR implements ExperimentComponent {
  private static final String OUTPUT_DIRECTORY = "tablesaw";
  private int numberOfRows;
  private int numberOfColumns;
  private boolean displayNotch;
  private String experimentBaseDirectory;
  private String csvSummaryFile;

  public GenerateBoxplotsWithTablesawAndR(
      String csvSummaryFile,
      int numberOfRows,
      int numberOfColumns,
      boolean displayNotch,
      String experimentBaseDirectory) {
    this.csvSummaryFile = csvSummaryFile;
    this.numberOfRows = numberOfRows;
    this.numberOfColumns = numberOfColumns;
    this.displayNotch = displayNotch;
    this.experimentBaseDirectory = experimentBaseDirectory;
  }

  @Override
  public void run() throws IOException {
    Table table = Table.read().csv(experimentBaseDirectory + "/" + csvSummaryFile);
    System.out.println(table.structure());

    StringColumn algorithmNames = (table.select("Algorithm").dropDuplicateRows()).stringColumn(0);
    StringColumn problemNames = (table.select("Problem").dropDuplicateRows()).stringColumn(0);
    StringColumn indicatorNames =
        (table.select("IndicatorName").dropDuplicateRows()).stringColumn(0);

    System.out.println("Algorithms");
    for (String name : algorithmNames) {
      System.out.println(name);
    }

    System.out.println("Problems");
    for (String name : problemNames) {
      System.out.println(name);
    }

    System.out.println("Indicators");
    for (String name : indicatorNames) {
      System.out.println(name);
    }

    int numberOfRuns = table.select("ExecutionId").dropDuplicateRows().rowCount();
    System.out.println("Number of runs: " + numberOfRuns);




    Table summary = Table.create("Summary");
    Column<String> algs = StringColumn.create("Problem", problemNames.asList());
    summary.addColumns(algs);

    for (String alg : algorithmNames) {
      System.out.println(alg + ": ");
      List<Double> medians = new ArrayList<>();
      List<Double> iqrs = new ArrayList<>();
      for (String prob : problemNames) {
        System.out.println(" -> " + prob);
        Table filtered =
            table.where(
                table
                    .stringColumn("Algorithm")
                    .isEqualTo(alg)
                    .and(table.stringColumn("Problem").isEqualTo(prob))
                    .and(table.stringColumn("IndicatorName").isEqualTo("HV")));

        Table indicatorValues = filtered.select("IndicatorValue");
        DoubleColumn hv = indicatorValues.doubleColumn(0);

        // System.out.println(hv.mean());
        medians.add(hv.median());
        iqrs.add(hv.quartile3() - hv.quartile1());
      }

      medians.forEach(v -> System.out.println(v + " "));

      Column<Double> median = DoubleColumn.create(alg, medians.toArray(new Double[medians.size()]));
      System.out.println(median);
      Column<Double> iqr = DoubleColumn.create(alg, iqrs.toArray(new Double[iqrs.size()]));
      summary.addColumns(median);
    }

    System.out.println(summary);
  }

  public static void main(String[] args) throws IOException {
    ExperimentComponent component =
        new GenerateBoxplotsWithTablesawAndR(
            "jmetal-lab/src/main/resources/QualityIndicatorSummary.csv", 4, 3, false, ".");

    component.run();
  }
}
