package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.touch.container.cell.Cell;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface ParsedCell extends Cell {

    ImmutableList<Section> allSections();//TODO need to test

    ImmutableList<Group> allGroups();//TODO need to test

    Optional<Section> getSectionAtElementIndex(int elementIndex);

    Optional<Group> getGroupAtElementIndex(int elementIndex);

    Group getGroupForSection(Section section); //TODO need to test

    String getCharacters(ElementSequence elementSequence);

}
