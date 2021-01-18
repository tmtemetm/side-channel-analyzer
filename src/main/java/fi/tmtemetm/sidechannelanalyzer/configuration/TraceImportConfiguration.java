package fi.tmtemetm.sidechannelanalyzer.configuration;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author tmtemetm
 */
@Data
@Component
public class TraceImportConfiguration {

  @Nullable
  private Resource tracesFile;
  @Nullable
  private Byte2DMatrix plaintexts;

  public void setPlaintexts(Byte2DMatrix plaintexts) {
    this.plaintexts = plaintexts;
    setNumberOfTraces(plaintexts.getRows());
  }

  private IntegerProperty traceLength = new SimpleIntegerProperty(this, "traceLength", 0);
  private IntegerProperty numberOfTraces = new SimpleIntegerProperty(this, "numberOfTraces", 0);

  public int getTraceLength() {
    return traceLength.get();
  }

  public IntegerProperty traceLengthProperty() {
    return traceLength;
  }

  public void setTraceLength(int traceLength) {
    this.traceLength.set(traceLength);
  }

  public int getNumberOfTraces() {
    return numberOfTraces.get();
  }

  public IntegerProperty numberOfTracesProperty() {
    return numberOfTraces;
  }

  public void setNumberOfTraces(int numberOfTraces) {
    this.numberOfTraces.set(numberOfTraces);
  }

}
