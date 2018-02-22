package org.ringingmaster.engine.compiler.impl;

import com.google.common.collect.Lists;
import org.ringingmaster.engine.touch.Touch;
import org.ringingmaster.engine.touch.cell.Cell;
import org.ringingmaster.engine.touch.variance.Variance;
import org.ringingmaster.engine.touch.variance.impl.NullVariance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO comments???
 * User: Stephen
 */
@NotThreadSafe
public abstract class SkeletalCallDecomposer<DC extends DecomposedCall> {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Touch touch;
	private final String logPreamble;
	private final Deque<CallGroup> callFIFO = new ArrayDeque<>();
	private Variance currentVariance = NullVariance.INSTANCE;

	public SkeletalCallDecomposer(Touch touch, String logPreamble) {
		this.touch = touch;
		this.logPreamble = logPreamble;
	}

	List<DC> createCallSequence() {
		//TODO
		return Lists.newArrayList();
//		log.debug("{} > create call sequence", logPreamble);
//		callFIFO.clear();
//		callFIFO.addFirst(new CallGroup(1));
//
//		preGenerate(touch);
//
//		Grid<TouchCell> mainBodyCells = touch.mainBodyView();
//		for (int rowIndex=0;rowIndex<mainBodyCells.getRowCount();rowIndex++) {
//			for (int columnIndex=0;columnIndex<mainBodyCells.getColumnCount();columnIndex++) {
//				TouchCell cell = mainBodyCells.getCell(columnIndex, rowIndex);
//				generateCallInstancesForCell(cell, columnIndex);
//			}
//		}
//
//		checkState(callFIFO.size() == 1);
//		log.debug("{} < create call sequence {}", logPreamble, callFIFO.getFirst());
//		return Collections.unmodifiableList(callFIFO.removeFirst());
	}

	protected abstract void preGenerate(Touch touch);

	private void generateCallInstancesForCell(Cell cell, int columnIndex) {
		//TODO
//		final List<TouchWord> words = cell.words();
//		for (TouchWord word : words) {
//			switch (word.getFirstParseType()) {
//				case CALL:
//				case CALL_MULTIPLIER:
//					decomposeWord(word, columnIndex, CALL, CALL_MULTIPLIER);
//					break;
//				case PLAIN_LEAD:
//				case PLAIN_LEAD_MULTIPLIER:
//					decomposeWord(word, columnIndex, PLAIN_LEAD, PLAIN_LEAD_MULTIPLIER);
//					break;
//				case MULTIPLIER_GROUP_OPEN:
//				case MULTIPLIER_GROUP_OPEN_MULTIPLIER:
//					openGroup(word);
//					break;
//				case MULTIPLIER_GROUP_CLOSE:
//					closeGroup();
//					break;
//				case VARIANCE_OPEN:
//					openVariance(word);
//					break;
//				case VARIANCE_CLOSE:
//					closeVariance(word);
//					break;
//				case DEFINITION:
//					insertDefinition(word, columnIndex);
//					break;
//			}
//		}
	}

//	private void decomposeWord(TouchWord word, int columnIndex,
//	                           ParseType parseType,ParseType multiplierParseType) {
//		MultiplierAndCall multiplierAndCall = getMultiplierAndCall(word, parseType, multiplierParseType);
//
//		log.debug("{}  - Adding call [{}] with multiplier [{}] to group level [{}]",
//				logPreamble, multiplierAndCall.getCallName(), multiplierAndCall.getMultiplier(), callFIFO.size());
//		if (multiplierAndCall.getCallName().length() > 0 ) {
//			for (int i=0;i<multiplierAndCall.getMultiplier();i++) {
//				DC decomposedCall = buildDecomposedCall(multiplierAndCall.getCallName(), multiplierAndCall.getVariance(), columnIndex, parseType);
//				callFIFO.peekFirst().add(decomposedCall);
//			}
//		}
//	}
//
//	protected abstract DC buildDecomposedCall(String callName, Variance variance, int columnIndex, ParseType parseType);
//
//	MultiplierAndCall getMultiplierAndCall(TouchWord word,
//	                                       ParseType parseType,ParseType multiplierParseType) {
//		List<TouchElement> elementsInWord = word.getElements();
//		StringBuilder parseTypeBuff = new StringBuilder(elementsInWord.size());
//		StringBuilder multiplierBuff = new StringBuilder(elementsInWord.size());
//		boolean finishedMultiplier = false;
//		for (TouchElement element : elementsInWord) {
//			if (!finishedMultiplier && element.getParseType().equals(multiplierParseType)) {
//				multiplierBuff.append(element.getCharacter());
//			}
//			else if (element.getParseType().equals(parseType)) {
//				parseTypeBuff.append(element.getCharacter());
//				finishedMultiplier = true;
//			}
//		}
//		int multiplier = 1;
//		if (multiplierBuff.length() > 0) {
//			String multiplierString = multiplierBuff.toString();
//			multiplier = Integer.parseInt(multiplierString);
//		}
//
//		return new MultiplierAndCall(multiplier, parseTypeBuff.toString(), currentVariance);
//	}
//
//	private void openMultiplierGroup(TouchWord word) {
//		MultiplierAndCall multiplierAndCall = getMultiplierAndCall(word, ParseType.MULTIPLIER_GROUP_OPEN, ParseType.MULTIPLIER_GROUP_OPEN_MULTIPLIER);
//		log.debug("Open Group level [{}] with multiplier [{}]", (callFIFO.size() + 1), multiplierAndCall.getMultiplier());
//		callFIFO.addFirst(new CallGroup(multiplierAndCall.getMultiplier()));
//	}
//
//	private void closeMultiplierGroup() {
//		CallGroup callGroup = callFIFO.removeFirst();
//		log.debug("Close Group level [{}] with multiplier [{}]", (callFIFO.size() + 1), callGroup.getMultiplier());
//		for (int i=0;i<callGroup.getMultiplier();i++) {
//			callFIFO.peekFirst().addAll(callGroup);
//		}
//	}
//
//	private void openVariance(TouchWord word) {
//		log.debug("Open variance [{}]", word);
//		checkArgument(word.getElements().size() == 1, "Open Variance should have a word with a length of 1");
//		currentVariance = word.getElements().get(0).getVariance();
//	}
//
//	private void closeVariance(TouchWord word) {
//		log.debug("Close variance []", word);
//		checkArgument(word.getElements().size() == 1, "Close Variance should have a word with a length of 1");
//		currentVariance = NullVariance.INSTANCE;
//	}

//	private void insertDefinition(TouchWord word, int columnIndex) {
//		log.debug("Start definition [{}]", word);
//		String elementsAsString = word.getElementsAsString();
//		Optional<DefinitionCell> definitionByShorthand = touch.findDefinitionByShorthand(elementsAsString);
//		if (definitionByShorthand.isPresent()) {
//			generateCallInstancesForCell(definitionByShorthand.get(), columnIndex);
//		}
//		log.debug("Finish definition [{}]",word);
//	}


	private class CallGroup extends ArrayList<DC> {
		private final int multiplier;

		CallGroup(int multiplier) {
			this.multiplier = multiplier;
		}

		int getMultiplier() {
			return multiplier;
		}
	}

	@Immutable
	private class MultiplierAndCall extends DecomposedCall {
		private final int multiplier;

		public MultiplierAndCall(int multiplier, String callName, Variance variance) {
			super(callName, variance);
			this.multiplier = multiplier;
			checkArgument(multiplier > 0, "Multiplier must be positive");
		}

		int getMultiplier() {
			return multiplier;
		}
	}

}
