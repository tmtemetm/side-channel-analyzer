package fi.tmtemetm.sidechannelanalyzer.configuration;

import fi.tmtemetm.sidechannelanalyzer.event.StageReadyEvent;
import fi.tmtemetm.sidechannelanalyzer.view.impl.IndexView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Data;
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

  @EventListener
  public void stageReadyEventListener(StageReadyEvent stageReadyEvent) throws Exception {
    Stage stage = stageReadyEvent.getStage();
    stage.setTitle(properties.stage.title);

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

}
