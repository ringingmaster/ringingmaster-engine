package com.concurrentperformance.ringingmaster.engine.touch.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.Bell;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.touch.Grid;
import com.concurrentperformance.ringingmaster.engine.touch.Touch;
import com.concurrentperformance.ringingmaster.engine.touch.TouchCell;
import com.concurrentperformance.ringingmaster.engine.touch.TouchDefinition;
import com.concurrentperformance.ringingmaster.engine.touch.TouchElement;
import com.concurrentperformance.ringingmaster.engine.touch.TouchType;
import com.google.common.base.Optional;
import net.jcip.annotations.Immutable;

import java.util.List;
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
	public TouchType getTouchType() {
		return touch.getTouchType();
	}

	@Override
	public void setTouchType(TouchType touchType) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public String setNumberOfBells(NumberOfBells numberOfBells) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public NumberOfBells getNumberOfBells() {
		return touch.getNumberOfBells();
	}

	@Override
	public Bell getCallFromBell() {
		return touch.getCallFromBell();
	}

	@Override
	public void setCallFromBell(Bell callFromBell) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void addNotation(NotationBody notationToAdd) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void removeNotation(NotationBody notationForRemoval) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public List<NotationBody> getAllNotations() {
		return touch.getAllNotations();
	}

	@Override
	public List<NotationBody> getNotationsInUse() {
		return touch.getNotationsInUse();
	}

	@Override
	public void setActiveNotation(NotationBody activeNotation) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public NotationBody getSingleMethodActiveNotation() {
		return touch.getSingleMethodActiveNotation();
	}

	@Override
	public TouchDefinition addDefinition(String name, String characters) {
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
	public TouchDefinition findDefinitionByName(String name) {
		return touch.findDefinitionByName(name);
	}

	@Override
	public boolean isSpliced() {
		return touch.isSpliced();
	}

	@Override
	public void setSpliced(boolean spliced) {
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
	public void setPlainLeadToken(String plainLeadToken) {
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
	public void setRowCount(int rowCount) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<Integer> getTerminationMaxLeads() {
		return touch.getTerminationMaxLeads();
	}

	@Override
	public void setTerminationMaxLeads(int terminationMaxLeads) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void removeTerminationMaxLeads() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<Integer> getTerminationMaxRows() {
		return touch.getTerminationMaxRows();
	}

	@Override
	public void setTerminationMaxRows(int terminationMaxRows) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void removeTerminationMaxRows() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public Optional<MethodRow> getTerminationSpecificRow() {
		return touch.getTerminationSpecificRow();
	}

	@Override
	public void setTerminationSpecificRow(MethodRow terminationSpecificRow) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public void removeTerminationSpecificRow() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public boolean collapseEmptyRowsAndColumns() {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public String getTitle() {
		return touch.getTitle();
	}

	@Override
	public void setTitle(String name) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}

	@Override
	public String getAuthor() {
		return touch.getAuthor();
	}

	@Override
	public void setAuthor(String author) {
		throw new UnsupportedOperationException("ImmutableTouch does not support this operation");
	}
}
