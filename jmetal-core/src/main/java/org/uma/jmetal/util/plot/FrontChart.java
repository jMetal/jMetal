package org.uma.jmetal.util.plot;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class FrontChart {

  private final XYChart chart;
  private SwingWrapper<XYChart> swingWrapper;
  private final String legend;
  private boolean firstUpdate = true;

  public FrontChart(String title, String xAxisTitle, String yAxisTitle, String legend) {
    chart = new XYChartBuilder()
        .width(800)
        .height(600)
        .title(title)
        .xAxisTitle(xAxisTitle)
        .yAxisTitle(yAxisTitle)
        .build();

    this.legend = legend;

    chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
    chart.getStyler().setMarkerSize(5);
  }

  private double[] getObjectiveValues(double[][] data, int obj) {
    double[] values = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      values[i] = data[i][obj];
    }
    return values;
  }

  public void setFront(double[][] front, String frontName) {
    double[] x = getObjectiveValues(front, 0);
    double[] y = getObjectiveValues(front, 1);
    var series = chart.addSeries(frontName, x, y);
    series.setMarkerColor(Color.blue);
    series.setMarker(SeriesMarkers.CIRCLE);
  }

  public void setPoint(double x, double y, String pointName) {
    XYSeries pointSeries = chart.addSeries(pointName,
        new double[]{x},
        new double[]{y});
    pointSeries.setMarker(SeriesMarkers.CIRCLE);
    //referencePointSeries.setMarkerColor(Color.green);
  }

  public void updateChart(double[] x, double[] y) {
    this.updateChart(Arrays.stream(x).boxed().collect(Collectors.toList()),
        Arrays.stream(y).boxed().collect(Collectors.toList()));
  }

  public void updateChart(List<Double> x, List<Double> y) {
    if (firstUpdate) {
      swingWrapper = new SwingWrapper<>(chart);
      swingWrapper.displayChart();

      firstUpdate = false;
      var series = chart.addSeries(legend, x, y);
      series.setMarkerColor(Color.BLACK);
      series.setMarker(SeriesMarkers.CIRCLE);
    } else {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      chart.updateXYSeries(legend, x, y, null);
      swingWrapper.repaintChart();
    }
  }

  public XYChart chart()  {
    return chart ;
  }
}
