package org.ringingmaster.engine.touch.newcontainer.cell;

import com.google.common.collect.Lists;
import org.ringingmaster.engine.touch.newcontainer.element.ElementBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellBuilder {

    private Cell prototype;
    private List<CharacterChange> characterChanges = Lists.newArrayList();

    public CellBuilder defaults() {
        add("");
        return this;
    }

    public CellBuilder prototypeOf(Cell prototype) {
        this.prototype = prototype;
        return this;
    }

    public CellBuilder add(String characters) {
        this.characterChanges.add(new CharacterChange(0, characters));
        return this;
    }

    public CellBuilder insert(int index, String characters) {
        checkState(index >= 0);
        this.characterChanges.add(new CharacterChange(index, characters));
        return this;
    }

    public Cell build() {
        StringBuilder buff = new StringBuilder();
        if (prototype != null) {
            buff.append(prototype.getCharacters());
        }

        for (CharacterChange characterChange : characterChanges) {
            buff.insert(characterChange.index, characterChange.characters);
        }

        return new Cell(ElementBuilder.createElements(buff.toString()));
    }

    private class CharacterChange {

        final int index;
        final String characters;

        private CharacterChange(int index, String characters) {
            this.index = index;
            this.characters = characters;
        }
    }
}
