package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.IndexController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLView;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Component
public class IndexView extends FXMLView<TabPane> {

  public IndexView(@Value("classpath:/view/index.fxml") Resource resource,
                   IndexController controller) {
    super(resource, controller);
  }

}
