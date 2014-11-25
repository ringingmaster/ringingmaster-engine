package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.Stroke;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilderHelper;
import com.concurrentperformance.ringingmaster.engine.touch.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.GridCellFactory;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchElement;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
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

	//IMPORTANT NOTE: When adding items here, think about the clone method and immutability, and toString. Also toString
	private String title;
	private String author;

	private NumberOfBells numberOfBells;
	private TouchType touchType;

	private Bell callFromBell;
	private final List<NotationBody> notations;
	private NotationBody singleMethodActiveNotation;
	private boolean spliced; // we use separate spliced and active-notation, rather than an optional because otherwise, adding your first notation will always be spliced.
	private String plainLeadToken;
	private SortedMap<String, TouchDefinition> definitions;

	private MethodRow startChange;
	private int startAtRow;
	private Stroke startStroke;
	private Optional<NotationBody> startNotation;

	private Integer terminationMaxRows;
	private Optional<Integer> terminationMaxLeads;
	private Optional<Integer> terminationMaxParts;
	private Optional<Integer> terminationCircularTouch;
	private Optional<MethodRow> terminationSpecificRow;

	private final Grid<TouchCell> cells;

	private static GridCellFactory<TouchCell> FACTORY = new GridCellFactory<TouchCell>() {
		@Override
		public TouchCell buildCell(int columnIndex, int rowIndex) {
			return new DefaultTouchCell();
		}
	};

	DefaultTouch() {
		title = "";
		author = "";

		numberOfBells = NumberOfBells.BELLS_6;
		touchType = TouchType.COURSE_BASED;

		callFromBell = numberOfBells.getTenor();
		notations = new ArrayList<>();
		spliced = false;
		plainLeadToken = "p";
		definitions = new TreeMap<>();

		startChange = MethodBuilder.buildRoundsRow(numberOfBells);
		startAtRow = 0;
		startStroke = Stroke.BACKSTROKE;
		startNotation = Optional.absent();

		terminationMaxRows = TERMINATION_MAX_ROWS_INITIAL_VALUE;
		terminationMaxLeads = Optional.absent();
		terminationMaxParts = Optional.absent();
		terminationCircularTouch = Optional.of(TERMINATION_CIRCULAR_TOUCH_INITIAL_VALUE);
		terminationSpecificRow = Optional.absent();

		cells = new DefaultGrid<>(FACTORY, 1, 1);
	}

	@Override
	public Touch clone() throws CloneNotSupportedException {
		DefaultTouch touchClone = new DefaultTouch();

		touchClone.title = this.title;
		touchClone.author = this.author;

		touchClone.numberOfBells = numberOfBells;
		touchClone.touchType = this.touchType;

		touchClone.callFromBell = callFromBell;
		touchClone.notations.addAll(this.notations);
		touchClone.singleMethodActiveNotation = this.singleMethodActiveNotation;
		touchClone.spliced = this.spliced;
		touchClone.plainLeadToken = this.plainLeadToken;
		for (TouchDefinition definition : definitions.values()) {
			touchClone.definitions.put(definition.getName(), definition.clone());
		}

		touchClone.startChange = this.startChange;
		touchClone.startAtRow = this.startAtRow;
		touchClone.startStroke = this.startStroke;
		touchClone.startNotation = this.startNotation;

		touchClone.terminationMaxRows = this.terminationMaxRows;
		touchClone.terminationMaxLeads = this.terminationMaxLeads;
		touchClone.terminationMaxParts = this.terminationMaxParts;
		touchClone.terminationCircularTouch = this.terminationCircularTouch;
		touchClone.terminationSpecificRow = this.terminationSpecificRow;

		touchClone.cells.setColumnCount(this.cells.getColumnCount());
		touchClone.cells.setRowCount(this.cells.getRowCount());

		for(int rowIndex=0;rowIndex<this.cells.getRowCount();rowIndex++) {
			for(int columnIndex=0;columnIndex<this.cells.getColumnCount();columnIndex++) {
				TouchCell cellClone = this.cells.getCell(columnIndex, rowIndex).clone();
				touchClone.cells.setCell(columnIndex, rowIndex, cellClone);
			}
		}

		return touchClone;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		if (!this.title.equals(title)) {
			log.info("[{}] Set title [{}]", this.title, title);
			this.title = checkNotNull(title);
		}
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		if (!this.author.equals(author)) {
			this.author = checkNotNull(author);
			log.info("[{}] Set author [{}]", this.title, this.author);
		}
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return numberOfBells;
	}

	@Override
	public void setNumberOfBells(NumberOfBells numberOfBells) {
		// NOTE: When modifying this method, also modify the error string that
		// is generated by the TouchDocument.setNumberOfBells()
		if (this.numberOfBells != numberOfBells) {
			this.numberOfBells = checkNotNull(numberOfBells);
			log.info("[{}] Set number of bells [{}]", this.title, this.numberOfBells);

			if (callFromBell.getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
				callFromBell = numberOfBells.getTenor();
			}

			final MethodRow existingStartChange = getStartChange();
			final MethodRow newStartChange = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
			setStartChange(newStartChange);

			if (terminationSpecificRow.isPresent()) {
				final MethodRow existingTerminationRow = getTerminationSpecificRow().get();
				final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
				setTerminationSpecificRow(newTerminationRow);
			}

			if (!isSpliced() &&
					singleMethodActiveNotation != null &&
					singleMethodActiveNotation.getNumberOfWorkingBells().getBellCount() > numberOfBells.getBellCount()) {
				final List<NotationBody> filteredNotations = NotationBuilderHelper.filterNotations(notations, numberOfBells);
				if (filteredNotations.size() > 0) {
					singleMethodActiveNotation = filteredNotations.get(0);
					log.info("[{}] Set active notation [{}]", this.title, singleMethodActiveNotation.getNameIncludingNumberOfBells());
				} else {
					singleMethodActiveNotation = null;
					log.info("[{}] Set active notation [null]", this.title);
				}
			}

			if (startNotation.isPresent()) {
				final String originalNotation = startNotation.get().getNotationDisplayString(false);
				NotationBody builtNotation = NotationBuilder.getInstance()
						.setNumberOfWorkingBells(numberOfBells)
						.setUnfoldedNotationShorthand(originalNotation)
						.build();
				if (!originalNotation.equals(builtNotation.getNotationDisplayString(false))) {
					if (builtNotation.getRowCount() == 0) {
						startNotation = Optional.absent();
						log.info("[{}] Set start notation to [{}]", this.title, this.startNotation);
					} else {
						startNotation = Optional.of(builtNotation);
						log.info("[{}] Set start notation to [{}]", this.title, this.startNotation.get().getNotationDisplayString(false));
					}
				}
			}
		}
		// TODO validation of other items. e.g. methods Etc.
	}

	@Override
	public TouchType getTouchType() {
		return touchType;
	}

	@Override
	public void setTouchType(TouchType touchType) {
		if (this.touchType != touchType) {
			this.touchType = checkNotNull(touchType);
			log.info("[{}] Set touch type [{}]", this.title, this.touchType.getName());
		}
	}

	@Override
	public Bell getCallFromBell() {
		return callFromBell;
	}

	@Override
	public void setCallFromBell(Bell callFromBell) {
		if (this.callFromBell != callFromBell) {
			checkNotNull(callFromBell);
			checkState(callFromBell.getZeroBasedBell() < numberOfBells.getBellCount());
			this.callFromBell = callFromBell;
			log.info("[{}] Set call from bell to [{}]", this.title, this.callFromBell);
		}
	}

	@Override
	public void addNotation(NotationBody notationToAdd) {
		checkNotNull(notationToAdd, "notation must not be null");
		//checkArgument(notationToAdd.getNumberOfWorkingBells().equals(numberOfBells), "notationToAdd [" + notationToAdd + "] must have the same number of bells as the touch [" +numberOfBells + "]");
		// Check duplicate name
		for (NotationBody existingNotation : notations) {
			if (existingNotation.getNumberOfWorkingBells() == notationToAdd.getNumberOfWorkingBells() &&
				Objects.equal(existingNotation.getName(), notationToAdd.getName())) {
				throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "] as it has a duplicate name to existing notation [" + existingNotation + "]");
			}
			if (!Strings.isNullOrEmpty(notationToAdd.getSpliceIdentifier()) &&
				Objects.equal(existingNotation.getSpliceIdentifier(), notationToAdd.getSpliceIdentifier())) {
				throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "] as it has a duplicate splice identifier to existing notation [" + existingNotation + "]");
			}
		}

		log.info("[{}] Add notation [{}]", this.title, notationToAdd.getNameIncludingNumberOfBells());
		notations.add(notationToAdd);
		Collections.sort(notations, NotationBody.BY_NAME);

		if (spliced == false && singleMethodActiveNotation == null) {
			singleMethodActiveNotation = notationToAdd;
			log.info("[{}] Set active notation [{}]", this.title, singleMethodActiveNotation.getNameIncludingNumberOfBells());
		}
	}

	@Override
	public void removeNotation(NotationBody notationForRemoval) {
		checkNotNull(notationForRemoval, "notationForRemoval must not be null");
		notations.remove(notationForRemoval);
		log.info("[{}] Remove notation [{}]", this.title, notationForRemoval.getNameIncludingNumberOfBells());

		// Sort out the next notation if it is the active notation
		if (notationForRemoval.equals(singleMethodActiveNotation)) {
			singleMethodActiveNotation = null;
			final List<NotationBody> validNotations = getValidNotations();
			for (NotationBody notation : validNotations) {
				if (notation.getName().compareTo(notationForRemoval.getName()) > 0) {
					singleMethodActiveNotation = notation;
					log.info("[{}] Set active notation [{}]", this.title, singleMethodActiveNotation.getNameIncludingNumberOfBells());
					break;
				}
			}
			if (singleMethodActiveNotation == null && validNotations.size() > 0) {
				singleMethodActiveNotation = validNotations.iterator().next();
				log.info("[{}] Set active notation [{}]", this.title, singleMethodActiveNotation.getNameIncludingNumberOfBells());
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
			if (singleMethodActiveNotation != null) {
				return Lists.<NotationBody>newArrayList(singleMethodActiveNotation);
			}
		}

		return Collections.emptyList();
	}


	@Override
	public NotationBody getSingleMethodActiveNotation() {
		return singleMethodActiveNotation;
	}

	@Override
	public void setSingleMethodActiveNotation(NotationBody singleMethodActiveNotation) {
		this.singleMethodActiveNotation = checkNotNull(singleMethodActiveNotation);
		log.info("[{}] Set single method active notation [{}]", this.title, singleMethodActiveNotation.getNameIncludingNumberOfBells());

		if (spliced == true) {
			spliced = false;
			log.info("[{}] Set spliced [{}]", this.title, this.spliced);
		}
	}

	@Override
	public boolean isSpliced() {
		return spliced;
	}

	@Override
	public void setSpliced(boolean spliced) {
		if (this.spliced != spliced) {
			this.spliced = spliced;
			log.info("[{}] Set spliced [{}]", this.title, this.spliced);

			if (spliced) {
				log.info("[{}] Set active notation [null]", this.title);
				singleMethodActiveNotation = null;
			} else {
				final List<NotationBody> validNotations = getValidNotations();

				if (validNotations.size() > 0) {
					singleMethodActiveNotation = validNotations.iterator().next();
					log.info("[{}] Set active notation [{}]", this.title, singleMethodActiveNotation.getNameIncludingNumberOfBells());
				}
			}
		}
	}

	@Override
	public String getPlainLeadToken() {
		return plainLeadToken;
	}

	@Override
	public void setPlainLeadToken(String plainLeadToken) {
		if (this.plainLeadToken != plainLeadToken) {
			this.plainLeadToken = checkNotNull(plainLeadToken);
			log.info("[{}] Set plain lead token [{}]", this.title, this.plainLeadToken);
		}
	}

	@Override
	public Set<TouchDefinition> getDefinitions() {
		return Sets.newHashSet(definitions.values());
	}

	@Override
	public TouchDefinition findDefinitionByName(String name) {
		return definitions.get(name);
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

		log.info("[{}] Add definition [{}]", this.title, definition);

		return definition;
	}

	@Override
	public void removeDefinition(String name) {
		definitions.remove(name);
	}

	@Override
	public MethodRow getStartChange() {
		return startChange;
	}

	@Override
	public void setStartChange(MethodRow startChange) {
		checkNotNull(startChange);
		checkState(startChange.getNumberOfBells() == numberOfBells);
		this.startChange = startChange;
		log.info("[{}] Set start change to [{}]", this.title, startChange);
	}

	@Override
	public int getStartAtRow() {
		return startAtRow;
	}

	@Override
	public void setStartAtRow(int startAtRow) {
		if (this.startAtRow != startAtRow) {
			checkState(startAtRow > 0, "Start at row must be greater than 0");
			checkState(startAtRow <= START_AT_ROW_MAX, "Start at row must be less than or equal to %s", START_AT_ROW_MAX);
			this.startAtRow = startAtRow;
			log.info("[{}] Set start at row to [{}]", this.title, startAtRow);
		}
	}

	@Override
	public Stroke getStartStroke() {
		return this.startStroke;
	}

	@Override
	public void setStartStroke(Stroke startStroke) {
		if (this.startStroke != startStroke) {
			this.startStroke = checkNotNull(startStroke);
			log.info("[{}] Set start stroke to [{}]", this.title, startStroke);
		}
	}

	@Override
	public Optional<NotationBody> getStartNotation() {
		return startNotation;
	}

	@Override
	public void setStartNotation(NotationBody startNotation) {
		checkNotNull(startNotation);
		checkState(startNotation.getNumberOfWorkingBells() == numberOfBells, "Start Notation number of bells must match touch number of bells");

		if (!this.startNotation.isPresent() ||
				!startNotation.getNotationDisplayString(false).equals(this.startNotation.get().getNotationDisplayString(false))) {
			this.startNotation = Optional.of(startNotation);
			log.info("[{}] Set start notation to [{}]", this.title, this.startNotation.get().getNotationDisplayString(false));
		}
	}

	@Override
	public void removeStartNotation() {
		if (this.startNotation.isPresent()) {
			this.startNotation = Optional.absent();
			log.info("[{}] Set start notation to [{}]", this.title, startNotation);
		}
	}

	@Override
	public int getTerminationMaxRows() {
		return terminationMaxRows;
	}

	@Override
	public void setTerminationMaxRows(int terminationMaxRows) {
		if (this.terminationMaxRows != terminationMaxRows) {
			checkState(terminationMaxRows > 0, "Termination max rows must be greater than 0");
			checkState(terminationMaxRows <= TERMINATION_MAX_ROWS_MAX, "Termination max rows must be less than or equal to %s", TERMINATION_MAX_ROWS_MAX);
			this.terminationMaxRows = terminationMaxRows;
			log.info("[{}] Set termination max rows to [{}]", this.title, this.terminationMaxRows);
		}
	}

	@Override
	public Optional<Integer> getTerminationMaxLeads() {
		return terminationMaxLeads;
	}

	@Override
	public void setTerminationMaxLeads(int terminationMaxLeads) {
		checkState(terminationMaxLeads > 0, "Termination max leads must be greater than 0");
		checkState(terminationMaxLeads <= TERMINATION_MAX_LEADS_MAX, "Termination max leads must be less than or equal to %s", TERMINATION_MAX_LEADS_MAX);
		this.terminationMaxLeads = Optional.of(terminationMaxLeads);
		log.info("[{}] Set termination max leads to [{}]", this.title, this.terminationMaxLeads);
	}

	@Override
	public void removeTerminationMaxLeads() {
		this.terminationMaxLeads = Optional.absent();
		log.info("[{}] Set termination max leads to [{}]", this.title, this.terminationMaxLeads);
	}
	
	@Override
	public Optional<Integer> getTerminationMaxParts() {
		return terminationMaxParts;
	}

	@Override
	public void setTerminationMaxParts(int terminationMaxParts) {
		checkState(terminationMaxParts > 0, "Termination max parts must be greater than 0");
		checkState(terminationMaxParts <= TERMINATION_MAX_PARTS_MAX, "Termination max parts must be less than or equal to %s", TERMINATION_MAX_PARTS_MAX);
		this.terminationMaxParts = Optional.of(terminationMaxParts);
		log.info("[{}] Set termination max parts to [{}]", this.title, this.terminationMaxParts);
	}

	@Override
	public void removeTerminationMaxParts() {
		this.terminationMaxParts = Optional.absent();
		log.info("[{}] Set termination max parts to [{}]", this.title, this.terminationMaxParts);
	}

	@Override
	public Optional<Integer> getTerminationCircularTouch() {
		return terminationCircularTouch;
	}

	@Override
	public void setTerminationCircularTouch(int terminationCircularTouch) {
		checkState(terminationCircularTouch > 0, "Termination max parts must be greater than 0");
		checkState(terminationCircularTouch <= TERMINATION_CIRCULAR_TOUCH_MAX, "Termination max parts must be less than or equal to %s", TERMINATION_CIRCULAR_TOUCH_MAX);
		this.terminationCircularTouch = Optional.of(terminationCircularTouch);
		log.info("[{}] Set termination circular touch to [{}]", this.title, this.terminationCircularTouch);
	}

	@Override
	public void removeTerminationCircularTouch() {
		this.terminationCircularTouch = Optional.absent();
		log.info("[{}] Set termination circular touch to [{}]", this.title, this.terminationCircularTouch);
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
		log.info("[{}] Set termination change to [{}]", this.title, this.terminationSpecificRow);
	}

	@Override
	public void removeTerminationSpecificRow() {
		terminationSpecificRow = Optional.absent();
		log.info("[{}] Set termination change to [{}]", this.title, this.terminationSpecificRow);
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
				", numberOfBells='" + numberOfBells + '\'' +
				", touchType=" + touchType +
				", callFromBell='" + callFromBell + '\'' +
				", notations=" + notations +
				", singleMethodActiveNotation=" + singleMethodActiveNotation +
				", spliced=" + spliced +
				", plainLeadToken='" + plainLeadToken + '\'' +
				", definitions=" + definitions +
				", startChange=" + startChange +
				", startAtRow=" + startAtRow +
				", startStroke=" + startStroke +
				", startNotation=" + startNotation +
				", terminationMaxRows=" + terminationMaxRows +
				", terminationMaxLeads=" + terminationMaxLeads +
				", terminationMaxParts=" + terminationMaxParts +
				", terminationCircularTouch=" + terminationCircularTouch +
				", terminationSpecificRow=" + terminationSpecificRow +
				", cells=" + cells +
				'}';
	}
}