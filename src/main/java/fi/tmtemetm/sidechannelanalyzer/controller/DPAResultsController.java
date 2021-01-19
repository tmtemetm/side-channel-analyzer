package fi.tmtemetm.sidechannelanalyzer.controller;

import fi.tmtemetm.sidechannelanalyzer.model.DPAResult;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author tmtemetm
 */
public class DPAResultsController {
  private static final int PROGRESS_BAR_MARGINS = 50;

  private final List<CompletableFuture<DPAResult>> dpaResults;
  private final Node[] detailsViews;

  public DPAResultsController(List<CompletableFuture<DPAResult>> dpaResults) {
    this.dpaResults = dpaResults;
    this.detailsViews = new Node[dpaResults.size()];
    Arrays.fill(this.detailsViews, new Label("Waiting for operation to complete"));
    this.completedBytes = new AtomicInteger(0);
  }

  @FXML
  private VBox vBox;

  @FXML
  private ChoiceBox<String> byteChoice;

  @FXML
  private ProgressBar progressBar;
  private final AtomicInteger completedBytes;

  @FXML
  private HBox fullKey;

  @FXML
  private HBox details;

  public void initialize() {
    progressBar.prefWidthProperty()
            .bind(vBox.widthProperty()
                    .subtract(PROGRESS_BAR_MARGINS));

    fullKey.getChildren()
            .addAll(dpaResults.stream()
                    .map(this::hexByteFromDPAResultFuture)
                    .collect(Collectors.toList()));

    byteChoice.getItems()
            .addAll(IntStream.range(0, dpaResults.size())
                    .mapToObj(this::choiceFromDPAResultFutureIndex)
                    .collect(Collectors.toList()));
    byteChoice.getSelectionModel()
            .select(0);
  }


  private Label hexByteFromDPAResultFuture(CompletableFuture<DPAResult> dpaResultFuture) {
    Label label = new Label("??");
    dpaResultFuture.thenApply(dpaResult -> {
      double progress = (double) completedBytes.incrementAndGet() / dpaResults.size();
      Platform.runLater(() -> {
        label.setText(String.format("%02x", Byte.toUnsignedInt(dpaResult.getByteHypotheses()
                .get(0)
                .getKeyByte())));
        if (progress == 1) {
          progressBar.setVisible(false);
        } else if (progress > progressBar.getProgress()) {
          progressBar.setProgress(progress);
        }
      });
      return dpaResult;
    });
    return label;
  }


  private String choiceFromDPAResultFutureIndex(int index) {
    dpaResults.get(index).thenApply(dpaResult -> {
      detailsViews[index] = tableViewFromDPAResult(dpaResult);
      return dpaResult;
    });
    return "Byte " + index;
  }

  private TableView<DPAResult.KeyByteHypothesis> tableViewFromDPAResult(DPAResult dpaResult) {
    TableView<DPAResult.KeyByteHypothesis> tableView = new TableView<>();

    TableColumn<DPAResult.KeyByteHypothesis, String> byteColumn = new TableColumn<>("Byte");
    byteColumn.setCellValueFactory(features -> new SimpleStringProperty(String.format("%02x", Byte.toUnsignedInt(features.getValue().getKeyByte()))));

    TableColumn<DPAResult.KeyByteHypothesis, String> timeColumn = new TableColumn<>("Time");
    timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

    TableColumn<DPAResult.KeyByteHypothesis, String> correlationColumn = new TableColumn<>("Correlation");
    correlationColumn.setCellValueFactory(new PropertyValueFactory<>("correlation"));

    TableColumn<DPAResult.KeyByteHypothesis, String> pValueColumn = new TableColumn<>("P-value (approximate)");
    pValueColumn.setCellValueFactory(new PropertyValueFactory<>("pValue"));

    tableView.getColumns()
            .addAll(List.of(byteColumn, timeColumn, correlationColumn, pValueColumn));
    tableView.getItems()
            .addAll(dpaResult.getByteHypotheses());
    HBox.setHgrow(tableView, Priority.ALWAYS);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    return tableView;
  }


  @FXML
  public void handleChoice(ActionEvent event) {
    details.getChildren()
            .clear();
    details.getChildren()
            .add(detailsViews[byteChoice.getSelectionModel()
                    .getSelectedIndex()]);
  }

}
