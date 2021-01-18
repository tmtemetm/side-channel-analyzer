package fi.tmtemetm.sidechannelanalyzer.configuration;

import fi.tmtemetm.sidechannelanalyzer.event.StageReadyEvent;
import fi.tmtemetm.sidechannelanalyzer.view.impl.IndexView;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Configuration
@RequiredArgsConstructor
public class JavaFXConfiguration {

  private final FXProperties properties;
  private final IndexView indexView;
  private final StageProvider stageProvider;

  @EventListener
  public void stageReadyEventListener(StageReadyEvent stageReadyEvent) throws Exception {
    Stage stage = stageReadyEvent.getStage();
    stage.setTitle(properties.stage.title);
    stageProvider.stage = stage;

    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    stage.setX(primaryScreenBounds.getMinX());
    stage.setY(primaryScreenBounds.getMinY());
    stage.setWidth(primaryScreenBounds.getWidth());
    stage.setHeight(primaryScreenBounds.getHeight());

    Scene scene = new Scene(indexView.createRootNode());
    stage.setScene(scene);

    stage.show();
  }


  @Data
  @Component
  @ConfigurationProperties("fx")
  public static class FXProperties {
    private StageProperties stage = new StageProperties();

    @Data
    public static class StageProperties {
      private String title = "Side Channel Analyzer";
    }
  }

  @Getter
  @Component
  public static class StageProvider {
    @SuppressWarnings("NotNullFieldNotInitialized") // Injected after the stage is ready
    private Stage stage;
  }

}
