package org.ringingmaster.engine.composition.cell;

import com.google.common.collect.Lists;
import org.ringingmaster.engine.composition.element.ElementBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkState;
import static org.ringingmaster.engine.composition.cell.CellBuilder.CharacterChangeAction.DELETE;
import static org.ringingmaster.engine.composition.cell.CellBuilder.CharacterChangeAction.INSERT;

/**
 * TODO comments???
 *
 * @author stevelake
 */
public class CellBuilder {

    private Cell prototype;
    private List<CharacterChange> characterChanges = Lists.newArrayList();

    public CellBuilder defaults() {
        insert(0, "");
        return this;
    }

    public CellBuilder prototypeOf(Cell prototype) {
        this.prototype = prototype;
        return this;
    }

    public CellBuilder insert(int index, String characters) {
        checkState(index >= 0);

        this.characterChanges.add(new CharacterChange(INSERT, index, characters));
        return this;
    }

    public CellBuilder delete(int index, int count) {
        checkState(index >= 0);
        checkState(count > 0);

        this.characterChanges.add(new CharacterChange(DELETE, index, count));
        return this;
    }

    public Cell build() {
        StringBuilder buff = new StringBuilder();
        if (prototype != null) {
            buff.append(prototype.getCharacters());
        }

        for (CharacterChange characterChange : characterChanges) {

            if (characterChange.action.equals(INSERT)) {
                checkPositionIndex(characterChange.index, buff.length(), characterChange.toString() + " for [" + buff.toString() + "]");
                buff.insert(characterChange.index, characterChange.characters);
            }
            else if (characterChange.action.equals(DELETE)) {
                checkElementIndex(characterChange.index, buff.length(), characterChange.toString() + " for [" + buff.toString() + "]");
                buff.delete(characterChange.index, characterChange.index + characterChange.count);
            }
        }

        if (buff.length() == 0) {

        }

        return new DefaultCell(ElementBuilder.createElements(buff.toString()));
    }

    enum CharacterChangeAction {
        INSERT,
        DELETE
    }

    private class CharacterChange {
        final CharacterChangeAction action;
        final int index;
        final int count;
        final String characters;

        private CharacterChange(CharacterChangeAction action, int index, String characters) {
            this.action = action;
            this.index = index;
            this.count = 0;
            this.characters = characters;
        }

        private CharacterChange(CharacterChangeAction action, int index, int count) {
            this.action = action;
            this.index = index;
            this.count = count;
            this.characters = null;
        }

        @Override
        public String toString() {
            return "CharacterChange{" +
                    "action=" + action +
                    ", index=" + index +
                    ", count=" + count +
                    ", characters='" + characters + '\'' +
                    '}';
        }
    }
}
