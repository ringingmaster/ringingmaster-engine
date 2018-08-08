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
public abstract class CommonCompilerPipelineData<T extends CommonCompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Parse parse;
    private final String logPreamble;
    private final Optional<ProofTerminationReason> terminationReason;
    private final Optional<String> terminateNotes;

    protected CommonCompilerPipelineData(Parse parse, String logPreamble,
                                            Optional<ProofTerminationReason> terminationReason,
                                            Optional<String> terminateNotes) {
        this.parse = parse;
        this.logPreamble = logPreamble;
        this.terminationReason = terminationReason;
        this.terminateNotes = terminateNotes;
    }

    public Parse getParse() {
        return parse;
    }

    public String getLogPreamble() {
        return logPreamble;
    }

    public abstract T terminate(final ProofTerminationReason terminationReason, String terminateNotes) ;

    public Optional<ProofTerminationReason> getTerminationReason() {
        return terminationReason;
    }

    public Optional<String> getTerminateNotes() {
        return terminateNotes;
    }

    public boolean isTerminated() {
        return terminationReason.isPresent();
    }

}