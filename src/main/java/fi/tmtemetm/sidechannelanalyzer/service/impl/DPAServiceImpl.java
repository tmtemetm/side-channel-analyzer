package fi.tmtemetm.sidechannelanalyzer.service.impl;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.DPAResult;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import fi.tmtemetm.sidechannelanalyzer.powermodel.PowerModel;
import fi.tmtemetm.sidechannelanalyzer.service.DPAService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author tmtemetm
 */
@Service
@RequiredArgsConstructor
public class DPAServiceImpl implements DPAService {
  protected static final int NOF_BYTES = 16;

  protected final AsyncDPAService asyncDPAService;

  @Override
  public List<CompletableFuture<DPAResult>> analyze(TracesContainer tracesContainer, Byte2DMatrix plainTexts,
                                                    PowerModel powerModel) {
    int nofTraces = tracesContainer.getTraces()
            .getRows();
    byte[][] originalPlaintextBytes = plainTexts.getData();
    byte[][] filteredPlaintextBytes = new byte[nofTraces][plainTexts.getColumns()];
    for (int i = 0; i < nofTraces; i++) {
      filteredPlaintextBytes[i] = originalPlaintextBytes[tracesContainer.getIndex(i)];
    }
    Byte2DMatrix filteredPlainTexts = new Byte2DMatrix(filteredPlaintextBytes, nofTraces, plainTexts.getColumns());
    return IntStream.range(0, NOF_BYTES)
            .mapToObj(byteIndex -> asyncDPAService.analyze(tracesContainer, filteredPlainTexts, powerModel, byteIndex))
            .collect(Collectors.toList());
  }

}
