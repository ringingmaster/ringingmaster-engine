package com.concurrentperformance.ringingmaster.engine.touch.container;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import net.jcip.annotations.NotThreadSafe;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A holder of all the raw data needed to be able to store, parse and prove a touch.
 *
 * @user Stephen
 */
@NotThreadSafe
public interface Touch extends Cloneable {

	int START_AT_ROW_MAX                         = 10000;
	int TERMINATION_MAX_ROWS_INITIAL_VALUE       = 10000;
	int TERMINATION_MAX_ROWS_MAX                 = 1000000;
	int TERMINATION_MAX_LEADS_MAX                = 10000;
	int TERMINATION_MAX_PARTS_MAX                = 10000;
	int TERMINATION_CIRCULAR_TOUCH_INITIAL_VALUE = 2;
	int TERMINATION_CIRCULAR_TOUCH_MAX           = 100000;

	enum Mutated {
		MUTATED,
		UNCHANGED
	}

	Touch clone() throws CloneNotSupportedException;

	String getTitle();
	Mutated setTitle(String title);

	String getAuthor();
	Mutated setAuthor(String author);

	TouchCheckingType getTouchCheckingType();
	void setTouchCheckingType(TouchCheckingType touchCheckingType);

	void    setNumberOfBells(NumberOfBells numberOfBells);
	NumberOfBells getNumberOfBells();

	Bell getCallFromBell();
	void setCallFromBell(Bell callFromBell);

	void addNotation(NotationBody notationToAdd);
	List<String> checkAddNotation(NotationBody notationToAdd);
	void removeNotation(NotationBody notationForRemoval);
	void exchangeNotation(NotationBody originalNotation, NotationBody replacementNotation);
	List<NotationBody> getAllNotations();
	List<NotationBody> getValidNotations();
	List<NotationBody> getNotationsInUse();

	void setNonSplicedActiveNotation(NotationBody activeNotation);
	NotationBody getNonSplicedActiveNotation();

	boolean isSpliced();
	void setSpliced(boolean spliced);

	String getPlainLeadToken();
	void setPlainLeadToken(String plainLeadToken);

	TouchDefinition addDefinition(String shorthand, String characters);
	Set<TouchDefinition> getDefinitions();
	void removeDefinition(String name);
	TouchDefinition findDefinitionByName(String name);

	MethodRow getStartChange();
	void setStartChange(MethodRow startChange);

	int getStartAtRow();
	void setStartAtRow(int startAtRow);

	void setStartStroke(Stroke startStroke);
	Stroke getStartStroke();

	Optional<NotationBody> getStartNotation();
	void setStartNotation(NotationBody startNotation);
	void removeStartNotation();

	int getTerminationMaxRows();
	void setTerminationMaxRows(int terminationMaxRows);

	Optional<Integer> getTerminationMaxLeads();
	void setTerminationMaxLeads(int terminationMaxLeads);
	void removeTerminationMaxLeads();

	Optional<Integer> getTerminationMaxParts();
	void setTerminationMaxParts(int terminationMaxParts);
	void removeTerminationMaxParts();

	Optional<Integer> getTerminationMaxCircularTouch();
	void setTerminationMaxCircularTouch(int terminationCircularTouch);
	void removeTerminationMaxCircularTouch();

	Optional<MethodRow> getTerminationChange();
	void setTerminationChange(MethodRow terminationChange);
	void removeTerminationChange();

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

