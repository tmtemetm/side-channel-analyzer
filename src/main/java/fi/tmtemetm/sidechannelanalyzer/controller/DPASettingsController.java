package fi.tmtemetm.sidechannelanalyzer.controller;

import fi.tmtemetm.sidechannelanalyzer.configuration.TraceImportConfiguration;
import fi.tmtemetm.sidechannelanalyzer.model.DPAResult;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import fi.tmtemetm.sidechannelanalyzer.powermodel.impl.AESPowerModels;
import fi.tmtemetm.sidechannelanalyzer.service.DPAService;
import fi.tmtemetm.sidechannelanalyzer.service.TraceInputService;
import fi.tmtemetm.sidechannelanalyzer.util.TextFieldUtils;
import fi.tmtemetm.sidechannelanalyzer.view.impl.DPAResultsView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author tmtemetm
 */
@Controller
@RequiredArgsConstructor
public class DPASettingsController {

  private final TraceInputService traceInputService;
  private final DPAService dpaService;
  private final TraceImportConfiguration traceImportConfiguration;
  private final DPAResultsView dpaResultsView;

  @FXML
  private TextField numberOfTracesField;

  @FXML
  @Getter
  private TextField startField;

  @FXML
  @Getter
  private TextField endField;

  @Setter
  @Nullable
  private Consumer<VBox> onDPAStart;


  @FXML
  public void initialize() {
    TextFieldUtils.addIntegerValidator(numberOfTracesField);
    TextFieldUtils.addIntegerValidator(startField);
    TextFieldUtils.addIntegerValidator(endField);

    traceImportConfiguration.numberOfTracesProperty().addListener((observableValue, oldValue, newValue) -> {
      if (!StringUtils.hasText(numberOfTracesField.getText())) {
        numberOfTracesField.setText(String.valueOf(newValue.intValue()));
      }
    });
    traceImportConfiguration.traceLengthProperty().addListener((observableValue, oldValue, newValue) -> {
      if (!StringUtils.hasText(endField.getText())) {
        endField.setText(String.valueOf(newValue.intValue()));
      }
    });
  }


  @FXML
  public void handleRunDPA(ActionEvent event) throws IOException {
    if (traceImportConfiguration.getTracesFile() == null
      || traceImportConfiguration.getPlaintexts() == null
      || traceImportConfiguration.getTraceLength() == 0
      || onDPAStart == null) {
      return;
    }
    int numberOfTraces = TextFieldUtils.readIntegerTextField(numberOfTracesField,
            traceImportConfiguration.getNumberOfTraces());
    BitSet bitSet = new BitSet(numberOfTraces);
    bitSet.set(0, numberOfTraces);

    TracesContainer traces = traceInputService.readTraces(traceImportConfiguration.getTracesFile(),
            traceImportConfiguration.getTraceLength(),
            TextFieldUtils.readIntegerTextField(startField, 0),
            TextFieldUtils.readIntegerTextField(endField, traceImportConfiguration.getTraceLength()), bitSet);

    List<CompletableFuture<DPAResult>> dpaResultFutures = dpaService.analyze(traces,
            traceImportConfiguration.getPlaintexts(), AESPowerModels.FIRST_SUB_BYTES_HAMMING_WEIGHT);

    onDPAStart.accept(dpaResultsView.createRootNode(new DPAResultsController(dpaResultFutures)));
  }

}
