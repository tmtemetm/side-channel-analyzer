package fi.tmtemetm.sidechannelanalyzer.controller;

import fi.tmtemetm.sidechannelanalyzer.compressor.impl.ExtremaPreservingSignalCompressor;
import fi.tmtemetm.sidechannelanalyzer.configuration.TraceImportConfiguration;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import fi.tmtemetm.sidechannelanalyzer.service.TraceInputService;
import fi.tmtemetm.sidechannelanalyzer.util.TextFieldUtils;
import fi.tmtemetm.sidechannelanalyzer.view.impl.TracesPlotView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author tmtemetm
 */
@Controller
@RequiredArgsConstructor
public class TracesController {
  private static final int MAXIMUM_PLOT_POINTS = 2000;

  private final TraceInputService traceInputService;
  private final TracesPlotView tracesPlotView;
  private final TraceImportConfiguration traceImportConfiguration;

  private final DPASettingsController dpaSettingsController;

  @FXML
  private SplitPane splitPane;

  @FXML
  private TitledPane plotTracesPane;

  @FXML
  private VBox traceSelection;

  @FXML
  private TextField startField;

  @FXML
  private TextField endField;

  @FXML
  private Label currentSelectionStart;

  @FXML
  private Label currentSelectionEnd;

  @Setter
  private Runnable onRunDPAOnCurrentSelection;


  @FXML
  public void initialize() {
    traceImportConfiguration.numberOfTracesProperty().addListener((observableValue, oldValue, newValue) -> {
      ObservableList<Node> children = traceSelection.getChildren();
      int currentSize = children.size();
      if (currentSize < newValue.intValue()) {
        children.addAll(IntStream.range(currentSize, newValue.intValue())
                .mapToObj(traceNumber -> new CheckBox("Trace " + traceNumber))
                .collect(Collectors.toList()));
      } else if (currentSize > newValue.intValue()) {
        children.remove(newValue.intValue(), currentSize);
      }
    });

    TextFieldUtils.addIntegerValidator(startField);
    TextFieldUtils.addIntegerValidator(endField);

    traceImportConfiguration.traceLengthProperty().addListener((observableValue, oldValue, newValue) -> {
      if (!StringUtils.hasText(endField.getText())) {
        endField.setText(String.valueOf(newValue));
      }
    });
  }


  @FXML
  public void handleSelectAll(ActionEvent event) {
    traceSelection.getChildren()
            .forEach(node -> {
              if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(true);
              }
            });
  }

  @FXML
  public void handleUnselectAll(ActionEvent event) {
    traceSelection.getChildren()
            .forEach(node -> {
              if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
              }
            });
  }


  @FXML
  public void handlePlotTraces(ActionEvent event) throws IOException {
    if (traceImportConfiguration.getTracesFile() == null
            || traceImportConfiguration.getTraceLength() == 0) {
      return;
    }

    BitSet bitSet = new BitSet(traceSelection.getChildren()
            .size());
    Iterator<Node> childIterator = traceSelection.getChildren()
            .iterator();
    int index = 0;
    while (childIterator.hasNext()) {
      Node next = childIterator.next();
      if (next instanceof CheckBox) {
        if (((CheckBox) next).isSelected()) {
          bitSet.set(index);
        }
        index++;
      }
    }

    if (splitPane.getItems().size() > 1) {
      currentSelectionStart.textProperty().unbind();
      currentSelectionEnd.textProperty().unbind();
      splitPane.getItems().remove(1);
    }


    TracesContainer traces = traceInputService.readTraces(traceImportConfiguration.getTracesFile(),
            traceImportConfiguration.getTraceLength(), TextFieldUtils.readIntegerTextField(startField, 0),
            TextFieldUtils.readIntegerTextField(endField, traceImportConfiguration.getTraceLength()), bitSet);

    TracesPlotController tracesPlotController = new TracesPlotController(traces,
            ExtremaPreservingSignalCompressor.ofMaxLength(MAXIMUM_PLOT_POINTS));

    splitPane.getItems()
            .add(tracesPlotView.createRootNode(tracesPlotController));

    currentSelectionStart.textProperty()
            .bind(tracesPlotController.selectionStartProperty()
                    .asString());
    currentSelectionEnd.textProperty()
            .bind(tracesPlotController.selectionEndProperty()
                    .asString());
  }


  @FXML
  public void handlePlotCurrentSelection(ActionEvent event) throws IOException {
    startField.setText(currentSelectionStart.getText());
    endField.setText(currentSelectionEnd.getText());

    plotTracesPane.setExpanded(true);
    handlePlotTraces(event);
  }

  @FXML
  public void handleRunDPAOnCurrentSelection(ActionEvent event) {
    dpaSettingsController.getStartField().setText(currentSelectionStart.getText());
    dpaSettingsController.getEndField().setText(currentSelectionEnd.getText());
    onRunDPAOnCurrentSelection.run();
  }

}
