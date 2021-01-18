package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.DPAResultsController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLPrototypeView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Component
public class DPAResultsView extends FXMLPrototypeView<VBox, DPAResultsController> {
  public DPAResultsView(@Value("classpath:/view/dpa-results.fxml") Resource resource) {
    super(resource);
  }
}
