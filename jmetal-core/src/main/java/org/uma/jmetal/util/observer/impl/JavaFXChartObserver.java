package org.uma.jmetal.util.observer.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Path;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

/**
 * JavaFX-based observer for real-time visualization of the Pareto front approximation. This
 * observer creates a JavaFX ScatterChart to display the solutions.
 */
public class JavaFXChartObserver<S extends Solution<?>> implements Observer<Map<String, Object>> {
  private static final int DEFAULT_UPDATE_INTERVAL_MS = 100;
  private static boolean platformInitialized = false;

  private final String title;
  private final int updateIntervalMs;
  private final ConcurrentLinkedQueue<DataUpdate> updateQueue;
  private final ScheduledExecutorService executorService;

  private volatile boolean initialized = false;
  private JFXPanel fxPanel;
  private ScatterChart<Number, Number> chart;
  private XYChart.Series<Number, Number> series;
  private volatile boolean running = true;

  public JavaFXChartObserver(String title) {
    this(title, DEFAULT_UPDATE_INTERVAL_MS);
  }

  public JavaFXChartObserver(String title, int updateIntervalMs) {
    this.title = title;
    this.updateIntervalMs = updateIntervalMs;
    this.updateQueue = new ConcurrentLinkedQueue<>();
    this.executorService = Executors.newSingleThreadScheduledExecutor();

    // Initialize JavaFX platform if not already done
    if (!platformInitialized) {
      synchronized (JavaFXChartObserver.class) {
        if (!platformInitialized) {
          new JFXPanel(); // Initializes the JavaFX Platform
          Platform.setImplicitExit(false);
          platformInitialized = true;
        }
      }
    }

    // Initialize the UI on the JavaFX Application Thread
    Platform.runLater(this::initFX);

    // Start update thread
    startUpdateThread();
  }

  private void initFX() {
    try {
      fxPanel = new JFXPanel();

      NumberAxis xAxis = new NumberAxis();
      NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Objective 1");
      yAxis.setLabel("Objective 2");

      chart = new ScatterChart<>(xAxis, yAxis);
      chart.setTitle(title);
      chart.setAnimated(false); // Disable animation for better performance

      series = new XYChart.Series<>();
      series.setName("Pareto Front");
      chart.getData().add(series);

      BorderPane root = new BorderPane();
      root.setCenter(chart);

      Scene scene = new Scene(root, 800, 600);
      fxPanel.setScene(scene);

      // Create and show the frame on the Event Dispatch Thread
      javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);

      initialized = true;
    } catch (Exception e) {
      JMetalLogger.logger.severe("Error initializing JavaFX: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void createAndShowGUI() {
    try {
      javax.swing.JFrame frame = new javax.swing.JFrame(title);
      frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
      frame.addWindowListener(
          new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
              dispose();
            }
          });

      frame.setContentPane(fxPanel);
      frame.pack();
      frame.setSize(800, 600);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    } catch (Exception e) {
      JMetalLogger.logger.severe("Error showing chart window: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void startUpdateThread() {
    executorService.scheduleAtFixedRate(
        () -> {
          if (!initialized || !running) {
            return;
          }

          DataUpdate update = updateQueue.poll();
          if (update != null) {
            final DataUpdate finalUpdate = update;
            Platform.runLater(() -> updateChart(finalUpdate));
          }
        },
        0,
        updateIntervalMs,
        TimeUnit.MILLISECONDS);
  }

  private void updateChart(DataUpdate update) {
    if (!running) return;

    try {
      series.getData().clear();

      for (int i = 0; i < update.objective1Values.length; i++) {
        series
            .getData()
            .add(new XYChart.Data<>(update.objective1Values[i], update.objective2Values[i]));
      }

      chart.setTitle(title + " (Evaluations: " + update.evaluations + ")");
    } catch (Exception e) {
      JMetalLogger.logger.warning("Error updating chart: " + e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    if (!running) return;

    try {
      Integer evaluations = (Integer) data.get("EVALUATIONS");
      List<S> population = (List<S>) data.get("POPULATION");

      if (evaluations != null && population != null && !population.isEmpty()) {
        double[] obj1 = new double[population.size()];
        double[] obj2 = new double[population.size()];

        for (int i = 0; i < population.size(); i++) {
          S solution = population.get(i);
          obj1[i] = solution.objectives()[0];
          obj2[i] = solution.objectives()[1];
        }

        updateQueue.offer(new DataUpdate(obj1, obj2, evaluations));
      } else {
        JMetalLogger.logger.warning(
            getClass().getName()
                + " : insufficient data for generating real-time information."
                + " Either EVALUATIONS or POPULATION keys have not been registered yet by the algorithm");
      }
    } catch (Exception e) {
      JMetalLogger.logger.warning("Error in update: " + e.getMessage());
    }
  }

  private static class DataUpdate {
    final double[] objective1Values;
    final double[] objective2Values;
    final int evaluations;

    DataUpdate(double[] obj1, double[] obj2, int eval) {
      this.objective1Values = obj1;
      this.objective2Values = obj2;
      this.evaluations = eval;
    }
  }

  /**
 * Sets a reference front to be displayed as a line in the chart
 * @param referenceFront 2D array of reference points, where each row is a point and each column is an objective
 * @param seriesName Name of the reference front series in the chart
 */
public void setFront(double[][] referenceFront, String seriesName) {
    if (referenceFront == null || referenceFront.length == 0) {
        return;
    }

    Platform.runLater(() -> {
        // Remove any existing reference series
        chart.getData().removeIf(series -> 
            ((XYChart.Series<?, ?>)series).getName().equals(seriesName));
        
        // Create new series for the reference front
        XYChart.Series<Number, Number> referenceSeries = new XYChart.Series<>();
        referenceSeries.setName(seriesName);
        
        // Add points to the series
        for (double[] point : referenceFront) {
            if (point != null && point.length >= 2) {
                referenceSeries.getData().add(
                    new XYChart.Data<>(point[0], point[1]));
            }
        }
        
        // Add the reference series to the chart
        chart.getData().add(referenceSeries);
        
        // Apply CSS styling to the series
        for (Node node : chart.lookupAll(".series" + (chart.getData().size() - 1))) {
            if (node instanceof Path) {
                node.setStyle("-fx-stroke: #FF0000; -fx-stroke-width: 2px;");
            }
        }
        
        // Hide the points
        for (XYChart.Series<Number, Number> s : chart.getData()) {
            if (s.getName().equals(seriesName)) {
                for (XYChart.Data<Number, Number> d : s.getData()) {
                    Node node = d.getNode();
                    if (node != null) {
                        node.setVisible(false);
                    }
                }
            }
        }
    });
}

  public void dispose() {
    running = false;
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
