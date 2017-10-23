package org.ringingmaster.engine.parsernew.cell;

import net.jcip.annotations.Immutable;
import org.ringingmaster.engine.touch.newcontainer.cell.Cell;

import java.util.Optional;

/**
 * TODO comments???
 *
 * @author stevelake
 */
@Immutable
public interface ParsedCell extends Cell {

    Optional<Section> getSectionAtElementIndex(int elementIndex);

    Optional<Group> getWordAtElementIndex(int elementIndex);

}
