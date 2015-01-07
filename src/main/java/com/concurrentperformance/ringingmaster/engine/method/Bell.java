package com.concurrentperformance.ringingmaster.engine.method; //TODO where should this live?

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents the individual bell in a method that actually swaps places Etc.
 *
 * @author Stephen Lake
 */
public enum Bell {

	BELL_1("1"),
	BELL_2("2"),
	BELL_3("3"),
	BELL_4("4"),
	BELL_5("5"),
	BELL_6("6"),
	BELL_7("7"),
	BELL_8("8"),
	BELL_9("9"),
	BELL_10("0"),
	BELL_11("E"),
	BELL_12("T"),
	BELL_13("A"),
	BELL_14("B"),
	BELL_15("C"),
	BELL_16("D"),
	BELL_17("F"),
	BELL_18("G"),
	BELL_19("H"),
	BELL_20("J"),
	BELL_21("K"),
	BELL_22("L"),
	BELL_23("M"),
	BELL_24("N"),
	BELL_25("P"),
	BELL_26("Q"),
	BELL_27("R"),
	BELL_28("S"),
	BELL_29("U"),
	BELL_30("V");


	private static final Map<String, Bell> mnemonicLookup = new ConcurrentHashMap<>();

	static {
		for (Bell bell : Bell.values()) {
			mnemonicLookup.put(bell.getMnemonic(), bell);
		}
	}

	private final String mnemonic;

	Bell(final String mnemonic) {
		checkArgument(mnemonic.length() == 1);
		this.mnemonic = mnemonic;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public int getZeroBasedBell() {
		return ordinal();
	}

	public String getDisplayString() {
		return Integer.toString(ordinal()+1);
	}

	public static Bell valueOf(final int bellNumber) {
		return values()[bellNumber];
	}

	public static Bell valueOfMnemonic(final String mnemonic) {
		return mnemonicLookup.get(mnemonic);
	}

}
