package org.ringingmaster.engine.touch.newcontainer.cell;

import com.google.common.collect.Lists;
import org.ringingmaster.engine.touch.newcontainer.element.ElementBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.touch.newcontainer.cell.CellBuilder.CharacterChangeAction.INSERT;
import static org.ringingmaster.engine.touch.newcontainer.cell.CellBuilder.CharacterChangeAction.DELETE;

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
        this.characterChanges.add(new CharacterChange(INSERT, 0, characters));
        return this;
    }

    public CellBuilder insert(int index, String characters) {
        checkState(index >= 0);
        this.characterChanges.add(new CharacterChange(INSERT, index, characters));
        return this;
    }

    public CellBuilder delete(int index) {
        checkState(index >= 0);
        this.characterChanges.add(new CharacterChange(DELETE, index, ""));
        return this;
    }

    public Cell build() {
        StringBuilder buff = new StringBuilder();
        if (prototype != null) {
            buff.append(prototype.getCharacters());
        }

        for (CharacterChange characterChange : characterChanges) {
            if (characterChange.action.equals(INSERT)) {
                buff.insert(characterChange.index, characterChange.characters);
            }
            else if (characterChange.action.equals(DELETE)) {
                buff.delete(characterChange.index, characterChange.index + 1);
            }
        }

        return new Cell(ElementBuilder.createElements(buff.toString()));
    }

    enum CharacterChangeAction {
        INSERT,
        DELETE
    }

    private class CharacterChange {
        final CharacterChangeAction action;
        final int index;
        final String characters;

        private CharacterChange(CharacterChangeAction action, int index, String characters) {
            this.action = action;
            this.index = index;
            this.characters = characters;
        }
    }
}
