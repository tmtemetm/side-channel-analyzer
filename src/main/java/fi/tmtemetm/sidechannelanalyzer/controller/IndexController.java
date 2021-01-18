package fi.tmtemetm.sidechannelanalyzer.controller;

import fi.tmtemetm.sidechannelanalyzer.view.impl.DPASettingsView;
import fi.tmtemetm.sidechannelanalyzer.view.impl.ImportTracesView;
import fi.tmtemetm.sidechannelanalyzer.view.impl.TracesView;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author tmtemetm
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

  private final ImportTracesView importTracesView;
  private final TracesView tracesView;
  private final DPASettingsView dpaSettingsView;

  private final ImportTracesController importTracesController;
  private final TracesController tracesController;
  private final DPASettingsController dpaSettingsController;

  @FXML
  private TabPane tabPane;

  @FXML
  public void initialize() throws IOException {
    Tab importTab = new Tab("Import", importTracesView.createRootNode());
    Tab tracesTab = new Tab("Traces", tracesView.createRootNode());
    Tab dpaTab = new Tab("DPA", dpaSettingsView.createRootNode());

    tracesTab.disableProperty().bind(importTracesController.allFieldsSetProperty().not());
    dpaTab.disableProperty().bind(importTracesController.allFieldsSetProperty().not());

    tabPane.getTabs()
            .addAll(importTab, tracesTab, dpaTab);
    tabPane.getTabs().forEach(tab -> tab.setClosable(false));

    tracesController.setOnRunDPAOnCurrentSelection(() -> tabPane.getSelectionModel()
            .select(2));
    dpaSettingsController.setOnDPAStart(dpaResults -> {
      LocalDateTime now = LocalDateTime.now();
      addTab(new Tab("DPA " + now.format(DateTimeFormatter.ofPattern("hh:mm:ss")), dpaResults));
    });
  }

  public void addTab(Tab tab) {
    tabPane.getTabs()
            .add(tab);
    tabPane.getSelectionModel()
            .select(tab);
  }

}
