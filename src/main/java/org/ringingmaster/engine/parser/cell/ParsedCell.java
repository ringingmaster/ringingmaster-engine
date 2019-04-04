package org.ringingmaster.engine.parser.cell;

import com.google.common.collect.ImmutableList;
import org.ringingmaster.engine.parser.cell.grouping.ElementRange;
import org.ringingmaster.engine.parser.cell.grouping.Group;
import org.ringingmaster.engine.parser.cell.grouping.Section;
import org.ringingmaster.engine.touch.cell.Cell;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface ParsedCell extends Cell {

    Cell getParentCell();

    ImmutableList<Section> allSections();//TODO need to test

    ImmutableList<Group> allGroups();//TODO need to test

    Optional<Section> getSectionAtElementIndex(int elementIndex);

    Optional<Group> getGroupAtElementIndex(int elementIndex);

    Group getGroupForSection(Section section); //TODO need to test

    String getCharacters(ElementRange elementRange);

}
