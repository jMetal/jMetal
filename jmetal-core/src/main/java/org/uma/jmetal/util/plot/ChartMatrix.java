package org.uma.jmetal.util.plot;

import java.util.List;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class ChartMatrix {
  public ChartMatrix(List<XYChart> charts) {
    new SwingWrapper<XYChart>(charts).displayChartMatrix() ;
  }
}
