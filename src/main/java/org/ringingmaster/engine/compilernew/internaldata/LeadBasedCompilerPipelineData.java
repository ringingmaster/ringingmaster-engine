package org.ringingmaster.engine.compilernew.internaldata;

import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public class LeadBasedCompilerPipelineData  extends CommonCompilerPipelineData<LeadBasedCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public LeadBasedCompilerPipelineData(Parse parse) {
        this(parse, "",
                Optional.empty(), Optional.empty());
    }


    private LeadBasedCompilerPipelineData(Parse parse, String logPreamble,
                                            Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes) {
        super(parse, logPreamble, terminationReason, terminateNotes);
    }

    @Override
    public LeadBasedCompilerPipelineData terminate(ProofTerminationReason terminationReason, String terminateNotes) {
        if (!isTerminated()) {
            log.debug("{}  - Terminate [{}] {}", getLogPreamble(), terminateNotes, terminationReason);
            return new LeadBasedCompilerPipelineData(getParse(), getLogPreamble(),
                    Optional.of(terminationReason), Optional.of(terminateNotes));
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
            return this;
        }
    }
}
