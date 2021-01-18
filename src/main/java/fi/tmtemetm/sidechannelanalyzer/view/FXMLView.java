package fi.tmtemetm.sidechannelanalyzer.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * @author tmtemetm
 */
@RequiredArgsConstructor
public abstract class FXMLView<R extends Node> implements FXSingletonView<R> {
  protected final Resource resource;
  @Nullable
  protected final Callback<Class<?>, Object> controllerFactory;

  public FXMLView(Resource resource) {
    this(resource, null);
  }

  public FXMLView(Resource resource, Object controller) {
    this(resource, aClass -> controller);
  }

  @Override
  public R createRootNode() throws IOException {
    FXMLLoader loader = new FXMLLoader(resource.getURL());
    if (controllerFactory != null) {
      loader.setControllerFactory(controllerFactory);
    }
    return loader.load();
  }

}
