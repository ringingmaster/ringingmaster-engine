package org.ringingmaster.engine.parser.parse;

import com.google.common.collect.ImmutableSet;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parser.cell.ParsedCell;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.tableaccess.DefaultDefinitionTableAccess;
import org.ringingmaster.engine.touch.tableaccess.DefaultTouchTableAccess;
import org.ringingmaster.engine.touch.tableaccess.DefinitionTableAccess;
import org.ringingmaster.engine.touch.tableaccess.TouchTableAccess;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultParse implements Parse {

    private final Touch touch;
    private final TouchTableAccess<ParsedCell> touchTableAccessDelegate;
    private final DefinitionTableAccess<ParsedCell> definitionTableAccessDelegate;

    DefaultParse(Touch touch, ImmutableArrayTable<ParsedCell> mainTableCells, ImmutableArrayTable<ParsedCell> definitionCells) {
        this.touch = touch;
        this.touchTableAccessDelegate = new DefaultTouchTableAccess<>(mainTableCells, touch.getCheckingType(), touch.isSpliced());
        this.definitionTableAccessDelegate = new DefaultDefinitionTableAccess<>(definitionCells);
    }

    @Override
    public Touch getTouch() {
        return touch;
    }

    @Override
    public ImmutableArrayTable<ParsedCell> allTouchCells() {
        return touchTableAccessDelegate.allTouchCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> mainBodyCells() {
        return touchTableAccessDelegate.mainBodyCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> callPositionCells() {
        return touchTableAccessDelegate.callPositionCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> splicedCells() {
        return touchTableAccessDelegate.splicedCells();
    }


    @Override
    public ImmutableArrayTable<ParsedCell> allDefinitionCells() {
        return definitionTableAccessDelegate.allDefinitionCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> definitionShorthandCells() {
        return definitionTableAccessDelegate.definitionShorthandCells();
    }

    @Override
    public ImmutableArrayTable<ParsedCell> definitionDefinitionCells() {
        return definitionTableAccessDelegate.definitionDefinitionCells();
    }

    @Override
    public Optional<ImmutableArrayTable<ParsedCell>> findDefinitionByShorthand(String shorthand) {
        return definitionTableAccessDelegate.findDefinitionByShorthand(shorthand);
    }

    @Override
    public ImmutableSet<String> getAllDefinitionShorthands() {
        return definitionTableAccessDelegate.getAllDefinitionShorthands();
    }

    @Override
    public String toString() {
        return "DefaultParse{" +
                "touch=" + touch +
                ", touchTableAccess=" + touchTableAccessDelegate +
                ", definitionTableAccess=" + definitionTableAccessDelegate +
                '}';
    }
}
