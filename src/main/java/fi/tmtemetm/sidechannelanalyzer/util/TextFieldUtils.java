package fi.tmtemetm.sidechannelanalyzer.util;

import javafx.scene.control.TextField;

/**
 * @author tmtemetm
 */
public abstract class TextFieldUtils {

  public static void addIntegerValidator(TextField textField) {
    textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
      if (!newValue.matches("\\d*")) {
        textField.setText(oldValue);
      }
    });
  }

  public static int readIntegerTextField(TextField textField, int defaultValue) {
    String text = textField.getText();
    if (text == null || text.isEmpty()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }


}
