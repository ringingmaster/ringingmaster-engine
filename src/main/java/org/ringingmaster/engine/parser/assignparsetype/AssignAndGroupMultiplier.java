package org.ringingmaster.engine.parser.assignparsetype;

import com.google.common.collect.HashBasedTable;
import org.ringingmaster.engine.arraytable.BackingTableLocationAndValue;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.parser.cell.ParsedCellMutator;
import org.ringingmaster.engine.parser.functions.InUseSpliceDefinitionsTransitively;
import org.ringingmaster.engine.parser.parse.Parse;
import org.ringingmaster.engine.parser.parse.ParseBuilder;
import org.ringingmaster.engine.touch.Touch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.ringingmaster.engine.parser.assignparsetype.ParseType.CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFAULT_CALL_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.DEFINITION_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.PLAIN_LEAD_MULTIPLIER;
import static org.ringingmaster.engine.parser.assignparsetype.ParseType.SPLICE_MULTIPLIER;
import static org.ringingmaster.engine.parser.cell.ParsedCellFactory.buildSection;
import static org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess.DEFINITION_COLUMN;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class AssignAndGroupMultiplier implements Function<Parse, Parse> {

    private final Logger log = LoggerFactory.getLogger(AssignAndGroupMultiplier.class);

    @Override
    public Parse apply(Parse parse) {

        log.debug("[{}] > assign and group multiplier", parse.getUnderlyingTouch().getTitle());

        final boolean hasFullyDefinedDefaultCall = hasFullyDefinedDefaultCall(parse.getUnderlyingTouch());

        final HashBasedTable<Integer, Integer, ParsedCell> touchTableResult =
                HashBasedTable.create(parse.allTouchCells().getBackingTable());

        for (BackingTableLocationAndValue<ParsedCell> cellAndLocation : parse.mainBodyCells()) {
            final ParsedCell replacementParsedCell = parseCellNumbers(cellAndLocation.getValue(), false, parse.getUnderlyingTouch().isSpliced(), hasFullyDefinedDefaultCall);
            touchTableResult.put(cellAndLocation.getRow(), cellAndLocation.getCol(), replacementParsedCell);
        }

        for (BackingTableLocationAndValue<ParsedCell> cellAndLocation : parse.splicedCells()) {
            final ParsedCell replacementParsedCell = parseCellNumbers(cellAndLocation.getValue(), true, parse.getUnderlyingTouch().isSpliced(), hasFullyDefinedDefaultCall);
            touchTableResult.put(cellAndLocation.getRow(), cellAndLocation.getCol(), replacementParsedCell);
        }

        // NOTE: The callPositionCells do not have any need for multiplier so are not parsed.
        final Set<String> splicedDefinitionsInUse = new InUseSpliceDefinitionsTransitively().apply(parse);

        final HashBasedTable<Integer, Integer, ParsedCell> definitionTableResult =
                HashBasedTable.create(parse.allDefinitionCells().getBackingTable());

        for (String shorthand : parse.getAllDefinitionShorthands()) {
            final Optional<ImmutableArrayTable<ParsedCell>> definitionByShorthand = parse.findDefinitionByShorthand(shorthand);

            if (definitionByShorthand.isPresent()) {
                boolean splicedCell = splicedDefinitionsInUse.contains(shorthand);
                final ImmutableArrayTable<ParsedCell> cell = definitionByShorthand.get();
                final ParsedCell replacementParsedCell = parseCellNumbers(cell.get(0, DEFINITION_COLUMN), splicedCell, parse.getUnderlyingTouch().isSpliced(), hasFullyDefinedDefaultCall);
                definitionTableResult.put(cell.getBackingRowIndex(0), cell.getBackingColumnIndex(DEFINITION_COLUMN), replacementParsedCell);
            }
        }


        final Parse result = new ParseBuilder()
                .prototypeOf(parse)
                .setTouchTableCells(touchTableResult)
                .setDefinitionTableCells(definitionTableResult)
                .build();

        log.debug("[{}] < assign and group multiplier", parse.getUnderlyingTouch().getTitle());

        return result;
    }

    private ParsedCell parseCellNumbers(final ParsedCell cell, boolean spliceCell, boolean splicedPerformance, boolean hasDefaultCall) {
        Optional<ParseType> parseTypeToRight = Optional.empty();

        final ParsedCellMutator parsedCellMutator = new ParsedCellMutator().prototypeOf(cell);

        // reverse iterate elements
        for (int elementIndex = cell.getElementSize() - 1; elementIndex >= 0; elementIndex--) {

            if (cell.getSectionAtElementIndex(elementIndex).isPresent()) {
                parseTypeToRight = Optional.of(cell.getSectionAtElementIndex(elementIndex).get().getParseType());
            }
            else if (Character.isDigit(cell.getElement(elementIndex).getCharacter().charAt(0))) {

                if (!parseTypeToRight.isPresent()) {
                    parseTypeToRight = matchAsDefaultCallMultiplier(elementIndex, parsedCellMutator, spliceCell, splicedPerformance, hasDefaultCall);
                }
                else {
                    switch (parseTypeToRight.get()) {
                        // Default Call multiplier
                        case WHITESPACE:
                        case VARIANCE_OPEN:
                        case VARIANCE_CLOSE:
                        case MULTIPLIER_GROUP_CLOSE:
                            parseTypeToRight = matchAsDefaultCallMultiplier(elementIndex, parsedCellMutator, spliceCell, splicedPerformance, hasDefaultCall);
                            break;

                        // Initial (Rightmost) multipliers
                        case CALL:
                            parseTypeToRight = addSectionToExistingGroup(parsedCellMutator, elementIndex, CALL_MULTIPLIER);
                            break;
                        case MULTIPLIER_GROUP_OPEN:
                            parseTypeToRight = addSectionToExistingGroup(parsedCellMutator, elementIndex, MULTIPLIER_GROUP_OPEN_MULTIPLIER);
                            break;
                        case PLAIN_LEAD:
                            parseTypeToRight = addSectionToExistingGroup(parsedCellMutator, elementIndex, PLAIN_LEAD_MULTIPLIER);
                            break;
                        case DEFINITION:
                            parseTypeToRight = addSectionToExistingGroup(parsedCellMutator, elementIndex, DEFINITION_MULTIPLIER);
                            break;
                        case SPLICE:
                            parseTypeToRight = addSectionToExistingGroup(parsedCellMutator, elementIndex, SPLICE_MULTIPLIER);
                            break;

                        // Additional (moving left) multiplier characters
                        case DEFAULT_CALL_MULTIPLIER:
                        case CALL_MULTIPLIER:
                        case MULTIPLIER_GROUP_OPEN_MULTIPLIER:
                        case PLAIN_LEAD_MULTIPLIER:
                        case DEFINITION_MULTIPLIER:
                        case SPLICE_MULTIPLIER:
                            parsedCellMutator.widenSectionLeft(elementIndex+1, 1);
                            break;
                        //TODO BLOCK and BLOCK_MULTIPLIER
                    }
                }
            }
            else {
                // unparsed and not a digit
                parseTypeToRight = Optional.empty();
            }

       }

        return parsedCellMutator.build();
    }

    private Optional<ParseType> addSectionToExistingGroup(ParsedCellMutator parsedCellMutator, int elementIndex, ParseType parseType) {
        parsedCellMutator.addSectionIntoExistingGroup(buildSection(elementIndex, 1, parseType), elementIndex + 1);
        return Optional.of(parseType);
    }

    //TODO need to enhance tool tips as per old ringingmaster. See: CString CellElement::getTipText()


    private Optional<ParseType> matchAsDefaultCallMultiplier(int elementIndex, ParsedCellMutator parsedCellMutator, boolean spliceCell, boolean splicedPerformance, boolean hasFullyDefinedDefaultCall) {
        if (spliceCell) {
            return Optional.empty();
        }

        parsedCellMutator.addSectionAndGenerateMatchingNewGroup(buildSection(elementIndex, 1, DEFAULT_CALL_MULTIPLIER));

        if (!hasFullyDefinedDefaultCall) {
            if (splicedPerformance) {
                parsedCellMutator.invalidateGroup(elementIndex, "Default call not defined in all methods");
            } else {
                parsedCellMutator.invalidateGroup(elementIndex, "No default call defined");
            }
        }
        return Optional.of(DEFAULT_CALL_MULTIPLIER);

    }

    //TODO we should be able to detect the actual notations being used for spliced, and only check those.
    private boolean hasFullyDefinedDefaultCall(Touch touch) {
        for (NotationBody notation : touch.getAvailableNotations()) {
            if (notation.getDefaultCall() == null) {
                return false;
            }
        }

        if (touch.getAvailableNotations().size() == 0) {
            return false;
        }
        return true;
    }
}