package fi.tmtemetm.sidechannelanalyzer.controller;

import fi.tmtemetm.sidechannelanalyzer.compressor.SignalCompressor;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * @author tmtemetm
 */
@Slf4j
public class TracesPlotController {
  private static final int PREFERRED_TICKS = 10;
  private static final double Y_AXIS_VALUES_RATIO = 1.1;

  private final TracesContainer traces;
  private final SignalCompressor compressor;

  @FXML
  private LineChart<Number, Number> lineChart;
  @FXML
  private NumberAxis xAxis;
  @FXML
  private NumberAxis yAxis;

  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Label coordinateLabel;

  @FXML
  private ToggleGroup toggleGroup;

  @FXML
  private Rectangle zoomBox;

  private boolean insidePlotArea;

  @Nullable
  private Point2D startDragPosition;
  @Nullable
  private Point2D startDragValues;

  private MouseFunction mouseFunction;
  private Cursor currentCursor;

  private int start;
  private int end;

  private final IntegerProperty selectionStart;
  private final IntegerProperty selectionEnd;

  public ReadOnlyIntegerProperty selectionStartProperty() {
    return selectionStart;
  }

  public ReadOnlyIntegerProperty selectionEndProperty() {
    return selectionEnd;
  }

  private Point2D lowerBounds;
  private Point2D upperBounds;

  private final Deque<XYBounds> zoomHistory;

  public TracesPlotController(TracesContainer traces, SignalCompressor compressor) {
    this.traces = traces;
    this.compressor = compressor;
    this.start = traces.getStart();
    this.end = traces.getEnd();
    this.selectionStart = new SimpleIntegerProperty(this, "selection start", this.start);
    this.selectionEnd = new SimpleIntegerProperty(this, "selection end", this.end);
    this.zoomHistory = new ArrayDeque<>();
    this.mouseFunction = MouseFunction.DRAG;
    this.currentCursor = mouseFunction.openCursor;
    this.insidePlotArea = false;
    this.lowerBounds = new Point2D(0, 0);
    this.upperBounds = new Point2D(0, 0);
  }

  @FXML
  public void initialize() {
    TimeValueExtrema extrema = drawPoints();
    xAxis.setAutoRanging(false);
    yAxis.setAutoRanging(false);
    setDisplayBounds(new Point2D(extrema.minTime, Y_AXIS_VALUES_RATIO * extrema.minValue),
            new Point2D(extrema.maxTime, Y_AXIS_VALUES_RATIO * extrema.maxValue));
    lowerBounds = new Point2D(xAxis.getLowerBound(), yAxis.getLowerBound());
    upperBounds = new Point2D(xAxis.getUpperBound(), yAxis.getUpperBound());
    pushZoomHistory();
  }

  private TimeValueExtrema drawPoints() {
    ObservableList<XYChart.Series<Number, Number>> lineChartData = lineChart.getData();
    lineChartData.clear();

    int[] times = traces.getTimes();
    int startIndex = Math.min(findIndex(times, start, false), times.length - 1);
    int endIndex = Math.max(findIndex(times, end, true), 1);

    int minTime = traces.getTime(startIndex);
    int maxTime = traces.getTime(endIndex - 1);
    double minValue = Double.MAX_VALUE;
    double maxValue = Double.MIN_VALUE;

    for (int i = 0; i < traces.getNumberOfTraces(); i++) {
      double[] inputSignal = (startIndex == 0 && endIndex == times.length)
              ? traces.getTrace(i)
              : Arrays.copyOfRange(traces.getTrace(i), startIndex, endIndex);
      SignalCompressor.CompressionResult traceCompressionResult = compressor.compress(inputSignal);
      double[] trace = traceCompressionResult.getSignal();
      int[] indices = traceCompressionResult.getIndices();

      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      ObservableList<XYChart.Data<Number, Number>> seriesData = series.getData();
      for (int j = 0; j < trace.length; j++) {
        double dataValue = trace[j];
        if (dataValue < minValue) {
          minValue = dataValue;
        } else if (dataValue > maxValue) {
          maxValue = dataValue;
        }
        seriesData.add(new XYChart.Data<>(times[indices[j] + startIndex], dataValue));
      }

      series.setName("Trace " + traces.getIndex(i));
      lineChartData.add(series);
    }
    return new TimeValueExtrema(minTime, maxTime, minValue, maxValue);
  }

  private int findIndex(int[] times, int time, boolean upperLimit) {
    int defaultIndex = upperLimit ? times.length - 1 : 0;
    int index = times[defaultIndex] == time
            ? defaultIndex
            : Arrays.binarySearch(times, time);
    if (index < 0) {
      return -(index + 1);
    }
    if (upperLimit) {
      return index + 1;
    }
    return index;
  }


  @FXML
  public void handleMouseMoved(MouseEvent event) {
    Point2D point = getValuesForMousePosition(event);
    double x = point.getX();
    double y = point.getY();
    if (x < xAxis.getLowerBound() || x > xAxis.getUpperBound()
            || y < yAxis.getLowerBound() || y > yAxis.getUpperBound()) {
      handleMouseExitPlotArea();
      return;
    }
    handleMouseEnterPlotArea();

    coordinateLabel.setText(String.format("%.3f\n%.3f\n", x, y));
  }

  @FXML
  public void handleMouseExited(MouseEvent event) {
    handleMouseExitPlotArea();
    if (startDragValues != null || startDragPosition != null) {
      startDragValues = null;
      startDragPosition = null;
      zoomBox.setVisible(false);
    }
  }

  private void handleMouseEnterPlotArea() {
    if (!insidePlotArea) {
      coordinateLabel.setVisible(true);
      anchorPane.setCursor(currentCursor);
      insidePlotArea = true;
    }
  }

  private void handleMouseExitPlotArea() {
    if (insidePlotArea) {
      coordinateLabel.setVisible(false);
      anchorPane.setCursor(Cursor.DEFAULT);
      insidePlotArea = false;
    }
  }

  @FXML
  public void handleMousePressed(MouseEvent event) {
    currentCursor = mouseFunction.closedCursor;
    anchorPane.setCursor(currentCursor);
    startDragPosition = new Point2D(event.getX(), event.getY());
    if (mouseFunction == MouseFunction.ZOOM) {
      startDragValues = getValuesForMousePosition(event);
      zoomBox.setX(startDragPosition.getX());
      zoomBox.setY(startDragPosition.getY());
      zoomBox.setWidth(0);
      zoomBox.setHeight(0);
      zoomBox.setVisible(true);
    }
  }

  @FXML
  public void handleMouseDragged(MouseEvent event) {
    handleMouseMoved(event);
    if (startDragPosition != null) {
      if (mouseFunction == MouseFunction.ZOOM) {
        zoomBox.setX(Math.min(event.getX(), startDragPosition.getX()));
        zoomBox.setWidth(Math.abs(event.getX() - startDragPosition.getX()));
        zoomBox.setY(Math.min(event.getY(), startDragPosition.getY()));
        zoomBox.setHeight(Math.abs(event.getY() - startDragPosition.getY()));
      } else if (mouseFunction == MouseFunction.DRAG && isZoomed()) {
        double dx = (event.getX() - startDragPosition.getX()) / xAxis.getScale();
        double dy = (event.getY() - startDragPosition.getY()) / yAxis.getScale();
        setDisplayBounds(new Point2D(lowerBounds.getX() - dx, lowerBounds.getY() - dy),
                new Point2D(upperBounds.getX() - dx, upperBounds.getY() - dy));
      }
    }
  }

  @FXML
  public void handleMouseDragReleased(MouseEvent event) {
    if (startDragPosition != null) {
      if (mouseFunction == MouseFunction.ZOOM && startDragValues != null) {
        Point2D endDragValues = getValuesForMousePosition(event);

        Point2D lowerBounds = new Point2D(Math.min(startDragValues.getX(), endDragValues.getX()),
                Math.min(startDragValues.getY(), endDragValues.getY()));
        Point2D upperBounds = new Point2D(Math.max(startDragValues.getX(), endDragValues.getX()),
                Math.max(startDragValues.getY(), endDragValues.getY()));
        setBounds(lowerBounds, upperBounds);
      } else if (mouseFunction == MouseFunction.DRAG && isZoomed()) {
        Point2D lowerBounds = new Point2D(xAxis.getLowerBound(), yAxis.getLowerBound());
        Point2D upperBounds = new Point2D(xAxis.getUpperBound(), yAxis.getUpperBound());
        setBounds(lowerBounds, upperBounds);
      }
    }
    startDragValues = null;
    startDragPosition = null;
    zoomBox.setVisible(false);
  }

  private void setDisplayBounds(Point2D lowerBounds, Point2D upperBounds) {
    double oldWidth = xAxis.getUpperBound() - xAxis.getLowerBound();
    double oldHeight = yAxis.getUpperBound() - yAxis.getLowerBound();
    double newWidth = upperBounds.getX() - lowerBounds.getX();
    double newHeight = upperBounds.getY() - lowerBounds.getY();
    double xTickUint = getTickUnit(newWidth);
    double yTickUint = getTickUnit(newHeight);
    if (newWidth > oldWidth) {
      xAxis.setTickUnit(xTickUint);
    }
    if (newHeight > oldHeight) {
      yAxis.setTickUnit(yTickUint);
    }
    xAxis.setLowerBound(lowerBounds.getX());
    xAxis.setUpperBound(upperBounds.getX());
    yAxis.setLowerBound(lowerBounds.getY());
    yAxis.setUpperBound(upperBounds.getY());
    if (newWidth < oldWidth) {
      xAxis.setTickUnit(xTickUint);
    }
    if (newHeight < oldHeight) {
      yAxis.setTickUnit(yTickUint);
    }
  }

  private double getTickUnit(double dimension) {
    return Math.round(dimension / PREFERRED_TICKS);
  }

  private void setBounds(Point2D lowerBounds, Point2D upperBounds) {
    double xInterval = upperBounds.getX() - lowerBounds.getX();
    applyXYBounds(new XYBounds(
            (int) Math.floor(lowerBounds.getX() - xInterval),
            (int) Math.ceil(upperBounds.getX() + xInterval),
            (int) Math.ceil(lowerBounds.getX()),
            (int) Math.floor(upperBounds.getX()),
            lowerBounds,
            upperBounds));
    this.lowerBounds = lowerBounds;
    this.upperBounds = upperBounds;
    pushZoomHistory();
  }


  private Point2D getValuesForMousePosition(MouseEvent event) {
    double xPosInAxis = xAxis.sceneToLocal(new Point2D(event.getSceneX(), 0)).getX();
    double yPosInAxis = yAxis.sceneToLocal(new Point2D(0, event.getSceneY())).getY();
    return new Point2D(xAxis.getValueForDisplay(xPosInAxis).doubleValue(),
            yAxis.getValueForDisplay(yPosInAxis).doubleValue());
  }


  private void pushZoomHistory() {
    zoomHistory.push(new XYBounds(start, end, selectionStart.get(), selectionEnd.get(), lowerBounds, upperBounds));
  }

  @FXML
  public void popZoomHistory(Event event) {
    if (zoomHistory.isEmpty()) {
      return;
    }
    applyXYBounds(zoomHistory.pop());
  }

  @FXML
  public void revertZoom(Event event) {
    if (zoomHistory.isEmpty()) {
      return;
    }
    applyXYBounds(zoomHistory.peekLast());
    zoomHistory.clear();
  }

  private void applyXYBounds(XYBounds xyBounds) {
    start = xyBounds.start;
    end = xyBounds.end;
    selectionStart.setValue(xyBounds.selectedStart);
    selectionEnd.setValue(xyBounds.selectedEnd);
    drawPoints();
    setDisplayBounds(xyBounds.lowerBounds, xyBounds.upperBounds);
  }

  private boolean isZoomed() {
    return !zoomHistory.isEmpty();
  }


  @FXML
  public void useDrag(Event event) {
    mouseFunction = MouseFunction.DRAG;
    currentCursor = mouseFunction.openCursor;
  }

  @FXML
  public void useZoom(Event event) {
    mouseFunction = MouseFunction.ZOOM;
    currentCursor = mouseFunction.openCursor;
  }


  @Getter
  @RequiredArgsConstructor
  private static class XYBounds {
    private final int start;
    private final int end;
    private final int selectedStart;
    private final int selectedEnd;
    private final Point2D lowerBounds;
    private final Point2D upperBounds;
  }

  @AllArgsConstructor
  private enum MouseFunction {
    DRAG(Cursor.MOVE, Cursor.MOVE),
    ZOOM(Cursor.CROSSHAIR, Cursor.CROSSHAIR);

    private final Cursor openCursor;
    private final Cursor closedCursor;
  }

  @Getter
  @RequiredArgsConstructor
  private static class TimeValueExtrema {
    private final int minTime;
    private final int maxTime;
    private final double minValue;
    private final double maxValue;
  }
}
