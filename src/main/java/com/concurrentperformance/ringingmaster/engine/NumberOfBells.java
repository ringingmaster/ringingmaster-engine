package com.concurrentperformance.ringingmaster.engine; //TODO where should this live?

import com.concurrentperformance.ringingmaster.engine.method.Bell;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Represents the number of bells in a notation / method type.
 *
 * @author Stephen Lake
 *
 */
public enum NumberOfBells implements Iterable<Bell> {

	BELLS_3(3, "Singles"),
	BELLS_4(4, "Minimus"),
	BELLS_5(5, "Doubles"),
	BELLS_6(6, "Minor"),
	BELLS_7(7, "Triples"),
	BELLS_8(8, "Major"),
	BELLS_9(9, "Caters"),
	BELLS_10(10, "Royal"),
	BELLS_11(11, "Cinques"),
	BELLS_12(12, "Maximus");

	private static final NumberOfBells MAX_ITEM = values()[values().length-1];

	public static final NumberOfBells getMax() {
		return MAX_ITEM;
	}

	private static Map<Integer, NumberOfBells> entity = new HashMap<Integer, NumberOfBells>();

	static {
		for(final NumberOfBells value : NumberOfBells.values()) {
			entity.put(Integer.valueOf(value.bellCount), value);
		}
	}

	private final String name;
	private final String displayString;
	private final int bellCount;
	private final Bell tenor;

	NumberOfBells(final int bellCount, final String name) {
		checkArgument(bellCount > 0);
		this.name = name;
		checkArgument(name.length() > 0);
		this.bellCount = bellCount;
		this.tenor = Bell.valueOf(bellCount-1);
		this.displayString = name + " (" + bellCount + ")";
	}

	/**
	 * Get the actual integer number of bells. e.g. For BELLS_8 return 8
	 * @return int
	 */
	public int getBellCount() {
		return bellCount;
	}

	public Bell getTenor() {
		return tenor;
	}

	public String getName() {
		return name;
	}

	public String getDisplayString() {
		return displayString;
	}

	public boolean isEven() {
		return (bellCount % 2) == 0;
	}

	public static NumberOfBells valueOf(final int bellCount) {
		return entity.get(bellCount);
	}

	@Override
	public Iterator<Bell> iterator() {
		return new Iterator<Bell>() {

			int bellIndex = 0;

			@Override
			public boolean hasNext() {
				return bellIndex < bellCount;
			}

			@Override
			public Bell next() {
				return Bell.valueOf(bellIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("NumberOfBells.iterator() does not support remove()");
			}
		};
	}
}
