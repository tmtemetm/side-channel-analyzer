package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.IndexController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author tmtemetm
 */
@Component
public class IndexView extends FXMLView<VBox> {

  public IndexView(@Value("classpath:/view/index.fxml") Resource resource,
                   IndexController controller) throws IOException {
    super(resource, controller);
  }

}
