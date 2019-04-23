package org.ringingmaster.engine.notation;

import javax.annotation.concurrent.Immutable;

/**
 * Representation of the calling positions.
 * i.e. W, B, M, H or 1, 2, 3, 4 Etc
 * <p>
 * User: Stephen
 */
@Immutable
public interface CallingPosition extends Comparable<CallingPosition> {

    int getCallInitiationRow();

    int getLeadOfTenor();

    String getName();
}
