package com.concurrentperformance.ringingmaster.engine.touch;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.google.common.base.Optional;
import net.jcip.annotations.NotThreadSafe;

import java.util.List;
import java.util.Set;

/**
 * A holder of all the raw data needed to be able to store, parse and prove a touch.
 *
 * @user Stephen
 */
@NotThreadSafe
public interface Touch extends Cloneable {

	Touch clone() throws CloneNotSupportedException;

	String getTitle();
	void setTitle(String title);

	String getAuthor();
	void setAuthor(String author);

	TouchType getTouchType();
	void setTouchType(TouchType touchType);

	String setNumberOfBells(NumberOfBells numberOfBells);
	NumberOfBells getNumberOfBells();

	Bell getCallFromBell();
	void setCallFromBell(Bell callFromBell);

	void addNotation(NotationBody notationToAdd);
	void removeNotation(NotationBody notationForRemoval);
	List<NotationBody> getAllNotations();
	List<NotationBody> getValidNotations();
	List<NotationBody> getNotationsInUse();

	void setActiveNotation(NotationBody activeNotation);
	NotationBody getSingleMethodActiveNotation();

	TouchDefinition addDefinition(String name, String characters);
	Set<TouchDefinition> getDefinitions();
	void removeDefinition(String name);
	TouchDefinition findDefinitionByName(String name);

	boolean isSpliced();
	void setSpliced(boolean spliced);

	/**
	 * @param columnIndex x cell position
	 * @param rowIndex y cell position
	 * @param cellIndex position inside a cell
	 */
	TouchElement insertCharacter(int columnIndex, int rowIndex, int cellIndex, char character);

	/**
	 * @param columnIndex x cell position
	 * @param rowIndex y cell position
	 */
	void addCharacters(int columnIndex, int rowIndex, String characters);

	/**
	 * @param columnIndex x cell position
	 * @param rowIndex y cell position
	 * @param cellIndex position inside a cell
	 */
	void removeCharacter(int columnIndex, int rowIndex, int cellIndex);

	/**
	 * @param columnIndex x cell position
	 * @param rowIndex y cell position
	 * @param cellIndex position inside a cell
	 */
	TouchElement getElement(int columnIndex, int rowIndex, int cellIndex);

	/**
	 * The string token that represents a plain lead
	 */
	String getPlainLeadToken();

	/**
	 * The string token that represents a plain lead
	 */
	void setPlainLeadToken(String plainLeadToken);

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

	Optional<Integer> getTerminationMaxLeads();
	void setTerminationMaxLeads(int terminationMaxLeads);
	void removeTerminationMaxLeads();

	Optional<Integer> getTerminationMaxRows();
	void setTerminationMaxRows(int terminationMaxRows);
	void removeTerminationMaxRows();

	Optional<MethodRow> getTerminationSpecificRow();
	void setTerminationSpecificRow(MethodRow terminationSpecificRow);
	void removeTerminationSpecificRow();

	/**
	 * @return true if changes were made
	 */
	boolean collapseEmptyRowsAndColumns();
}
