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
	BELL_12("T");

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
