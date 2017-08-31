package org.ringingmaster.engine.touch.newcontainer.cell;

import org.ringingmaster.engine.touch.newcontainer.element.ElementBuilder;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellBuilder {

    private Cell prototype;
    private List<> charactersToAdd;

    public CellBuilder defaults() {
        add("");
        return this;
    }

    public CellBuilder prototypeOf(Cell prototype) {
        this.prototype = prototype;
        return this;
    }

    public CellBuilder add(String charactersToAdd) {
        this.charactersToAdd = Optional.of(charactersToAdd);
        return this;
    }

    public CellBuilder insert(int index, String charactersToAdd) {
        checkState(index >= 0);
        this.charactersToAdd = Optional.of(charactersToAdd);
        return this;
    }

    Cell build() {
        return new Cell(ElementBuilder.createElements(charactersToAdd.orElseGet(() -> prototype.getCharacters())));
    }
}
