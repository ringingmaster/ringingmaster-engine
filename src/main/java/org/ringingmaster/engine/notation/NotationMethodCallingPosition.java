package org.ringingmaster.engine.notation;

import net.jcip.annotations.Immutable;

/**
 * Representation of the calling positions.
 * i.e. W, B, M, H or 1, 2, 3, 4 Etc
 *
 * User: Stephen
 */
@Immutable
public interface NotationMethodCallingPosition extends Comparable<NotationMethodCallingPosition> {

	int getCallInitiationRow();
	int getLeadOfTenor();
	String getName();
}
