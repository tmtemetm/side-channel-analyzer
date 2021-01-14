package fi.tmtemetm.sidechannelanalyzer;

import fi.tmtemetm.sidechannelanalyzer.event.StageReadyEvent;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/**
 * @author tmtemetm
 */
public class SideChannelAnalyzerFxApplication extends Application {

  @Nullable
  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() {
    String[] args = getParameters().getRaw()
            .toArray(new String[0]);
    applicationContext = SpringApplication.run(SideChannelAnalyzerApplication.class, args);
  }

  @Override
  public void start(Stage stage) {
    if (applicationContext == null) {
      throw new IllegalStateException("ApplicationContext was null when starting JavaFX application");
    }
    applicationContext.publishEvent(new StageReadyEvent(stage));
  }

}
