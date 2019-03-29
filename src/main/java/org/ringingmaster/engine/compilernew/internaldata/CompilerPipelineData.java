package org.ringingmaster.engine.compilernew.internaldata;

import org.ringingmaster.engine.compilernew.proof.ProofTerminationReason;
import org.ringingmaster.engine.method.Method;
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
public abstract class CompilerPipelineData<T extends CompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Parse parse;
    private final String logPreamble;
    private final Optional<Method> createdMethod;
    private final Optional<ProofTerminationReason> terminationReason;
    private final Optional<String> terminateNotes;

    protected CompilerPipelineData(Parse parse, String logPreamble,
                                   Optional<Method> createdMethod,
                                   Optional<ProofTerminationReason> terminationReason,
                                   Optional<String> terminateNotes) {
        this.parse = parse;
        this.logPreamble = logPreamble;
        this.createdMethod = createdMethod;
        this.terminationReason = terminationReason;
        this.terminateNotes = terminateNotes;
    }

    public Parse getParse() {
        return parse;
    }

    public String getLogPreamble() {
        return logPreamble;
    }

    public T setCreatedMethod(Optional<Method> method) {
        return build(parse, logPreamble,
                method,
                terminationReason, terminateNotes);
    }

    public Optional<Method> getCreatedMethod() {
        return createdMethod;
    }

    public T terminate(final ProofTerminationReason terminationReason, String terminateNotes) {
        if (!isTerminated()) {
            return build(parse, logPreamble,
                    createdMethod,
                    Optional.of(terminationReason), Optional.of(terminateNotes));
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
            return (T)this;
        }
    }

    public Optional<ProofTerminationReason> getTerminationReason() {
        return terminationReason;
    }

    public Optional<String> getTerminateNotes() {
        return terminateNotes;
    }

    public boolean isTerminated() {
        return terminationReason.isPresent();
    }


    protected abstract T build(Parse parse, String logPreamble,
                               Optional<Method> method,
                               Optional<ProofTerminationReason> terminationReason, Optional<String> terminateNotes);



}