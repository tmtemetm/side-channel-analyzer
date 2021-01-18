package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.DPASettingsController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Component
public class DPASettingsView extends FXMLView<VBox> {

  public DPASettingsView(@Value("classpath:/view/dpa-settings.fxml") Resource resource,
                         DPASettingsController controller) {
    super(resource, controller);
  }

}
