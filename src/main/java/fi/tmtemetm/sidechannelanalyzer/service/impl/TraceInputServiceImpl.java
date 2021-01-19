package fi.tmtemetm.sidechannelanalyzer.service.impl;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.Double2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import fi.tmtemetm.sidechannelanalyzer.service.TraceInputService;
import fi.tmtemetm.sidechannelanalyzer.util.MathUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author tmtemetm
 */
@Service
@Slf4j
public class TraceInputServiceImpl implements TraceInputService {
  private static final int PLAINTEXT_LENGTH = 16;

  @Override
  public int readTotalNumberOfTraces(Resource resource, int traceLength) throws IOException {
    long contentLength = resource.contentLength();
    return (int) MathUtils.ceilingDivision(contentLength, traceLength);
  }

  @Override
  public TracesContainer readTraces(Resource resource, int traceLength, int start, int end, BitSet includeTraces) throws IOException {
    if (traceLength <= 0) {
      throw new IllegalArgumentException("Trace length should be positive");
    }
    if (start < 0 || end < 0) {
      throw new IllegalArgumentException("Start and end indices should be non-negative");
    }
    if (start >= traceLength) {
      throw new IllegalArgumentException("Start index should not exceed trace length");
    }
    if (end <= start) {
      throw new IllegalArgumentException("End index should be higher than the start index");
    }
    int totalTraces = readTotalNumberOfTraces(resource, traceLength);
    int intervalLength = end - start;
    int readLength = Math.min(intervalLength, traceLength - start);
    int endSkipLength = Math.max(traceLength - end, 0);

    @Cleanup InputStream inputStream = resource.getInputStream();
    List<double[]> traces = new ArrayList<>();
    List<Integer> indices = new ArrayList<>();

    int previousIndex = -1;
    for (int i = includeTraces.nextSetBit(0); i >= 0; i = includeTraces.nextSetBit(i + 1)) {
      if (i >= totalTraces) {
        log.warn("Requested a trace index above the total trace count. Ignoring remaining indices.");
        break;
      }
      double[] trace = new double[intervalLength];
      traces.add(trace);
      indices.add(i);
      try {
        inputStream.readNBytes((previousIndex < 0 ? 0 : endSkipLength)
                + (i - previousIndex - 1) * traceLength + start);
        for (int j = 0; j < readLength; j++) {
          int read = inputStream.read();
          if (read < 0) {
            throw new EOFException();
          }
          trace[j] = read;
        }
      } catch (EOFException e) {
        log.warn("Reached end of file while reading traces from resource {}", resource);
        break;
      }
      previousIndex = i;
    }

    return new TracesContainer(new Double2DMatrix(traces.toArray(double[][]::new), traces.size(), intervalLength), IntStream.range(start, end).toArray(), indices, start, end);
  }


  @Override
  public Byte2DMatrix readPlainTexts(Resource resource) throws IOException {
    byte[][] bytes = Files.lines(resource.getFile().toPath())
            .flatMap(line -> {
              String[] hexArray = StringUtils.delimitedListToStringArray(StringUtils.trimWhitespace(line), " ");
              if (hexArray.length == 0) {
                return Stream.empty();
              }
              if (hexArray.length != PLAINTEXT_LENGTH) {
                throw new IllegalArgumentException("File " + resource.getFilename() + " has the wrong format. Line had "
                        + hexArray.length + " bytes out of the expected " + PLAINTEXT_LENGTH);
              }
              byte[] byteArray = new byte[hexArray.length];
              for (int i = 0; i < hexArray.length; i++) {
                byteArray[i] = (byte) Integer.parseInt(hexArray[i], 16);
              }
              return Stream.of(byteArray);
            })
            .toArray(byte[][]::new);

    return new Byte2DMatrix(bytes, bytes.length, PLAINTEXT_LENGTH);
  }
}
