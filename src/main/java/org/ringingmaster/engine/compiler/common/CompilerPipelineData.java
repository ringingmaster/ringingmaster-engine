package org.ringingmaster.engine.compiler.common;

import com.google.common.collect.ImmutableMap;
import org.ringingmaster.engine.compiler.CompileTerminationReason;
import org.ringingmaster.engine.compiler.variance.Variance;
import org.ringingmaster.engine.method.Method;
import org.ringingmaster.engine.notation.Call;
import org.ringingmaster.engine.parser.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
@Immutable
public abstract class CompilerPipelineData<T extends CompilerPipelineData> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // inputs
    private final Parse parse;
    private final String logPreamble;

    // internal
    private final ImmutableMap<String, Call> callLookupByName;
    private final ImmutableMap<String, Variance> varianceLookupByName;

    // outputs
    private final Optional<Method> method;
    private final Optional<CompileTerminationReason> terminationReason;
    private final Optional<String> terminateNotes;

    protected CompilerPipelineData(Parse parse, String logPreamble,
                                   ImmutableMap<String, Call> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                   Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes) {
        this.parse = checkNotNull(parse);
        this.logPreamble = checkNotNull(logPreamble);

        this.callLookupByName = checkNotNull(callLookupByName);
        this.varianceLookupByName = checkNotNull(varianceLookupByName);

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


    public ImmutableMap<String, Call> getCallLookupByName() {
        return callLookupByName;
    }

    public T setLookupByName(ImmutableMap<String, Call> callLookupByName) {
        return buildWhenBaseChanges(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes);
    }

    public ImmutableMap<String, Variance> getVarianceLookupByName() {
        return varianceLookupByName;
    }

    public T setVarianceLookupByName(ImmutableMap<String, Variance> varianceLookupByName) {
        return buildWhenBaseChanges(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes);    }


    public Optional<Method> getMethod() {
        return method;
    }

    public T setMethod(Optional<Method> method) {
        return buildWhenBaseChanges(parse, logPreamble,
                callLookupByName, varianceLookupByName,
                method, terminationReason, terminateNotes);
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

    public T terminate(final CompileTerminationReason terminationReason, String terminateNotes) {
        if (!isTerminated()) {
            return buildWhenBaseChanges(parse, logPreamble,
                    callLookupByName, varianceLookupByName,
                    method, Optional.of(terminationReason), Optional.of(terminateNotes));
        }
        else  {
            log.warn("Requesting second terminate [{}]{}", terminationReason, terminateNotes);
            return (T)this;
        }
    }


    protected abstract T buildWhenBaseChanges(Parse parse, String logPreamble,
                                              ImmutableMap<String, Call> callLookupByName, ImmutableMap<String, Variance> varianceLookupByName,
                                              Optional<Method> method, Optional<CompileTerminationReason> terminationReason, Optional<String> terminateNotes);



}