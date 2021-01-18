package fi.tmtemetm.sidechannelanalyzer.service;

import fi.tmtemetm.sidechannelanalyzer.model.Byte2DMatrix;
import fi.tmtemetm.sidechannelanalyzer.model.DPAResult;
import fi.tmtemetm.sidechannelanalyzer.model.TracesContainer;
import fi.tmtemetm.sidechannelanalyzer.powermodel.PowerModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author tmtemetm
 */
public interface DPAService {

  List<CompletableFuture<DPAResult>> analyze(TracesContainer tracesContainer, Byte2DMatrix plainTexts,
                                             PowerModel powerModel);

}
