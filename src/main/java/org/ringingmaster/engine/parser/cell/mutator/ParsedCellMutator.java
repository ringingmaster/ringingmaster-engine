package org.ringingmaster.engine.parser.cell.mutator;

import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellFactory;

//NOTE There was much more functionality in here that was removed since using regex to parse and group multipliers.
// This could be resurrected from GIT.
public class ParsedCellMutator {

    private ParsedCell prototype;

    private final ParsedCellMutatorGetSectionsAndGroups getSectionsAndGroups = new ParsedCellMutatorGetSectionsAndGroups();
    private final ParsedCellMutatorGroups invalidateGroups = new ParsedCellMutatorGroups(true);
    private final ParsedCellMutatorGroups setMessageForGroups = new ParsedCellMutatorGroups(false);

    public ParsedCell build() {

        ParsedCellMutatorSectionsAndGroups sectionsAndGroups =
                getSectionsAndGroups
                .andThen(invalidateGroups)
                .andThen(setMessageForGroups)
                .apply(prototype);

        // rebuild parsed cell
        return ParsedCellFactory.buildParsedCellFromGroups(prototype.getParentCell(), sectionsAndGroups.getGroups());
    }

    public ParsedCellMutator prototypeOf(ParsedCell prototype) {
        this.prototype = prototype;
        return this;
    }

    public ParsedCellMutator invalidateGroup(int sourceGroupElementIndex, String message) {
        invalidateGroups.invalidateGroup(sourceGroupElementIndex, message);
        return this;
    }

    public ParsedCellMutator setGroupMessage(int sourceGroupElementIndex, String message) {
        setMessageForGroups.invalidateGroup(sourceGroupElementIndex, message);
        return this;
    }

}
