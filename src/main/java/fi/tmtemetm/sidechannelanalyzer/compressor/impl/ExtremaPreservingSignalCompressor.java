package fi.tmtemetm.sidechannelanalyzer.compressor.impl;

import fi.tmtemetm.sidechannelanalyzer.compressor.SignalCompressor;
import fi.tmtemetm.sidechannelanalyzer.util.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @author tmtemetm
 */
@RequiredArgsConstructor
public class ExtremaPreservingSignalCompressor implements SignalCompressor {

  @Nullable
  protected final Integer maxLength;
  @Nullable
  protected final Integer windowLength;

  @Override
  public CompressionResult compress(double[] inputSignal) {
    int binSize = getBinSize(inputSignal);
    int length = MathUtils.ceilingDivision(inputSignal.length, binSize);

    double[] signal = new double[length];
    int[] indices = new int[length];

    double previousValue = 0;
    for (int bin = 0; bin < length; bin++) {
      int representativeIndex = findIndexWithMaximumDifference(inputSignal, previousValue, binSize, bin);
      indices[bin] = representativeIndex;
      double representativeValue = inputSignal[representativeIndex];
      signal[bin] = representativeValue;
      previousValue = representativeValue;
    }

    return new CompressionResult(signal, indices);
  }

  protected int findIndexWithMaximumDifference(double[] inputSignal, double previousValue, int binSize, int bin) {
    int baseIndex = bin * binSize;
    int remainingBinSize = Math.min(binSize, inputSignal.length - baseIndex);
    int representativeIndex = baseIndex;
    double maximumDifference = 0;
    for (int i = 0; i < remainingBinSize; i++) {
      int index = baseIndex + i;
      double difference = Math.abs(inputSignal[index] - previousValue);
      if (difference > maximumDifference) {
        representativeIndex = index;
        maximumDifference = difference;
      }
    }
    return representativeIndex;
  }

  protected int getBinSize(double[] inputSignal) {
    if (maxLength != null) {
      if (inputSignal.length > maxLength) {
        return MathUtils.ceilingDivision(inputSignal.length, maxLength);
      }
      return 1;
    }
    if (windowLength == null) {
      throw new IllegalStateException("Max length and window length can't both be null");
    }
    return windowLength;
  }


  public static ExtremaPreservingSignalCompressor ofMaxLength(int maxLength) {
    return new ExtremaPreservingSignalCompressor(maxLength, null);
  }

  public static ExtremaPreservingSignalCompressor ofWindowLength(int windowLength) {
    return new ExtremaPreservingSignalCompressor(null, windowLength);
  }

}
