package org.ringingmaster.engine.touch.container.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.Bell;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.Stroke;
import org.ringingmaster.engine.notation.NotationBody;
import org.ringingmaster.engine.grid.Grid;
import org.ringingmaster.engine.touch.container.Touch;
import org.ringingmaster.engine.touch.container.TouchCell;
import org.ringingmaster.engine.touch.container.TouchCheckingType;
import org.ringingmaster.engine.touch.container.TouchDefinition;
import org.ringingmaster.engine.touch.container.TouchElement;
import net.jcip.annotations.Immutable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable wrapper for a Touch
 * //TODO make the embedded items immutable
 * User: Stephen
 */
@Immutable
public class ImmutableTouch implements Touch {

	private final Touch touch;

	public ImmutableTouch(Touch touch) {
		this.touch = touch;
	}

	@Override
	public Touch clone() throws CloneNotSupportedException {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public String getTitle() {
		return touch.getTitle();
	}

	@Override
	public Mutated setTitle(String name) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public String getAuthor() {
		return touch.getAuthor();
	}

	@Override
	public Mutated setNumberOfBells(NumberOfBells numberOfBells) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated setAuthor(String author) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return touch.getNumberOfBells();
	}

	@Override
	public TouchCheckingType getTouchCheckingType() {
		return touch.getTouchCheckingType();
	}

	@Override
	public Bell getCallFromBell() {
		return touch.getCallFromBell();
	}

	@Override
	public Mutated setTouchCheckingType(TouchCheckingType touchCheckingType) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated setCallFromBell(Bell callFromBell) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated addNotation(NotationBody notationToAdd) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public List<String> checkAddNotation(NotationBody notationToAdd) {
		return touch.checkAddNotation(notationToAdd);
	}

	@Override
	public Mutated removeNotation(NotationBody notationForRemoval) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated updateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public List<String> checkUpdateNotation(NotationBody originalNotation, NotationBody replacementNotation) {
		return touch.checkUpdateNotation(originalNotation, replacementNotation);
	}

	@Override
	public List<NotationBody> getAllNotations() {
		return touch.getAllNotations();
	}

	@Override
	public List<NotationBody> getValidNotations() {
		return touch.getValidNotations();
	}

	@Override
	public List<NotationBody> getNotationsInUse() {
		return touch.getNotationsInUse();
	}

	@Override
	public Mutated setNonSplicedActiveNotation(NotationBody activeNotation) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public NotationBody getNonSplicedActiveNotation() {
		return touch.getNonSplicedActiveNotation();
	}

	@Override
	public TouchDefinition addDefinition(String shorthand, String characters) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Set<TouchDefinition> getDefinitions() {
		return touch.getDefinitions();
	}

	@Override
	public void removeDefinition(String name) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public TouchDefinition findDefinitionByShorthand(String name) {
		return touch.findDefinitionByShorthand(name);
	}

	@Override
	public boolean isSpliced() {
		return touch.isSpliced();
	}

	@Override
	public Mutated setSpliced(boolean spliced) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public TouchElement insertCharacter(int columnIndex, int rowIndex, int cellIndex, char character) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void addCharacters(int columnIndex, int rowIndex, String characters) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void removeCharacter(int columnIndex, int rowIndex, int cellIndex) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public TouchElement getElement(int columnIndex, int rowIndex, int cellIndex) {
		return touch.getElement(columnIndex, rowIndex, cellIndex);
	}

	@Override
	public String getPlainLeadToken() {
		return touch.getPlainLeadToken();
	}

	@Override
	public Mutated setPlainLeadToken(String plainLeadToken) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void resetParseData() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Grid<TouchCell> allCellsView() {
		return touch.allCellsView();
	}

	@Override
	public Grid<TouchCell> callPositionView() {
		return touch.callPositionView();
	}

	@Override
	public Grid<TouchCell> mainBodyView() {
		return touch.mainBodyView();
	}

	@Override
	public Grid<TouchCell> spliceView() {
		return touch.spliceView();
	}

	@Override
	public int getColumnCount() {
		return touch.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return touch.getRowCount();
	}

	@Override
	public void setColumnCount(int columnCount) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void incrementColumnCount() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void incrementRowCount() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public MethodRow getStartChange() {
		return touch.getStartChange();
	}

	@Override
	public Mutated setStartChange(MethodRow startChange) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public int getStartAtRow() {
		return touch.getStartAtRow();
	}

	@Override
	public Mutated setStartAtRow(int startAtRow) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Stroke getStartStroke() {
		return touch.getStartStroke();
	}

	@Override
	public Optional<NotationBody> getStartNotation() {
		return touch.getStartNotation();
	}

	@Override
	public Mutated setStartNotation(NotationBody startNotation) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated removeStartNotation() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated setStartStroke(Stroke startStroke) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void setRowCount(int rowCount) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<Integer> getTerminationMaxLeads() {
		return touch.getTerminationMaxLeads();
	}

	@Override
	public Mutated setTerminationMaxLeads(int terminationMaxLeads) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated removeTerminationMaxLeads() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<Integer> getTerminationMaxParts() {
		return touch.getTerminationMaxParts();
	}

	@Override
	public Mutated setTerminationMaxParts(int terminationMaxParts) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated removeTerminationMaxParts() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<Integer> getTerminationMaxCircularTouch() {
		return touch.getTerminationMaxCircularTouch();
	}

	@Override
	public Mutated setTerminationMaxCircularTouch(int terminationCircularTouch) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated removeTerminationMaxCircularTouch() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public int getTerminationMaxRows() {
		return touch.getTerminationMaxRows();
	}

	@Override
	public Mutated setTerminationMaxRows(int terminationMaxRows) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<MethodRow> getTerminationChange() {
		return touch.getTerminationChange();
	}

	@Override
	public Mutated setTerminationChange(MethodRow terminationChange) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Mutated removeTerminationChange() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public boolean collapseEmptyRowsAndColumns() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

}
