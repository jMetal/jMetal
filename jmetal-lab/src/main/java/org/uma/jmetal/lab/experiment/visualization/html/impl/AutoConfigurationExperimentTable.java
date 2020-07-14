package org.uma.jmetal.lab.experiment.visualization.html.impl;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class AutoConfigurationExperimentTable extends HtmlTable<Object> {

  private static final String[] CREATE_INITIAL_SOLUTIONS =
      new String[] {"random", "latinHypercubeSampling", "scatterSearch"};
  private static final String[] SELECTION = new String[] {"tournament", "random"};
  private static final String[] CROSSOVER_AND_MUTATION =
      new String[] {"crossoverAndMutationVariation"};
  private static final String[] CROSSOVER = new String[] {"SBX", "BLX_ALPHA"};
  private static final String[] REPAIR_STRATEGY = new String[] {"random", "round", "bounds"};
  private static final String[] MUTATION = new String[] {"uniform", "polynomial"};
  private static final String[] RESULT = new String[] {"externalArchive", "population"};
  private static final int[] POPULATION_SIZE_ARCHIVE = new int[] {10, 20, 50, 100, 200};
  private static final int[] OFFSPRING_POPULATION = new int[] {1, 10, 50, 100, 200, 400};

  public AutoConfigurationExperimentTable(Table table) {
    this.headersColumn = table.columnNames().toArray(new String[] {});
    this.data = new Object[table.rowCount() + 1][table.columnCount()];

    for (int columnIndex = 0; columnIndex < table.columns().size(); columnIndex++) {
      this.data[data.length - 1][columnIndex] = "";
    }

    this.data[data.length - 1][0] = "MEAN";

    for (int columnIndex = 0; columnIndex < table.columns().size(); columnIndex++) {
      Column<?> column = table.column(columnIndex);
      fillHtmlTableData(column, columnIndex);
      switch (column.name()) {
        case "createInitialSolutions":
        case "selection":
        case "variation":
        case "offspringPopulationSize":
        case "crossover":
        case "crossoverRepairStrategy":
        case "mutation":
        case "mutationRepairStrategy":
        case "algorithmResult":
          {
            selectMostCountedAsResult(table, column);
            break;
          }
        case "crossoverProbability":
        case "mutationProbability":
          {
            selectAverageValueAsResult(table, column);
            break;
          }
        case "selectionTournamentSize":
          {
            int valueDependsOn = 0; // "tournament"
            String columnDependsOn = "selection";
            if ((int) getResultValueOf(table, columnDependsOn) == valueDependsOn) {
              Table filteredTable =
                  tableFilteredByDependencies(table, columnDependsOn, valueDependsOn);
              selectMostCountedAsResult(filteredTable, column);
            }
            break;
          }
        case "sbxDistributionIndex":
          {
            int valueDependsOn = 0; // "SBX"
            String columnDependsOn = "crossover";
            if ((int) getResultValueOf(table, columnDependsOn) == valueDependsOn) {
              Table filteredTable =
                  tableFilteredByDependencies(table, columnDependsOn, valueDependsOn);
              selectAverageValueAsResult(filteredTable, filteredTable.column(columnIndex));
            }
            break;
          }
        case "blxAlphaCrossoverAlphaValue":
          {
            int valueDependsOn = 1; // "BLX_ALPHA"
            String columnDependsOn = "crossover";
            if ((int) getResultValueOf(table, columnDependsOn) == valueDependsOn) {
              Table filteredTable =
                  tableFilteredByDependencies(table, columnDependsOn, valueDependsOn);
              selectAverageValueAsResult(filteredTable, filteredTable.column(columnIndex));
            }
            break;
          }
        case "polynomialMutationDistributionIndex":
          {
            int valueDependsOn = 1; // "polynomial"
            String columnDependsOn = "mutation";
            if ((int) getResultValueOf(table, columnDependsOn) == valueDependsOn) {
              Table filteredTable =
                  tableFilteredByDependencies(table, columnDependsOn, valueDependsOn);
              selectAverageValueAsResult(filteredTable, filteredTable.column(columnIndex));
            }
            break;
          }
        case "uniformMutationPerturbation":
          {
            int valueDependsOn = 0; // "uniform"
            String columnDependsOn = "mutation";
            if ((int) getResultValueOf(table, columnDependsOn) == valueDependsOn) {
              Table filteredTable =
                  tableFilteredByDependencies(table, columnDependsOn, valueDependsOn);
              selectAverageValueAsResult(filteredTable, filteredTable.column(columnIndex));
            }
            break;
          }
        case "populationSizeWithArchive":
          {
            int valueDependsOn = 0; // "externalArchive"
            String columnDependsOn = "algorithmResult";
            if ((int) getResultValueOf(table, columnDependsOn) == valueDependsOn) {
              Table filteredTable =
                  tableFilteredByDependencies(table, columnDependsOn, valueDependsOn);
              selectMostCountedAsResult(filteredTable, column);
            }
            break;
          }
      }
    }

    transformData(table);
  }

  private void fillHtmlTableData(Column<?> column, int columnIndex) {
    for (int rowIndex = 0; rowIndex < column.size(); rowIndex++) {
      this.data[rowIndex][columnIndex] = column.get(rowIndex);
    }
  }

  private void selectMostCountedAsResult(Table table, Column<?> column) {
    String columnName = column.name();
    int columnIndex = table.columnIndex(column.name());
    Table countValuesTable = table.xTabCounts(columnName).sortDescendingOn("Count");
    Object mostRepeatedValue = countValuesTable.column("Category").get(0);
    this.data[data.length - 1][columnIndex] = mostRepeatedValue;
  }

  private void selectAverageValueAsResult(Table table, Column<?> column) {
    int columnIndex = table.columnIndex(column);
    NumericColumn<?> numericColumn = (NumericColumn<?>) column;
    double mean = numericColumn.mean();
    this.data[data.length - 1][columnIndex] = mean;
  }

  private Object getResultValueOf(Table table, String columnName) {
    int columnIndex = table.columnIndex(columnName);
    return data[data.length - 1][columnIndex];
  }

  private Table tableFilteredByDependencies(
      Table table, String columnNameDependsOn, int valueDependsOn) {
    return table.where(table.intColumn(columnNameDependsOn).isEqualTo(valueDependsOn));
  }

  private void transformData(Table table) {
    for (int columnIndex = 0; columnIndex < data[0].length; columnIndex++) {
      Column<?> column = table.column(columnIndex);
      for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
        if (this.data[rowIndex][columnIndex] != "") {
          switch (column.name()) {
            case "createInitialSolutions":
              {
                this.data[rowIndex][columnIndex] =
                    CREATE_INITIAL_SOLUTIONS[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "selection":
              {
                this.data[rowIndex][columnIndex] = SELECTION[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "variation":
              {
                this.data[rowIndex][columnIndex] =
                    CROSSOVER_AND_MUTATION[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "offspringPopulationSize":
              {
                this.data[rowIndex][columnIndex] =
                    OFFSPRING_POPULATION[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "crossover":
              {
                this.data[rowIndex][columnIndex] = CROSSOVER[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "crossoverRepairStrategy":
              {
                this.data[rowIndex][columnIndex] =
                    REPAIR_STRATEGY[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "mutation":
              {
                this.data[rowIndex][columnIndex] = MUTATION[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "mutationRepairStrategy":
              {
                this.data[rowIndex][columnIndex] =
                    REPAIR_STRATEGY[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "algorithmResult":
              {
                this.data[rowIndex][columnIndex] = RESULT[(int) data[rowIndex][columnIndex]];
                break;
              }
            case "populationSizeWithArchive":
              {
                this.data[rowIndex][columnIndex] =
                    POPULATION_SIZE_ARCHIVE[(int) data[rowIndex][columnIndex]];
                break;
              }
          }
        }
      }
    }
  }
}
