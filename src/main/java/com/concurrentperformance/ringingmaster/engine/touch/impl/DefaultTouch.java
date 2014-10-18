package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import com.concurrentperformance.ringingmaster.engine.touch.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.GridCellFactory;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchElement;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.jcip.annotations.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Default implementation of {@link com.concurrentperformance.ringingmaster.engine.touch.Touch}
 *
 * User: Stephen
 */
@NotThreadSafe
public class DefaultTouch implements Touch {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final int SAFETY_VALVE_MAX_ROWS = 100000;

	//IMPORTANT NOTE: When adding items here, think about the clone method and immutability, and toString. Also toString
	private String title = "";
	private String author = "";

	private NumberOfBells numberOfBells = NumberOfBells.BELLS_6;
	private final Grid<TouchCell> cells;

	private Bell callFromBell = numberOfBells.getTenor();
	private List<NotationBody> notations = new ArrayList<>();
	private NotationBody activeNotation;
	private boolean spliced;
	private String plainLeadToken = "p";
	private TouchType touchType = TouchType.COURSE_BASED;
	private SortedMap<String, TouchDefinition> definitions = new TreeMap<>();

	private Optional<Integer> terminationMaxLeads = Optional.absent();
	private Optional<Integer> terminationMaxRows = Optional.absent();
	private Optional<MethodRow> terminationSpecificRow = Optional.absent();

	private static GridCellFactory<TouchCell> FACTORY = new GridCellFactory<TouchCell>() {
		@Override
		public TouchCell buildCell(int columnIndex, int rowIndex) {
			return new DefaultTouchCell();
		}
	};

	DefaultTouch() {
		this.cells = new DefaultGrid<>(FACTORY, 1, 1);
		setTerminationMaxRows(SAFETY_VALVE_MAX_ROWS);
	}

	@Override
	public Touch clone() throws CloneNotSupportedException {
		DefaultTouch touchClone = new DefaultTouch();

		touchClone.numberOfBells = numberOfBells;
		touchClone.cells.setColumnCount(this.cells.getColumnCount());
		touchClone.cells.setRowCount(this.cells.getRowCount());

		for(int rowIndex=0;rowIndex<this.cells.getRowCount();rowIndex++) {
			for(int columnIndex=0;columnIndex<this.cells.getColumnCount();columnIndex++) {
				TouchCell cellClone = this.cells.getCell(columnIndex, rowIndex).clone();
				touchClone.cells.setCell(columnIndex, rowIndex, cellClone);
			}
		}

		touchClone.title = this.title;
		touchClone.author = this.author;
		touchClone.notations.addAll(this.notations);
		touchClone.activeNotation = this.activeNotation;
		touchClone.spliced = this.spliced;
		touchClone.plainLeadToken = this.plainLeadToken;
		touchClone.touchType = this.touchType;
		for (TouchDefinition definition : definitions.values()) {
			touchClone.definitions.put(definition.getName(), definition.clone());
		}

		if(terminationMaxRows.isPresent()) {
			touchClone.setTerminationMaxRows(terminationMaxRows.get());
		}
		if(terminationMaxLeads.isPresent()) {
			touchClone.setTerminationMaxLeads(terminationMaxLeads.get());
		}
		if(terminationSpecificRow.isPresent()) {
			touchClone.setTerminationSpecificRow(terminationSpecificRow.get());
		}

		return touchClone;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public void setTouchType(TouchType touchType) {
		this.touchType = touchType;
	}

	@Override
	public TouchType getTouchType() {
		return touchType;
	}

	@Override
	public String setNumberOfBells(NumberOfBells numberOfBells) {
		// NOTE: When modifying this method, also modify the error string that
		// is generated by the TouchDocument.setNumberOfBells()
		if (this.numberOfBells != numberOfBells) {
			this.numberOfBells = numberOfBells;

			StringBuilder builder = new StringBuilder();
			if (callFromBell.getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
				callFromBell = numberOfBells.getTenor();
			}

			if (terminationSpecificRow.isPresent()) {

				final MethodRow existingTerminationRow = getTerminationSpecificRow().get();
				final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
				setTerminationSpecificRow(newTerminationRow);
			}

			if (!isSpliced() &&
					activeNotation != null &&
					activeNotation.getNumberOfWorkingBells().getBellCount() > numberOfBells.getBellCount()) {
				final List<NotationBody> filteredNotations = NotationBuilderHelper.filterNotations(notations, numberOfBells);
				if (filteredNotations.size() > 0) {
					activeNotation = filteredNotations.get(0);
				}
				else {
					activeNotation = null;
				}
			}

			// TODO validation of other items. e.g. methods Etc.
			return builder.toString();
		}

		return null;
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return numberOfBells;
	}

	@Override
	public Bell getCallFromBell() {
		return callFromBell;
	}

	@Override
	public void setCallFromBell(Bell callFromBell) {
		checkState(callFromBell.getZeroBasedBell() < numberOfBells.getBellCount());
		this.callFromBell = callFromBell;
	}

	@Override
	public void addNotation(NotationBody notationToAdd) {
		checkNotNull(notationToAdd, "notation must not be null");
		//checkArgument(notationToAdd.getNumberOfWorkingBells().equals(numberOfBells), "notationToAdd [" + notationToAdd + "] must have the same number of bells as the touch [" +numberOfBells + "]");
		// Check duplicate name
		for (NotationBody existingNotation : notations) {
			if (existingNotation.getNameIncludingNumberOfBells().equals(notationToAdd.getNameIncludingNumberOfBells())) {
				throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "] as it has a duplicate name to existing notation [" + existingNotation + "]");
			}
		}

		notations.add(notationToAdd);

		if (spliced == false && activeNotation == null) {
			activeNotation = notationToAdd;
		}
	}

	@Override
	public void removeNotation(NotationBody notationForRemoval) {
		checkNotNull(notationForRemoval, "notationForRemoval must not be null");
		notations.remove(notationForRemoval);

		// Sort out the next notation if it is the active notation
		if (notationForRemoval.equals(activeNotation)) {
			activeNotation = null;
			for (NotationBody notation : notations) {
				if (notation.getName().compareTo(notationForRemoval.getName()) > 0) {
					activeNotation = notation;
					break;
				}
			}
			if (activeNotation == null && notations.size() > 0) {
				activeNotation = notations.iterator().next();
			}
		}
	}

	@Override
	public List<NotationBody> getAllNotations() {
		return Collections.unmodifiableList(notations);
	}

	@Override
	public List<NotationBody> getValidNotations() {
		return NotationBuilderHelper.filterNotations(notations, numberOfBells);
	}

	@Override
	public List<NotationBody> getNotationsInUse() {
		if (isSpliced()) {
			return getValidNotations();
		}
		else {
			// Not Spliced
			if (activeNotation != null) {
				return Lists.<NotationBody>newArrayList(activeNotation);
			}
		}

		return Collections.emptyList();
	}


	@Override
	public NotationBody getSingleMethodActiveNotation() {
		return activeNotation;
	}

	@Override
	public TouchDefinition addDefinition(String name, String characters) {
		checkNotNull(name, "name must not be null");
		checkNotNull(name.length() > 0, "name must contain some characters");
		// Check duplicate name
		if (definitions.get(name) != null) {
			throw new IllegalArgumentException("Can't add definition [" + name + "] as it has a duplicate name to existing definition [" + definitions.get(name) + "]");
		}

		TouchDefinition definition = new DefaultTouchDefinition(name);
		definition.add(characters);
		definitions.put(definition.getName(), definition);
		return definition;
	}

	@Override
	public Set<TouchDefinition> getDefinitions() {
		return Sets.newHashSet(definitions.values());
	}

	@Override
	public void removeDefinition(String name) {
		definitions.remove(name);
	}

	@Override
	public TouchDefinition findDefinitionByName(String name) {
		return definitions.get(name);
	}

	@Override
	public void setActiveNotation(NotationBody activeNotation) {
		this.activeNotation = activeNotation;
	}

	@Override
	public boolean isSpliced() {
		return spliced;
	}

	@Override
	public void setSpliced(boolean spliced) {
		if (this.spliced == spliced) {
			return;
		}
		this.spliced = spliced;
		if (spliced) {
			activeNotation = null;
		}
		else if (notations.size() > 0){
			activeNotation = notations.iterator().next();
		}
	}

	@Override
	public TouchElement insertCharacter(int columnIndex, int rowIndex, int cellIndex, char character) {
		checkValidPosition(columnIndex, rowIndex, cellIndex, true);
		TouchCell cell = cells.getCell(columnIndex, rowIndex);
		return cell.insert(character, cellIndex);
	}

	@Override
	public void addCharacters(int columnIndex, int rowIndex, String characters) {
		TouchCell cell = cells.getCell(columnIndex, rowIndex);
		cell.add(characters);
	}

	@Override
	public void removeCharacter(int columnIndex, int rowIndex, int cellIndex) {
		TouchCell cell = cells.getCell(columnIndex, rowIndex);
		cell.remove(cellIndex);
	}

	@Override
	public TouchElement getElement(int columnIndex, int rowIndex, int cellIndex) {
		checkValidPosition(columnIndex, rowIndex, cellIndex, false);
		TouchCell cell = cells.getCell(columnIndex, rowIndex);
		TouchElement element = cell.getElement(cellIndex);
		return element;
	}

	@Override
	public String getPlainLeadToken() {
		return plainLeadToken;
	}

	@Override
	public void setPlainLeadToken(String plainLeadToken) {
		this.plainLeadToken = plainLeadToken;
	}

	@Override
	public void resetParseData() {
		for (TouchCell cell : cells) {
			cell.resetParseData();
		}
	}

	private void checkValidPosition(int columnIndex, int rowIndex, int elementIndex, boolean forInsert) {
		TouchCell cell = cells.getCell(columnIndex, rowIndex);
		int length = cell.getLength() + (forInsert?1:0);
		checkElementIndex(elementIndex, length, "Element index");
	}

	@Override
	public Grid<TouchCell> allCellsView() {
		return cells.unmodifiableSubGrid(
				0,
				cells.getColumnCount(),
				0,
				cells.getRowCount());
	}

	@Override
	public Grid<TouchCell> callPositionView() {
		return cells.unmodifiableSubGrid(
				0,
				(isSpliced() ? (cells.getColumnCount() - 1) : cells.getColumnCount()),
				0,
				(getTouchType() == TouchType.COURSE_BASED ? 1 : 0));
	}

	@Override
	public Grid<TouchCell> mainBodyView() {
		return cells.unmodifiableSubGrid(
				0,
				(isSpliced() ? (cells.getColumnCount() - 1) : cells.getColumnCount()),
				(getTouchType() == TouchType.COURSE_BASED ? 1 : 0),
				cells.getRowCount());
	}

	@Override
	public Grid<TouchCell> spliceView() {
		return cells.unmodifiableSubGrid(
				(isSpliced() ? (cells.getColumnCount() - 1) : cells.getColumnCount()),
				cells.getColumnCount(),
				(getTouchType() == TouchType.COURSE_BASED ? 1 : 0),
				cells.getRowCount());
	}

	@Override
	public int getColumnCount() {
		return cells.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return cells.getRowCount();
	}

	@Override
	public void setColumnCount(int columnCount) {
		cells.setColumnCount(columnCount);
	}

	@Override
	public void incrementColumnCount() {
		cells.setColumnCount(cells.getColumnCount()+1);
	}

	@Override
	public void incrementRowCount() {
		cells.setRowCount(cells.getRowCount()+1);
	}

	@Override
	public void setRowCount(int rowCount) {
		cells.setRowCount(rowCount);
	}

	@Override
	public Optional<Integer> getTerminationMaxLeads() {
		return terminationMaxLeads;
	}

	@Override
	public void setTerminationMaxLeads(int terminationMaxLeads) {
		checkState(terminationMaxLeads > 0, "Termination max leads must be greater than 0");

		this.terminationMaxLeads = Optional.of(terminationMaxLeads);
	}

	@Override
	public void removeTerminationMaxLeads() {
		this.terminationMaxLeads = Optional.absent();
	}

	@Override
	public Optional<Integer> getTerminationMaxRows() {
		return terminationMaxRows;
	}

	@Override
	public void setTerminationMaxRows(int terminationMaxRows) {
		checkState(terminationMaxRows > 0, "Termination max rows must be greater than 0");
		this.terminationMaxRows = Optional.of(terminationMaxRows);
	}

	@Override
	public void removeTerminationMaxRows() {
		terminationMaxRows = Optional.absent();
	}

	@Override
	public Optional<MethodRow> getTerminationSpecificRow() {
		return terminationSpecificRow;
	}

	@Override
	public void setTerminationSpecificRow(MethodRow terminationSpecificRow) {
		checkNotNull(terminationSpecificRow, "terminationSpecificRow cant be null");
		checkArgument(terminationSpecificRow.getNumberOfBells().equals(numberOfBells));
		this.terminationSpecificRow = Optional.of(terminationSpecificRow);
	}

	@Override
	public void removeTerminationSpecificRow() {
		terminationSpecificRow = Optional.absent();
	}

	@Override
	public boolean collapseEmptyRowsAndColumns() {
		boolean changed = false;
		for (int colIndex=0;colIndex<cells.getColumnCount();colIndex++) {
			List<TouchCell> column = cells.column(colIndex);
			if (isEmpty(column)) {
				log.info("Column [{}] is empty. Collapsing", colIndex);
				cells.removeColumn(colIndex);
				changed = true;
			}
		}
		for (int rowIndex=0;rowIndex<cells.getRowCount();rowIndex++) {
			List<TouchCell> row = cells.row(rowIndex);
			if (isEmpty(row)) {
				log.info("Row [{}] is empty. Collapsing", rowIndex);
				cells.removeRow(rowIndex);
				changed = true;
			}
		}
		return changed;
	}

	private boolean isEmpty(List<TouchCell> cellList) {
		for (TouchCell cell : cellList) {
			if (cell.getLength() != 0) {
				return false;
			}
		}
		return true;
	}

	public TouchCell getCell_FOR_TEST_ONLY(int columnIndex, int rowIndex) {
		return cells.getCell(columnIndex, rowIndex);
	}

	@Override
	public String toString() {
		return "DefaultTouch{" +
				"title='" + title + '\'' +
				", author=" + author +
				", cells=" + cells +
				", notations=" + notations +
				", activeNotation=" + activeNotation +
				", spliced=" + spliced +
				", plainLeadToken='" + plainLeadToken + '\'' +
				", touchType=" + touchType +
				", definitions=" + definitions +
				", terminationMaxLeads=" + terminationMaxLeads +
				", terminationMaxRows=" + terminationMaxRows +
				", terminationSpecificRow=" + terminationSpecificRow +
				'}';
	}
}