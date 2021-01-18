package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.ImportTracesController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Component
public class ImportTracesView extends FXMLView<VBox> {

  public ImportTracesView(@Value("classpath:/view/import-traces.fxml") Resource resource,
                          ImportTracesController controller) {
    super(resource, controller);
  }

}
