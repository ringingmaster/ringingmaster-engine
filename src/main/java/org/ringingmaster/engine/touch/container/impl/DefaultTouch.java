package org.ringingmaster.engine.touch.container.impl;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.jcip.annotations.NotThreadSafe;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.grid.Grid;
import org.ringingmaster.engine.grid.GridCellFactory;
import org.ringingmaster.engine.grid.impl.DefaultGrid;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.notation.impl.NotationBuilder;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.touch.container.TouchDefinition;
import org.ringingmaster.engine.touch.container.TouchElement;
import org.ringingmaster.engine.touch.newcontainer.checkingtype.CheckingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;
import static org.ringingmaster.engine.touch.container.Touch.Mutated.MUTATED;
import static org.ringingmaster.engine.touch.container.Touch.Mutated.UNCHANGED;

/**
 * Default implementation of {@link Touch}
 *
 * User: Stephen
 */
@NotThreadSafe
@Deprecated
public class DefaultTouch implements Touch {


	private final Logger log = LoggerFactory.getLogger(this.getClass());

	//IMPORTANT NOTE: When adding items here, think about the clone method, immutability, and toString.
	private String title;
	private String author;

	private NumberOfBells numberOfBells;
	private CheckingType checkingType;

	private Bell callFromBell;
	private final List<NotationBody> sortedNotations;
	private NotationBody nonSplicedActiveNotation;
	private boolean spliced; // we use separate spliced and active-notation, rather than an optional because otherwise, adding your first notation will always be spliced.
	private String plainLeadToken;
	private SortedMap<String, TouchDefinition> definitions;

	private MethodRow startChange;
	private int startAtRow;
	private Stroke startStroke;
	private Optional<NotationBody> startNotation;

	private int terminationMaxRows;
	private Optional<Integer> terminationMaxLeads;
	private Optional<Integer> terminationMaxParts;
	private Optional<Integer> terminationMaxCircularTouch;
	private Optional<MethodRow> terminationChange;

	private final Grid<TouchCell> cells;

	private static GridCellFactory<TouchCell> FACTORY = (columnIndex, rowIndex) -> new DefaultTouchCell();

	DefaultTouch() {
		title = "";
		author = "";

		numberOfBells = NumberOfBells.BELLS_6;
		checkingType = CheckingType.COURSE_BASED;

		callFromBell = numberOfBells.getTenor();
		sortedNotations = new ArrayList<>();
		spliced = false;
		plainLeadToken = "p";
		definitions = new TreeMap<>();

		startChange = MethodBuilder.buildRoundsRow(numberOfBells);
		startAtRow = 0;
		startStroke = Stroke.BACKSTROKE;
		startNotation = Optional.empty();

		terminationMaxRows = TERMINATION_MAX_ROWS_INITIAL_VALUE;
		terminationMaxLeads = Optional.empty();
		terminationMaxParts = Optional.empty();
		terminationMaxCircularTouch = Optional.of(TERMINATION_CIRCULAR_TOUCH_INITIAL_VALUE);
		terminationChange = Optional.empty();

		cells = new DefaultGrid<>(FACTORY, 1, 1);
	}

	@Override
	public Touch clone() throws CloneNotSupportedException {
		DefaultTouch touchClone = new DefaultTouch();

		touchClone.title = this.title;
		touchClone.author = this.author;

		touchClone.numberOfBells = numberOfBells;
		touchClone.checkingType = this.checkingType;

		touchClone.callFromBell = callFromBell;
		touchClone.sortedNotations.addAll(this.sortedNotations);
		touchClone.nonSplicedActiveNotation = this.nonSplicedActiveNotation;
		touchClone.spliced = this.spliced;
		touchClone.plainLeadToken = this.plainLeadToken;
		for (TouchDefinition definition : definitions.values()) {
			touchClone.definitions.put(definition.getShorthand(), definition.clone());
		}

		touchClone.startChange = this.startChange;
		touchClone.startAtRow = this.startAtRow;
		touchClone.startStroke = this.startStroke;
		touchClone.startNotation = this.startNotation;

		touchClone.terminationMaxRows = this.terminationMaxRows;
		touchClone.terminationMaxLeads = this.terminationMaxLeads;
		touchClone.terminationMaxParts = this.terminationMaxParts;
		touchClone.terminationMaxCircularTouch = this.terminationMaxCircularTouch;
		touchClone.terminationChange = this.terminationChange;

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
	public Mutated setTitle(String title) {
		if (!this.title.equals(title)) {
			this.title = checkNotNull(title);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public Mutated setAuthor(String author) {
		if (!this.author.equals(author)) {
			this.author = checkNotNull(author);
			log.debug("[{}] Set author [{}]", this.title, this.author);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return numberOfBells;
	}

	@Override
	public Mutated setNumberOfBells(NumberOfBells numberOfBells) {
		// NOTE: When modifying this method, also modify the error string that
		// is generated by the TouchDocument.setNumberOfBells()
		if (this.numberOfBells != numberOfBells) {
			this.numberOfBells = checkNotNull(numberOfBells);
			log.debug("[{}] Set number of bells [{}]", this.title, this.numberOfBells);

			if (callFromBell.getZeroBasedBell() > numberOfBells.getTenor().getZeroBasedBell()) {
				callFromBell = numberOfBells.getTenor();
			}

			final MethodRow existingStartChange = getStartChange();
			final MethodRow newStartChange = MethodBuilder.transformToNewNumberOfBells(existingStartChange, numberOfBells);
			setStartChange(newStartChange);

			if (terminationChange.isPresent()) {
				final MethodRow existingTerminationRow = getTerminationChange().get();
				final MethodRow newTerminationRow = MethodBuilder.transformToNewNumberOfBells(existingTerminationRow, numberOfBells);
				setTerminationChange(newTerminationRow);
			}

			if (!isSpliced() &&
					nonSplicedActiveNotation != null &&
					nonSplicedActiveNotation.getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
				findNextBestNonSplicedActiveNotation(nonSplicedActiveNotation);
			}

			if (startNotation.isPresent()) {
				final String originalNotation = startNotation.get().getNotationDisplayString(false);
				NotationBody builtNotation = NotationBuilder.getInstance()
						.setNumberOfWorkingBells(numberOfBells)
						.setUnfoldedNotationShorthand(originalNotation)
						.build();
				if (!originalNotation.equals(builtNotation.getNotationDisplayString(false))) {
					if (builtNotation.getRowCount() == 0) {
						startNotation = Optional.empty();
						log.debug("[{}] Set start notation to [{}]", this.title, this.startNotation);
					} else {
						startNotation = Optional.of(builtNotation);
						log.debug("[{}] Set start notation to [{}]", this.title, this.startNotation.get().getNotationDisplayString(false));
					}
				}
			}
			return MUTATED;
		}
		// TODO validation of other items. e.g. methods Etc.
		return UNCHANGED;
	}

	@Override
	public CheckingType getCheckingType() {
		return checkingType;
	}

	@Override
	public Mutated setTouchCheckingType(CheckingType checkingType) {
		if (this.checkingType != checkingType) {
			this.checkingType = checkNotNull(checkingType);
			log.debug("[{}] Set touch type [{}]", this.title, this.checkingType.getName());
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Bell getCallFromBell() {
		return callFromBell;
	}

	@Override
	public Mutated setCallFromBell(Bell callFromBell) {
		if (this.callFromBell != callFromBell) {
			checkNotNull(callFromBell);
			checkState(callFromBell.getZeroBasedBell() < numberOfBells.toInt());
			this.callFromBell = callFromBell;
			log.debug("[{}] Set call from bell to [{}]", this.title, this.callFromBell);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Mutated addNotation(NotationBody notationToAdd) {
		checkNotNull(notationToAdd, "notation must not be null");

		List<String> messages = checkAddNotation(notationToAdd);

		if (messages.size() > 0) {
			String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
			throw new IllegalArgumentException("Can't add notation [" + notationToAdd + "]: " + System.lineSeparator() + message);
		}

		log.debug("[{}] Add notation [{}]", this.title, notationToAdd.getNameIncludingNumberOfBells());
		sortedNotations.add(notationToAdd);
		Collections.sort(sortedNotations, NotationBody.BY_NAME);
//TODO what if the number of bella is wrong?
		if (spliced == false && nonSplicedActiveNotation == null) {
			nonSplicedActiveNotation = notationToAdd;
			log.debug("[{}] Set active notation [{}]", this.title, nonSplicedActiveNotation.getNameIncludingNumberOfBells());
		}
		return MUTATED;
	}

	public List<String> checkAddNotation(NotationBody notationToAdd) {
		return checkPotentialNewNotation(notationToAdd, Collections.emptySet());
	}

	private List<String> checkPotentialNewNotation(NotationBody notationToCheck, Set<NotationBody> notationsToExclude) {
		List<String> messages = new ArrayList<>();
		Set<NotationBody> allNotationsWithExclusions = new HashSet<>(getAllNotations());
		allNotationsWithExclusions.removeAll(notationsToExclude);

		messages.addAll(allNotationsWithExclusions.stream()
				.filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
						(Objects.equal(existingNotation.getName(), notationToCheck.getName())))
				.map(existingNotation -> "An existing method with notation '" + existingNotation.getNotationDisplayString(true) + "' has the same Name and Number Of Bells.")
				.collect(Collectors.toList()));

		messages.addAll(allNotationsWithExclusions.stream()
				.filter(existingNotation -> (!Strings.isNullOrEmpty(notationToCheck.getSpliceIdentifier()) &&
						Objects.equal(existingNotation.getSpliceIdentifier(), notationToCheck.getSpliceIdentifier())))
				.map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Splice Identifier.")
				.collect(Collectors.toList()));

		messages.addAll(allNotationsWithExclusions.stream()
				.filter(existingNotation -> (existingNotation.getNumberOfWorkingBells() == notationToCheck.getNumberOfWorkingBells()) &&
						Objects.equal(existingNotation.getNotationDisplayString(true), notationToCheck.getNotationDisplayString(true)))
				.map(existingNotation -> "An existing method '" + existingNotation.getNameIncludingNumberOfBells() + "' has the same Notation '" + notationToCheck.getNotationDisplayString(false) + "'.")
				.collect(Collectors.toList()));
		return messages;
	}

	@Override
	public Mutated removeNotation(NotationBody notationForRemoval) {
		checkNotNull(notationForRemoval, "notationForRemoval must not be null");
		checkState(sortedNotations.contains(notationForRemoval));

		sortedNotations.remove(notationForRemoval);
		log.info("[{}] Remove notation [{}]", this.title, notationForRemoval.getNameIncludingNumberOfBells());

		// Sort out the next notation if it is the active notation
		if (notationForRemoval.equals(nonSplicedActiveNotation)) {
			findNextBestNonSplicedActiveNotation(notationForRemoval);
		}
		return MUTATED;
	}

	private void findNextBestNonSplicedActiveNotation(NotationBody previousNotation) {
		final List<NotationBody> validNotations = getValidNotations();
		validNotations.remove(previousNotation);

		Comparator<NumberOfBells> byDistanceFromPassedNumberOfBells = (o1, o2) -> ComparisonChain.start()
				.compare(Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o1.toInt()),
						 Math.abs(previousNotation.getNumberOfWorkingBells().toInt() - o2.toInt()))
				.compare(o2.toInt(), o1.toInt()) // always take higher number of bells where distance is equal
				.result();

		// from the validNotations, find all number of bells in use, sorted by distance from passed number of bells.
		Optional<NumberOfBells> bestNumberOfBells = validNotations.stream()
				.map(Notation::getNumberOfWorkingBells)
				.sorted(byDistanceFromPassedNumberOfBells)
				.findFirst();

		if (!bestNumberOfBells.isPresent()) {
			nonSplicedActiveNotation = null;
			log.debug("[{}] Set active notation [null]", this.title);
		}

		// Try notations that are lexicographically the same or higher
		Optional<NotationBody> lexicographicallyHigher = validNotations.stream()
				.filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
				.filter(notation -> notation.getName().compareTo(previousNotation.getName()) >= 0)
				.sorted(Notation.BY_NAME)
				.findFirst();
		if (lexicographicallyHigher.isPresent()) {
			nonSplicedActiveNotation = lexicographicallyHigher.get();
			log.debug("[{}] Set active notation [{}]", this.title, nonSplicedActiveNotation.getNameIncludingNumberOfBells());
			return;
		}

		// Try notations that are lexicographically the same or higher
		Optional<NotationBody> lexicographicallyLower = validNotations.stream()
				.filter(notation -> notation.getNumberOfWorkingBells() == bestNumberOfBells.get())
				.filter(notation -> notation.getName().compareTo(previousNotation.getName()) < 0)
				.sorted(Notation.BY_NAME.reversed())
				.findFirst();

		if (lexicographicallyLower.isPresent()) {
			nonSplicedActiveNotation = lexicographicallyLower.get();
			log.debug("[{}] Set active notation [{}]", this.title, nonSplicedActiveNotation.getNameIncludingNumberOfBells());
			return;
		}

	}

	@Override
	public Mutated updateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
		checkNotNull(originalNotation, "originalNotation must not be null");
		checkNotNull(replacementNotation, "replacementNotation must not be null");
		checkState(sortedNotations.contains(originalNotation));
		checkState(originalNotation != replacementNotation);

		List<String> messages = checkUpdateNotation(originalNotation, replacementNotation);

		if (messages.size() > 0) {
			String message = messages.stream().collect(Collectors.joining(System.lineSeparator()));
			throw new IllegalArgumentException("Can't update notation [" + replacementNotation + "]: " + System.lineSeparator() + message);
		}
		log.info("[{}] update notation [{}] with [{}]", this.title, originalNotation.getNameIncludingNumberOfBells(), replacementNotation.getNameIncludingNumberOfBells());


		sortedNotations.remove(originalNotation);
		sortedNotations.add(replacementNotation);
		Collections.sort(sortedNotations, NotationBody.BY_NAME);

		if (nonSplicedActiveNotation == originalNotation) {
			nonSplicedActiveNotation = replacementNotation;
			if (!isSpliced() &&
				nonSplicedActiveNotation.getNumberOfWorkingBells().toInt() > numberOfBells.toInt()) {
				findNextBestNonSplicedActiveNotation(nonSplicedActiveNotation);
			}
		}

		return MUTATED;
	}

	@Override
	public List<String> checkUpdateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
		return checkPotentialNewNotation(replacementNotation, Sets.<NotationBody>newHashSet(originalNotation));
	}

	@Override
	public List<NotationBody> getAllNotations() {
		return Collections.unmodifiableList(sortedNotations);
	}

	@Override
	public List<NotationBody> getValidNotations() {
		return null;//TODO NotationBuilderHelper.filterNotationsUptoNumberOfBells(sortedNotations, numberOfBells);
	}

	@Override
	public List<NotationBody> getNotationsInUse() {
		if (isSpliced()) {
			return getValidNotations();
		}
		else {
			// Not Spliced
			if (nonSplicedActiveNotation != null) {
				return Lists.<NotationBody>newArrayList(nonSplicedActiveNotation);
			}
		}

		return Collections.emptyList();
	}


	@Override
	public NotationBody getNonSplicedActiveNotation() {
		return nonSplicedActiveNotation;
	}

	@Override
	public Mutated setNonSplicedActiveNotation(NotationBody nonSplicedActiveNotation) {
		checkNotNull(nonSplicedActiveNotation);
		checkState(sortedNotations.contains(nonSplicedActiveNotation), "Can't set NonSplicedActiveNotation to notation not part of touch.");

		if (this.nonSplicedActiveNotation != nonSplicedActiveNotation) {
			this.nonSplicedActiveNotation = nonSplicedActiveNotation;
			log.debug("[{}] Set single method active notation [{}]", this.title, nonSplicedActiveNotation.getNameIncludingNumberOfBells());

			if (spliced == true) {
				spliced = false;
				log.debug("[{}] Set spliced [{}]", this.title, this.spliced);
			}
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public boolean isSpliced() {
		return spliced;
	}

	@Override
	public Mutated setSpliced(boolean spliced) {
		if (this.spliced != spliced) {
			this.spliced = spliced;
			log.debug("[{}] Set spliced [{}]", this.title, this.spliced);

			if (spliced) {
				log.debug("[{}] Set active notation [null]", this.title);
				nonSplicedActiveNotation = null;
			} else {
				final List<NotationBody> validNotations = getValidNotations();

				if (validNotations.size() > 0) {
					nonSplicedActiveNotation = validNotations.iterator().next();
					log.debug("[{}] Set active notation [{}]", this.title, nonSplicedActiveNotation.getNameIncludingNumberOfBells());
				}
			}
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public String getPlainLeadToken() {
		return plainLeadToken;
	}

	@Override
	public Mutated setPlainLeadToken(String plainLeadToken) {
		if (this.plainLeadToken != plainLeadToken) {
			this.plainLeadToken = checkNotNull(plainLeadToken);
			log.debug("[{}] Set plain lead token [{}]", this.title, this.plainLeadToken);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Set<TouchDefinition> getDefinitions() {
		return Sets.newHashSet(definitions.values());
	}

	@Override
	public TouchDefinition findDefinitionByShorthand(String shorthand) {
		return definitions.get(shorthand);
	}

	@Override
	public TouchDefinition addDefinition(String shorthand, String characters) {
		checkNotNull(shorthand, "shorthand must not be null");
		checkNotNull(shorthand.length() > 0, "shorthand must contain some characters");
		// Check duplicate name
		if (definitions.get(shorthand) != null) {
			throw new IllegalArgumentException("Can't add definition [" + shorthand + "] as it has a duplicate shorthand to existing definition [" + definitions.get(shorthand) + "]");
		}

		TouchDefinition definition = new DefaultTouchDefinition(shorthand);
		definition.add(characters);
		definitions.put(definition.getShorthand(), definition);

		log.debug("[{}] Add definition [{}]", this.title, definition);

		return definition;
	}

	@Override
	public void removeDefinition(String shorthand) {
		definitions.remove(shorthand);
	}

	@Override
	public MethodRow getStartChange() {
		return startChange;
	}

	@Override
	public Mutated setStartChange(MethodRow startChange) {
		checkNotNull(startChange);
		checkState(startChange.getNumberOfBells() == numberOfBells);
		if (!this.startChange.equals(startChange)) {
			this.startChange = startChange;
			log.debug("[{}] Set start change to [{}]", this.title, startChange);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public int getStartAtRow() {
		return startAtRow;
	}

	@Override
	public Mutated setStartAtRow(int startAtRow) {
		if (this.startAtRow != startAtRow) {
			checkState(startAtRow > 0, "Start at row must be greater than 0");
			checkState(startAtRow <= START_AT_ROW_MAX, "Start at row must be less than or equal to %s", START_AT_ROW_MAX);
			this.startAtRow = startAtRow;
			log.debug("[{}] Set start at row to [{}]", this.title, startAtRow);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Stroke getStartStroke() {
		return this.startStroke;
	}

	@Override
	public Mutated setStartStroke(Stroke startStroke) {
		if (this.startStroke != startStroke) {
			this.startStroke = checkNotNull(startStroke);
			log.debug("[{}] Set start stroke to [{}]", this.title, startStroke);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Optional<NotationBody> getStartNotation() {
		return startNotation;
	}

	@Override
	public Mutated setStartNotation(NotationBody startNotation) {
		checkNotNull(startNotation);
		checkState(startNotation.getNumberOfWorkingBells() == numberOfBells, "Start Notation number of bells must match touch number of bells");

		if (!this.startNotation.isPresent() ||
				!startNotation.getNotationDisplayString(false).equals(this.startNotation.get().getNotationDisplayString(false))) {
			this.startNotation = Optional.of(startNotation);
			log.debug("[{}] Set start notation to [{}]", this.title, this.startNotation.get().getNotationDisplayString(false));
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Mutated removeStartNotation() {
		if (this.startNotation.isPresent()) {
			this.startNotation = Optional.empty();
			log.debug("[{}] Set start notation to [{}]", this.title, startNotation);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public int getTerminationMaxRows() {
		return terminationMaxRows;
	}

	@Override
	public Mutated setTerminationMaxRows(int terminationMaxRows) {
		if (this.terminationMaxRows != terminationMaxRows) {
			checkState(terminationMaxRows > 0, "Termination max rows must be greater than 0");
			checkState(terminationMaxRows <= TERMINATION_MAX_ROWS_MAX, "Termination max rows must be less than or equal to %s", TERMINATION_MAX_ROWS_MAX);
			this.terminationMaxRows = terminationMaxRows;
			log.debug("[{}] Set termination max rows to [{}]", this.title, this.terminationMaxRows);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Optional<Integer> getTerminationMaxLeads() {
		return terminationMaxLeads;
	}

	@Override
	public Mutated setTerminationMaxLeads(int terminationMaxLeads) {
		checkState(terminationMaxLeads > 0, "Termination max leads must be greater than 0");
		checkState(terminationMaxLeads <= TERMINATION_MAX_LEADS_MAX, "Termination max leads must be less than or equal to %s", TERMINATION_MAX_LEADS_MAX);

		Optional<Integer> optionalTerminationMaxLeads = Optional.of(terminationMaxLeads);
		if (!this.terminationMaxLeads.equals(optionalTerminationMaxLeads)) {
			this.terminationMaxLeads = optionalTerminationMaxLeads;
			log.debug("[{}] Set termination max leads to [{}]", this.title, this.terminationMaxLeads);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Mutated removeTerminationMaxLeads() {
		if (!this.terminationMaxLeads.equals(Optional.<Integer>empty())) {
			this.terminationMaxLeads = Optional.empty();
			log.debug("[{}] Set termination max leads to [{}]", this.title, this.terminationMaxLeads);
			return MUTATED;
		}
		return UNCHANGED;
	}
	
	@Override
	public Optional<Integer> getTerminationMaxParts() {
		return terminationMaxParts;
	}

	@Override
	public Mutated setTerminationMaxParts(int terminationMaxParts) {
		checkState(terminationMaxParts > 0, "Termination max parts must be greater than 0");
		checkState(terminationMaxParts <= TERMINATION_MAX_PARTS_MAX, "Termination max parts must be less than or equal to %s", TERMINATION_MAX_PARTS_MAX);

		Optional<Integer> optionalTerminationMaxParts = Optional.of(terminationMaxParts);
		if (!this.terminationMaxParts.equals(optionalTerminationMaxParts)) {
			this.terminationMaxParts = Optional.of(terminationMaxParts);
			log.debug("[{}] Set termination max parts to [{}]", this.title, this.terminationMaxParts);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Mutated removeTerminationMaxParts() {
		if (!this.terminationMaxParts.equals(Optional.<Integer>empty())) {
			this.terminationMaxParts = Optional.empty();
			log.debug("[{}] Set termination max parts to [{}]", this.title, this.terminationMaxParts);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Optional<Integer> getTerminationMaxCircularTouch() {
		return terminationMaxCircularTouch;
	}

	@Override
	public Mutated setTerminationMaxCircularTouch(int terminationMaxCircularTouch) {
		checkState(terminationMaxCircularTouch > 0, "Termination circular touch must be greater than 0");
		checkState(terminationMaxCircularTouch <= TERMINATION_CIRCULAR_TOUCH_MAX, "Termination circular touch must be less than or equal to %s", TERMINATION_CIRCULAR_TOUCH_MAX);

		Optional<Integer> optionalTerminationMaxCircularTouch = Optional.of(terminationMaxCircularTouch);
		if (!this.terminationMaxCircularTouch.equals(optionalTerminationMaxCircularTouch)) {
			this.terminationMaxCircularTouch = Optional.of(terminationMaxCircularTouch);
			log.debug("[{}] Set termination circular touch to [{}]", this.title, this.terminationMaxCircularTouch);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Mutated removeTerminationMaxCircularTouch() {
		if (!this.terminationMaxCircularTouch.equals(Optional.<Integer>empty())) {
			this.terminationMaxCircularTouch = Optional.empty();
			log.debug("[{}] Set termination circular touch to [{}]", this.title, this.terminationMaxCircularTouch);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Optional<MethodRow> getTerminationChange() {
		return terminationChange;
	}

	@Override
	public Mutated setTerminationChange(MethodRow terminationChange) {
		checkNotNull(terminationChange, "terminationChange cant be null");
		checkArgument(terminationChange.getNumberOfBells().equals(numberOfBells));
		Optional<MethodRow> optionalTerminationChange = Optional.of(terminationChange);
		if (!this.terminationChange.equals(optionalTerminationChange)) {
			this.terminationChange = Optional.of(terminationChange);
			log.debug("[{}] Set termination change to [{}]", this.title, this.terminationChange);
			return MUTATED;
		}
		return UNCHANGED;
	}

	@Override
	public Mutated removeTerminationChange() {
		if (!terminationChange.equals(Optional.<MethodRow>empty())) {
			terminationChange = Optional.empty();
			log.debug("[{}] Set termination change to [{}]", this.title, this.terminationChange);
			return MUTATED;
		}
		return UNCHANGED;
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
		for (TouchDefinition definition : definitions.values()) {
			definition.resetParseData();
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
				(getCheckingType() == CheckingType.COURSE_BASED ? 1 : 0));
	}

	@Override
	public Grid<TouchCell> mainBodyView() {
		return cells.unmodifiableSubGrid(
				0,
				(isSpliced() ? (cells.getColumnCount() - 1) : cells.getColumnCount()),
				(getCheckingType() == CheckingType.COURSE_BASED ? 1 : 0),
				cells.getRowCount());
	}

	@Override
	public Grid<TouchCell> spliceView() {
		return cells.unmodifiableSubGrid(
				(isSpliced() ? (cells.getColumnCount() - 1) : cells.getColumnCount()),
				cells.getColumnCount(),
				(getCheckingType() == CheckingType.COURSE_BASED ? 1 : 0),
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
				", touchType=" + checkingType +
				", callFromBell='" + callFromBell + '\'' +
				", sortedNotations=" + sortedNotations +
				", nonSplicedActiveNotation=" + nonSplicedActiveNotation +
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
				", terminationMaxCircularTouch=" + terminationMaxCircularTouch +
				", terminationChange=" + terminationChange +
				", cells=" + cells +
				'}';
	}
}