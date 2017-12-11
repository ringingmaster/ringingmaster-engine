package org.ringingmaster.engine.parsernew;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.ringingmaster.engine.arraytable.ImmutableArrayTable;
import org.ringingmaster.engine.parsernew.cell.ParsedCell;
import org.ringingmaster.engine.touch.newcontainer.Touch;
import org.ringingmaster.engine.touch.newcontainer.tableaccess.DefaultDefinitionTableAccess;
import org.ringingmaster.engine.touch.newcontainer.tableaccess.DefaultTouchTableAccess;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public class DefaultParse implements Parse {

    private final Touch touch;
    private final DefaultTouchTableAccess<ParsedCell> touchTableAccessDelegate;
    private final DefaultDefinitionTableAccess<ParsedCell> definitionTableAccessDelegate;

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
    public ImmutableArrayTable<ParsedCell> allShorthands() {
        return definitionTableAccessDelegate.allShorthands();
    }

    @Override
    public Optional<ImmutableArrayTable<ParsedCell>> findDefinitionByShorthand(String shorthand) {
        return definitionTableAccessDelegate.findDefinitionByShorthand(shorthand);
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
