package fi.tmtemetm.sidechannelanalyzer.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author tmtemetm
 */
@RequiredArgsConstructor
public abstract class FXMLPrototypeView<R extends Node, C> implements FXPrototypeView<R, C> {
  protected final Resource resource;

  @Override
  public R createRootNode(C controller) throws IOException {
    FXMLLoader loader = new FXMLLoader(resource.getURL());
    loader.setControllerFactory(aClass -> controller);
    return loader.load();
  }

}
