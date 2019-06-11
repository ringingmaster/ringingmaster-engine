package org.ringingmaster.engine.parser.brace;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.assignparsetype.ParseType;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.mutator.ParsedCellMutator;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public abstract class SkeletalMatchingBraceLogic implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ParseType openingBrace;
    private final ParseType closingBrace;
    private final String braceTypeName;
    private final int nestingDepth;

    public SkeletalMatchingBraceLogic(ParseType openingBrace, ParseType closingBrace, String braceTypeName, int nestingDepth) {
        this.openingBrace = checkNotNull(openingBrace);
        this.closingBrace = checkNotNull(closingBrace);
        this.braceTypeName = checkNotNull(braceTypeName);
        this.nestingDepth = nestingDepth;
        checkArgument(nestingDepth > 0);
    }

    public Parse apply(Parse input) {
        log.debug("[{}] > validate " + braceTypeName + " brace logic", input.getComposition().getLoggingTag());

        HashBasedTable<Integer, Integer, ParsedCell> resultCells =
                HashBasedTable.create(input.allCompositionCells().getBackingTable());

        parseCells(input.mainBodyCells(), resultCells, input.getComposition().getLoggingTag());
        parseCells(input.splicedCells(), resultCells, input.getComposition().getLoggingTag());

        // We parse definitions individually. This is so that any open/close brace in a definition
        // must be complete sets within the definition. i.e a matched open and close brace.
        HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(input.definitionDefinitionCells().getBackingTable());
        final ImmutableArrayTable<ParsedCell> definitionDefinitionCells = input.definitionDefinitionCells();
        for(int rowIndex = 0; rowIndex < definitionDefinitionCells.getRowSize();rowIndex++) {
            final ImmutableArrayTable<ParsedCell> cell = definitionDefinitionCells.subTable(rowIndex, rowIndex + 1, 0, 1);
            parseCells(cell,  definitionTableResult, input.getComposition().getLoggingTag());
        }

        Parse result = new ParseBuilder()
                .prototypeOf(input)
                .setCompositionTableCells(resultCells)
                .setDefinitionTableCells(definitionTableResult)
                .build();

        log.debug("[{}] < validate " + braceTypeName + " brace logic", input.getComposition().getLoggingTag());

        return result;

    }

    private void parseCells(ImmutableArrayTable<ParsedCell> originalCells, HashBasedTable<Integer, Integer, ParsedCell> resultCells, String logPreamble) {

        Map<CoordinateAndSection, String> invalidSections = Maps.newHashMap();

        Deque<CoordinateAndSection> openBraces = new ArrayDeque<>();

        for (BackingTableLocationAndValue<ParsedCell> locationAndCell : originalCells) {
            for (Section section : locationAndCell.getValue().allSections()) {
                if (openingBrace.equals(section.getParseType())) {
                    if (openBraces.size() >= nestingDepth) {
                        invalidSections.put(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section), "Nesting depth greater than the " + nestingDepth + " allowed for " + braceTypeName );
                    }
                    else {
                        openBraces.addFirst(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section));
                    }
                }
                else if (closingBrace.equals(section.getParseType())) {
                    if (openBraces.size() > 0) {
                        openBraces.removeFirst();
                    }
                    else {
                        invalidSections.put(new CoordinateAndSection(locationAndCell.getRow(), locationAndCell.getCol(), section), "No matching opening " + braceTypeName + " brace");
                    }
                }
            }
        }
        while (openBraces.peekFirst() != null ) {
            invalidSections.put(openBraces.removeFirst(), "No matching closing " + braceTypeName + " brace");
        }
        if (!invalidSections.isEmpty()) {
            log.debug("[{}]  marking invalid sections [{}]", logPreamble, invalidSections);
        }

        for (Map.Entry<CoordinateAndSection, String> invalidSection : invalidSections.entrySet()) {

            Section section = invalidSection.getKey().section;

            ParsedCell originalParsedCell = resultCells.get(invalidSection.getKey().row,invalidSection.getKey().col);
            ParsedCellMutator parsedCellMutator = new ParsedCellMutator()
                    .prototypeOf(originalParsedCell)
                    .invalidateGroup(section.getStartIndex(), invalidSection.getValue());

            resultCells.put(invalidSection.getKey().row,
                    invalidSection.getKey().col,
                    parsedCellMutator.build());
        }

    }

    class CoordinateAndSection {
        private final int row;
        private final int col;
        final Section section;

        CoordinateAndSection(int row, int col, Section section) {
            this.row = row;
            this.col = col;
            this.section = checkNotNull(section);
        }

        @Override
        public String toString() {
            return "CoordinateAndSection{" +
                    "location=[" + row + "," + col +
                    "], section=" + section +
                    '}';
        }
    }
}
