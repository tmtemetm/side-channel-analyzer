package fi.tmtemetm.sidechannelanalyzer.view.impl;

import fi.tmtemetm.sidechannelanalyzer.controller.TracesPlotController;
import fi.tmtemetm.sidechannelanalyzer.view.FXMLPrototypeView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Component
public class TracesPlotView extends FXMLPrototypeView<VBox, TracesPlotController> {

  public TracesPlotView(@Value("classpath:/view/traces-plot.fxml") Resource resource) {
    super(resource);
  }

}
