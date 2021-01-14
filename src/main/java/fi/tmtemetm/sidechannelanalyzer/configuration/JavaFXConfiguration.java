package fi.tmtemetm.sidechannelanalyzer.configuration;

import fi.tmtemetm.sidechannelanalyzer.event.StageReadyEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

  @EventListener
  public void stageReadyEventListener(StageReadyEvent stageReadyEvent) throws Exception {
    Stage stage = stageReadyEvent.getStage();
    stage.setTitle(properties.stage.title);

    Scene scene = new Scene(new Label(properties.stage.title));
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
