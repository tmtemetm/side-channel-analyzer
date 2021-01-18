package fi.tmtemetm.sidechannelanalyzer.compressor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

/**
 * @author tmtemetm
 */
@FunctionalInterface
public interface SignalCompressor {

  CompressionResult compress(double[] inputSignal);

  SignalCompressor NO_COMPRESSION = inputSignal -> new CompressionResult(inputSignal,
          IntStream.range(0, inputSignal.length)
                  .toArray());

  @Getter
  @RequiredArgsConstructor
  class CompressionResult {
    protected final double[] signal;
    protected final int[] indices;
  }

}
