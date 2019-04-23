package org.ringingmaster.engine.notation;

import javax.annotation.concurrent.Immutable;

/**
 * Contains the verified notation elements for the different types of
 * Call.
 * <p>
 * User: Stephen
 */
@Immutable
public interface Call extends PlaceSetSequence {

    /**
     * Get the shorthand name of the call. i.e. '-' or 'S'
     */
    String getNameShorthand();

    /*
     *  Get a representation that is suitable for user display.
     */
    String toDisplayString(); //TODO should this go down to the Notation, and all sub classes.
}
