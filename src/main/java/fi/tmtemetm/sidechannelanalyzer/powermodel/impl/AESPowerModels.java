package fi.tmtemetm.sidechannelanalyzer.powermodel.impl;

import fi.tmtemetm.sidechannelanalyzer.model.Double2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.powermodel.PowerModel;
import fi.tmtemetm.sidechannelanalyzer.util.AESUtils;
import fi.tmtemetm.sidechannelanalyzer.util.HammingWeightUtils;

/**
 * @author tmtemetm
 */
public abstract class AESPowerModels {
  private static final int NOF_KEYS = 256;

  public static final PowerModel FIRST_SUB_BYTES_HAMMING_WEIGHT = (plainTexts, byteIndex) -> {
    byte[][] plainTextBytes = plainTexts.getData();
    int numberOfPlaintexts = plainTexts.getRows();
    double[][] result = new double[plainTextBytes.length][NOF_KEYS];

    for (int plainText = 0; plainText < numberOfPlaintexts; plainText++) {
      for (int key = 0; key < NOF_KEYS; key++) {
        result[plainText][key] = HammingWeightUtils.hammingWeight(AESUtils.substituteWithSBox(
                Byte.toUnsignedInt(plainTextBytes[plainText][byteIndex]) ^ key));
      }
    }

    return new Double2DMatrix(result, numberOfPlaintexts, NOF_KEYS);
  };

}
