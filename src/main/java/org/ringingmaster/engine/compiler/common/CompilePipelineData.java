package org.ringingmaster.engine.compiler.common;

import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
@Immutable
public abstract class CompilePipelineData<T extends CompilePipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Parse parse;
    private final String logPreamble;
    private final Optional<Method> method;
    private final Optional<CompileTerminationReason> terminationReason;
    private final Optional<String> terminateNotes;

    protected CompilePipelineData(Parse parse, String logPreamble,
                                  Optional<Method> method,
                                  Optional<CompileTerminationReason> terminationReason,
                                  Optional<String> terminateNotes) {
        this.parse = checkNotNull(parse);
        this.logPreamble = checkNotNull(logPreamble);
        this.method = checkNotNull(method);
        this.terminationReason = checkNotNull(terminationReason);
        this.terminateNotes = checkNotNull(terminateNotes);
    }

    public Parse getParse() {
        return parse;
    }

    public String getLogPreamble() {
        return logPreamble;
    }

    public T setMethod(Optional<Method> method) {
        return buildWhenBaseChanges(parse, logPreamble,
                method,
                terminationReason, terminateNotes);
    }

    public Optional<Method> getMethod() {
        return method;
    }

    public T terminate(final CompileTerminationReason terminationReason, String terminateNotes) {
        if (!isTerminated()) {
            return buildWhenBaseChanges(parse, logPreamble,
                    method,
                    Optional.of(terminationReason), Optional.of(terminateNotes));
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
            return (T)this;
        }
    }

    public Optional<CompileTerminationReason> getTerminationReason() {
        return terminationReason;
    }

    public Optional<String> getTerminateNotes() {
        return terminateNotes;
    }

    public boolean isTerminated() {
        return terminationReason.isPresent();
    }


    protected abstract T buildWhenBaseChanges(Parse parse, String logPreamble,
                                              Optional<Method> method,
                                              Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes);



}