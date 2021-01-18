package fi.tmtemetm.sidechannelanalyzer.controller;

import fi.tmtemetm.sidechannelanalyzer.configuration.JavaFXConfiguration;
import fi.tmtemetm.sidechannelanalyzer.configuration.TraceImportConfiguration;
import fi.tmtemetm.sidechannelanalyzer.service.TraceInputService;
import fi.tmtemetm.sidechannelanalyzer.util.TextFieldUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

/**
 * @author tmtemetm
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ImportTracesController {

  private final JavaFXConfiguration.StageProvider stageProvider;
  private final TraceInputService traceInputService;
  private final TraceImportConfiguration traceImportConfiguration;

  @FXML
  private TextField traceLengthField;

  @FXML
  private Label tracesFilename;

  @FXML
  private Label plaintextFilename;

  private final FileChooser tracesFileChooser = new FileChooser();
  private final FileChooser plaintextFileChooser = new FileChooser();

  private final BooleanProperty allFieldsSet = new SimpleBooleanProperty(this, "allFieldsSet", false);

  public ReadOnlyBooleanProperty allFieldsSetProperty() {
    return allFieldsSet;
  }


  @FXML
  public void initialize() {
    tracesFileChooser.getExtensionFilters()
            .addAll(new FileChooser.ExtensionFilter("Bin files", "*.bin"),
                    new FileChooser.ExtensionFilter("All files", "*.*"));
    plaintextFileChooser.getExtensionFilters()
            .addAll(new FileChooser.ExtensionFilter("Text files", "*.txt"),
                    new FileChooser.ExtensionFilter("All files", "*.*"));

    TextFieldUtils.addIntegerValidator(traceLengthField);
    traceLengthField.textProperty().addListener((observableValue, oldValue, newValue) -> {
      try {
        traceImportConfiguration.setTraceLength(Integer.parseInt(newValue));
        if (traceImportConfiguration.getTracesFile() != null && traceImportConfiguration.getPlaintexts() != null) {
          allFieldsSet.set(true);
        }
      } catch (NumberFormatException e) {
        // empty
      }
    });
  }

  @FXML
  public void handleTraceFileSelect(ActionEvent event) throws IOException {
    File file = tracesFileChooser.showOpenDialog(stageProvider.getStage());
    if (file == null) {
      return;
    }
    traceImportConfiguration.setTracesFile(new FileSystemResource(file));
    tracesFilename.setText(file.getName());
    if (traceImportConfiguration.getPlaintexts() != null) {
      calculateTraceLengthIfDivisible();
      if (traceImportConfiguration.getTraceLength() != 0) {
        allFieldsSet.set(true);
      }
    }
  }

  @FXML
  public void handlePlaintextsFileSelect(ActionEvent event) throws IOException {
    File file = plaintextFileChooser.showOpenDialog(stageProvider.getStage());
    if (file == null) {
      return;
    }
    traceImportConfiguration.setPlaintexts(traceInputService.readPlainTexts(new FileSystemResource(file)));
    plaintextFilename.setText(file.getName());
    if (traceImportConfiguration.getTracesFile() != null) {
      calculateTraceLengthIfDivisible();
      if (traceImportConfiguration.getTraceLength() != 0) {
        allFieldsSet.set(true);
      }
    }
  }

  private void calculateTraceLengthIfDivisible() throws IOException {
    if (traceImportConfiguration.getTracesFile() == null || traceImportConfiguration.getPlaintexts() == null) {
      return;
    }
    long contentLength = traceImportConfiguration.getTracesFile()
            .contentLength();
    int traces = traceImportConfiguration.getPlaintexts()
            .getRows();
    int traceLengthCandidate = (int) (contentLength / traces);
    if ((long) traceLengthCandidate * traces == contentLength) {
      traceImportConfiguration.setTraceLength(traceLengthCandidate);
      traceLengthField.setText(String.valueOf(traceLengthCandidate));
    }
  }
}
