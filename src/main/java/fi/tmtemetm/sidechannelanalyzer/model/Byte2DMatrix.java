package fi.tmtemetm.sidechannelanalyzer.model;

import lombok.ToString;
import lombok.Value;

/**
 * @author tmtemetm
 */
@Value
@ToString(onlyExplicitlyIncluded = true)
public class Byte2DMatrix {
  byte[][] data;
  int rows, columns;
}
