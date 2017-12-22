package org.ringingmaster.engine.touch.container;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationBody;
import net.jcip.annotations.NotThreadSafe;
import org.ringingmaster.engine.grid.Grid;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A holder of all the raw data needed to be able to store, parse and prove a touch.
 *
 * @user Stephen
 */
@NotThreadSafe
@Deprecated
public interface Touch extends Cloneable {

	int START_AT_ROW_MAX                         = 10_000;
	int TERMINATION_MAX_ROWS_INITIAL_VALUE       = 10_000;
	int TERMINATION_MAX_ROWS_MAX                 = 1_000_000;
	int TERMINATION_MAX_LEADS_MAX                = 10_000;
	int TERMINATION_MAX_PARTS_MAX                = 10_000;
	int TERMINATION_CIRCULAR_TOUCH_INITIAL_VALUE = 2;
	int TERMINATION_CIRCULAR_TOUCH_MAX           = 100_000;

	enum Mutated {
		MUTATED,
		UNCHANGED
	}

	Touch clone() throws CloneNotSupportedException;

	String getTitle();
	Mutated setTitle(String title);

	String getAuthor();
	Mutated setAuthor(String author);

	CheckingType getCheckingType();
	Mutated setTouchCheckingType(CheckingType checkingType);

	NumberOfBells getNumberOfBells();
	Mutated setNumberOfBells(NumberOfBells numberOfBells);

	Bell getCallFromBell();
	Mutated setCallFromBell(Bell callFromBell);

	Mutated addNotation(NotationBody notationToAdd);
	List<String> checkAddNotation(NotationBody notationToAdd);
	Mutated removeNotation(NotationBody notationForRemoval);
	Mutated updateNotation(NotationBody originalNotation, NotationBody replacementNotation);
	List<String> checkUpdateNotation(NotationBody originalNotation, NotationBody replacementNotation);

	List<NotationBody> getAllNotations();
	List<NotationBody> getValidNotations();
	List<NotationBody> getNotationsInUse();

	Mutated setNonSplicedActiveNotation(NotationBody activeNotation);
	NotationBody getNonSplicedActiveNotation();

	boolean isSpliced();
	Mutated setSpliced(boolean spliced);

	String getPlainLeadToken();
	Mutated setPlainLeadToken(String plainLeadToken);

	TouchDefinition addDefinition(String shorthand, String characters);
	Set<TouchDefinition> getDefinitions();
	void removeDefinition(String name);
	TouchDefinition findDefinitionByShorthand(String name);

	MethodRow getStartChange();
	Mutated setStartChange(MethodRow startChange);

	int getStartAtRow();
	Mutated setStartAtRow(int startAtRow);

	Stroke getStartStroke();
	Mutated setStartStroke(Stroke startStroke);

	Optional<NotationBody> getStartNotation();
	Mutated setStartNotation(NotationBody startNotation);
	Mutated removeStartNotation();

	int getTerminationMaxRows();
	Mutated setTerminationMaxRows(int terminationMaxRows);

	Optional<Integer> getTerminationMaxLeads();
	Mutated setTerminationMaxLeads(int terminationMaxLeads);
	Mutated removeTerminationMaxLeads();

	Optional<Integer> getTerminationMaxParts();
	Mutated setTerminationMaxParts(int terminationMaxParts);
	Mutated removeTerminationMaxParts();

	Optional<Integer> getTerminationMaxCircularTouch();
	Mutated setTerminationMaxCircularTouch(int terminationCircularTouch);
	Mutated removeTerminationMaxCircularTouch();

	Optional<MethodRow> getTerminationChange();
	Mutated setTerminationChange(MethodRow terminationChange);
	Mutated removeTerminationChange();

	TouchElement insertCharacter(int columnIndex, int rowIndex, int cellIndex, char character);
	void addCharacters(int columnIndex, int rowIndex, String characters);
	void removeCharacter(int columnIndex, int rowIndex, int cellIndex);

	TouchElement getElement(int columnIndex, int rowIndex, int cellIndex);

	void resetParseData();

	Grid<TouchCell> allCellsView();
	Grid<TouchCell> callPositionView();
	Grid<TouchCell> mainBodyView();
	Grid<TouchCell> spliceView();

	int getColumnCount();
	int getRowCount();

	void setColumnCount(int columnCount);
	void setRowCount(int rowCount);

	void incrementColumnCount();
	void incrementRowCount();

	/**
	 * @return true if changes were made
	 */
	boolean collapseEmptyRowsAndColumns();

}

