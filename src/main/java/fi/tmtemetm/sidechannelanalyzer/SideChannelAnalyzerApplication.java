package fi.tmtemetm.sidechannelanalyzer;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author tmtemetm
 */
@EnableAsync
@SpringBootApplication
public class SideChannelAnalyzerApplication {

	public static void main(String[] args) {
		Application.launch(SideChannelAnalyzerFxApplication.class, args);
	}

}
