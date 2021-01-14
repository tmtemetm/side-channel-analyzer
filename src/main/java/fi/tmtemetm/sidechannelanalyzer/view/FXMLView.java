package fi.tmtemetm.sidechannelanalyzer.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author tmtemetm
 */
public abstract class FXMLView<R extends Node> implements FXView<R> {
  protected final FXMLLoader loader;

  public FXMLView(Resource resource) throws IOException {
    loader = new FXMLLoader(resource.getURL());
  }

  public FXMLView(Resource resource, Object controller) throws IOException {
    this(resource, aClass -> controller);
  }

  public FXMLView(Resource resource, Callback<Class<?>, Object> controllerFactory) throws IOException {
    loader = new FXMLLoader(resource.getURL());
    loader.setControllerFactory(controllerFactory);
  }

  @Override
  public R createRootNode() throws IOException {
    return loader.load();
  }
}
