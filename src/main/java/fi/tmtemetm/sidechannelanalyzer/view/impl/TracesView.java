package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.TracesController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLView;
import javafx.scene.control.SplitPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Component
public class TracesView extends FXMLView<SplitPane> {

  public TracesView(@Value("classpath:/view/traces.fxml") Resource resource,
                    TracesController controller) {
    super(resource, controller);
  }

}
