package org.ringingmaster.engine.composition;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ringingmaster.engine.composition.DryRun.DryRunResult.SUGGESTED_ALTERATIVE;

/**
 * Helper to the MutableComposition for complex operations that need details of result of a dry run of setting a value
 *
 * @author Steve Lake
 */


public class DryRun {

    public enum DryRunResult {
        FAIL,
        SUCCESS,
        //We are settig the same value
        NO_CHANGE,
        SUGGESTED_ALTERATIVE
    }

    private final List<String> messages;
    private final DryRunResult result;
    private final Object suggestedAlternative;

    public DryRun(Object suggestedAlternative) {
        this.messages = Collections.emptyList();
        this.result = SUGGESTED_ALTERATIVE;
        this.suggestedAlternative = checkNotNull(suggestedAlternative);
    }

    public DryRun(DryRunResult result) {
        this.messages = Collections.emptyList();
        this.result = checkNotNull(result);
        this.suggestedAlternative = null;
    }

    public DryRun(DryRunResult result, List<String> messages) {
        this.messages = checkNotNull(messages);
        this.result = checkNotNull(result);
        this.suggestedAlternative = null;
    }

    public List<String> getMessages() {
        return messages;
    }

    public DryRunResult result() {
        return result;
    }

    public Object getSuggestedAlternative() {
        return suggestedAlternative;
    }

    @Override
    public String toString() {
        return "DryRun{" +
                "messages=" + messages +
                ", result=" + result +
                ", suggestedAlternative=" + suggestedAlternative +
                '}';
    }
}
