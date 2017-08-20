package org.ringingmaster.engine.notation.impl;

import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.engine.method.MethodLead;
import org.ringingmaster.engine.method.MethodRow;
import org.ringingmaster.engine.method.impl.MethodBuilder;
import org.ringingmaster.engine.notation.NotationPlace;
import org.ringingmaster.engine.notation.NotationRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * <b>For single-hunt methods:</b><br>
 *   codes a - f and p - q are for seconds place lead ends and codes g - m and r - s for lead ends with no internal places <br>
 * <br>
 * <b>For twin-hunt methods:</b><br>
 *   When the two hunt bells are crossing in 1-2, that is at the first change of the lead rather than the last.
 *   So twin-hunt methods with Plain Bob lead heads that go off "3" will have codes a-f and those that go off with
 *   the last bell lying still will have codes g-m.<br>
 * <br>
 * This also means that twin-hunt methods will have the same code as the corresponding single-hunt methods,
 * for example Single/Double Oxford and Single/Double Court.<br>
 *
 * @author Lake
 */

//TODO Need to handle type 'm' being optionally near for canned calls. file:///Users/Lake/Documents/projects/ringingmaster_cpp/Supporting%20Projects/Web/Ringing%20Master/WebHelp/Option_Library.htm


public class LeadHeadCalculator {


	private final static Logger log = LoggerFactory.getLogger(LeadHeadCalculator.class);

    private static List< LeadHeadCodes> orderedLeadHeadCodes = new ArrayList<>();
    private static Map<MethodRow, LeadHeadCodes> codeLookup = new HashMap<>();
	private static Map<String,  Map<NumberOfBells, String>> rowLookup = new HashMap<>();
	private static Map<String,  LeadHeadType> typeLookup = new HashMap<>();

	public enum LeadHeadType {
		NEAR,
		EXTREME,
	}

	public enum LeadHeadValidity {
		VALID_LEADHEAD_CODE,
		VALID_LEADHEAD_ROW,
		INVALID_LEADHEAD,
	}

	public static String calculateLeadHeadCode(MethodLead plainLead, List<NotationRow> normalisedNotationElements) {
		Set<NotationPlace> huntBellStartPlace = plainLead.getHuntBellStartPlace();

		if (huntBellStartPlace.size() <= 1) {
			return getLeadHeadCodeForSingleHunt(plainLead, normalisedNotationElements);
		}
		else {
			return getLeadHeadCodeForTwinHunt(plainLead, normalisedNotationElements);
		}
	}

	private static String getLeadHeadCodeForSingleHunt(MethodLead plainLead, List<NotationRow> normalisedNotationElements) {
		NotationRow leadHeadNotationRow = normalisedNotationElements.get(normalisedNotationElements.size() - 1);
		NumberOfBells numberOfBells = plainLead.getNumberOfBells();
		MethodRow leadHeadRow = plainLead.getLastRow();

		if (!hasLeadEndGotPlainBobPlacesForSingleHunt(numberOfBells, leadHeadNotationRow)) {
			return leadHeadRow.getDisplayString(false);
		}

		if (hasLeadEndGotInternalPlaces(numberOfBells, leadHeadNotationRow)) {
			return lookupLeadHeadCode(leadHeadRow, LeadHeadType.NEAR);
		}
		else {
			return lookupLeadHeadCode(leadHeadRow, LeadHeadType.EXTREME);
		}
	}

	private static boolean hasLeadEndGotPlainBobPlacesForSingleHunt(NumberOfBells numberOfBells, NotationRow leadHeadNotationRow) {

		if (numberOfBells.isEven()) {
			if (leadHeadNotationRow.getElementCount() == 2) {
				if (leadHeadNotationRow.makesPlace(NotationPlace.PLACE_1) &&
						leadHeadNotationRow.makesPlace(NotationPlace.PLACE_2)) {
					return true;
				}
				if (leadHeadNotationRow.makesPlace(NotationPlace.PLACE_1) &&
						leadHeadNotationRow.makesPlace(numberOfBells.getTenorPlace())) {
					return true;
				}

			}
			return false;

		}
		else {
			if (leadHeadNotationRow.getElementCount() == 1) {
				if (leadHeadNotationRow.makesPlace(NotationPlace.PLACE_1)) {
					return true;
				}
			}
			if (leadHeadNotationRow.getElementCount() == 3) {
				if (leadHeadNotationRow.makesPlace(NotationPlace.PLACE_1) &&
						leadHeadNotationRow.makesPlace(NotationPlace.PLACE_2) &&
						leadHeadNotationRow.makesPlace(numberOfBells.getTenorPlace())) {
					return true;
				}
			}
			return false;
		}
	}


	private static String getLeadHeadCodeForTwinHunt(MethodLead plainLead, List<NotationRow> normalisedNotationElements) {
		NotationRow leadHeadNotationRow = normalisedNotationElements.get(0);
		NumberOfBells numberOfBells = plainLead.getNumberOfBells();
		MethodRow leadHeadRow = plainLead.getLastRow();

		if (numberOfBells.isEven()) {
			if (leadHeadNotationRow.isAllChange()) {
				return lookupLeadHeadCode(leadHeadRow, LeadHeadType.EXTREME);
			} else {
				return lookupLeadHeadCode(leadHeadRow, LeadHeadType.NEAR);
			}
		}
		else {

			if (leadHeadNotationRow.makesPlace(NotationPlace.PLACE_3)) {
				return lookupLeadHeadCode(leadHeadRow, LeadHeadType.NEAR);
			} else if (leadHeadNotationRow.makesPlace(NotationPlace.valueOf(numberOfBells.getBellCount() - 1))) {
				return lookupLeadHeadCode(leadHeadRow, LeadHeadType.EXTREME);
			} else {
				return leadHeadRow.getDisplayString(false);
			}
		}
	}

	public static String lookupRowFromCode(String leadHeadCode, NumberOfBells numberOfBells) {
		checkState(getLeadHeadValidity(leadHeadCode, numberOfBells) != LeadHeadValidity.INVALID_LEADHEAD);

		Map<NumberOfBells, String> lookupMap = rowLookup.get(leadHeadCode);
		if (lookupMap != null) {
			String fullLeadHeadCode = lookupMap.get(numberOfBells);
			if (fullLeadHeadCode != null) {
				return fullLeadHeadCode;
			}
		}
		return leadHeadCode;
	}

	public static LeadHeadValidity getLeadHeadValidity(String leadHeadCode, NumberOfBells numberOfBells) {
		Map<NumberOfBells, String> lookupMap = rowLookup.get(leadHeadCode);
		if (lookupMap != null) {
			String fullLeadHeadCode = lookupMap.get(numberOfBells);
			if (fullLeadHeadCode != null) {
				return LeadHeadValidity.VALID_LEADHEAD_CODE;
			}
		}

		// At this point, it could be a row. Attempt to parse it.
		try {
			MethodBuilder.parse(numberOfBells, leadHeadCode);
			return LeadHeadValidity.VALID_LEADHEAD_ROW;
		}
		catch (Exception e) {
			return LeadHeadValidity.INVALID_LEADHEAD;
		}
	}

	public static LeadHeadType getLeadHeadType(String leadHeadCode) {
		return typeLookup.get(leadHeadCode);
	}

	private static boolean hasLeadEndGotInternalPlaces(NumberOfBells numberOfBells, NotationRow leadHeadNotationRow) {

		for (int zeroBasedPlace=1;zeroBasedPlace<numberOfBells.getBellCount()-1;zeroBasedPlace++) {
			NotationPlace notationPlace = NotationPlace.valueOf(zeroBasedPlace);
			if (leadHeadNotationRow.makesPlace(notationPlace)) {
				return true;
			}
 		}
		return false;
	}

	protected static String lookupLeadHeadCode(MethodRow row, LeadHeadType type) {
		checkNotNull(row);
		LeadHeadCodes leadHeadCode = codeLookup.get(row);

		if (leadHeadCode != null) {
			switch (type) {
				case NEAR:
					return leadHeadCode.getNear();
				case EXTREME:
					return leadHeadCode.getExtreme();
				default:
					throw new IllegalArgumentException("Unknown lead head type [" + type + "]");
			}
		}
		else {
			return row.getDisplayString(false);
		}
	}

	public static List<LeadHeadCodes> getOrderedLeadHeadCodes() {
		return orderedLeadHeadCodes;
	}

	private static void addLeadHeadCodes(NumberOfBells numberOfBells, String change, LeadHeadCodes leadHeadCodes) {
		MethodRow row = MethodBuilder.parse(numberOfBells, change);
		codeLookup.put(row, leadHeadCodes);

		LeadHeadType previousTypeNear = typeLookup.put(leadHeadCodes.near, LeadHeadType.NEAR);
		checkState(previousTypeNear == null || previousTypeNear == LeadHeadType.NEAR);
		LeadHeadType previousTypeExtreme = typeLookup.put(leadHeadCodes.extreme, LeadHeadType.EXTREME);
		checkState(previousTypeExtreme == null || previousTypeExtreme == LeadHeadType.EXTREME);

		putRowLookup(numberOfBells, change, leadHeadCodes.getNear());
		putRowLookup(numberOfBells, change, leadHeadCodes.getExtreme());
	}

	private static void putRowLookup(NumberOfBells numberOfBells, String change, String code) {
		Map<NumberOfBells, String> lookupMap = rowLookup.get(code);
		if (lookupMap == null) {
			lookupMap = new HashMap<>();
			rowLookup.put(code, lookupMap);
		}
		lookupMap.put(numberOfBells, change);
	}

	static {

        LeadHeadCodes a_g   = addOrderedCode("a",  "g");
        LeadHeadCodes b_h   = addOrderedCode("b",  "h");
		LeadHeadCodes c_j   = addOrderedCode("c",  "j");
		LeadHeadCodes c1_j1 = addOrderedCode("c1", "j1");
		LeadHeadCodes c2_j2 = addOrderedCode("c2", "j2");
		LeadHeadCodes c3_j3 = addOrderedCode("c3", "j3");
		LeadHeadCodes c4_j4 = addOrderedCode("c4", "j4");
		LeadHeadCodes c5_j5 = addOrderedCode("c5", "j5");
        LeadHeadCodes c6_j6 = addOrderedCode("c6", "j6");
        LeadHeadCodes c7_j7 = addOrderedCode("c7", "j7");
        LeadHeadCodes d7_k7 = addOrderedCode("d7", "k7");
        LeadHeadCodes d6_k6 = addOrderedCode("d6", "k6");
		LeadHeadCodes d5_k5 = addOrderedCode("d5", "k5");
		LeadHeadCodes d4_k4 = addOrderedCode("d4", "k4");
		LeadHeadCodes d3_k3 = addOrderedCode("d3", "k3");
		LeadHeadCodes d2_k2 = addOrderedCode("d2", "k2");
		LeadHeadCodes d1_k1 = addOrderedCode("d1", "k1");
		LeadHeadCodes d_k   = addOrderedCode("d",  "k");
		LeadHeadCodes e_l   = addOrderedCode("e",  "l");
		LeadHeadCodes f_m   = addOrderedCode("f",  "m");
		LeadHeadCodes p_r   = addOrderedCode("p",  "r");
        LeadHeadCodes p1_r1 = addOrderedCode("p1", "r1");
        LeadHeadCodes p2_r2 = addOrderedCode("p2", "r2");
		LeadHeadCodes p3_r3 = addOrderedCode("p3", "r3");
		LeadHeadCodes p4_r4 = addOrderedCode("p4", "r4");
		LeadHeadCodes q4_s4 = addOrderedCode("q4", "s4");
        LeadHeadCodes q3_s3 = addOrderedCode("q3", "s3");
        LeadHeadCodes q2_s2 = addOrderedCode("q2", "s2");
		LeadHeadCodes q1_s1 = addOrderedCode("q1", "s1");
		LeadHeadCodes q_s   = addOrderedCode("q",  "s");

		addLeadHeadCodes(NumberOfBells.BELLS_4, "1342", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_4, "1423", f_m);

		addLeadHeadCodes(NumberOfBells.BELLS_5, "12534", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_5, "12453", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_5, "13524", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_5, "14253", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_6, "135264", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_6, "156342", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_6, "164523", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_6, "142635", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_6, "125364", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_6, "124635", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_7, "1253746", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_7, "1275634", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_7, "1267453", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_7, "1246375", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_7, "1352746", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_7, "1426375", q_s);
		
		addLeadHeadCodes(NumberOfBells.BELLS_8, "13527486", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "15738264", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "17856342", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "18674523", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "16482735", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "14263857", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "12537486", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_8, "12463857", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_9, "125374968", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "127593846", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "129785634", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "128967453", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "126849375", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "124638597", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "135274968", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "179583624", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "186947253", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_9, "142638597", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_10, "1352749608", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1573920486", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1907856342", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1089674523", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1648203957", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1426385079", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1253749608", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1297058364", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1280694735", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_10, "1246385079", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_11, "12537496E80", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "127593E4068", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "12E90785634", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "120E8967453", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "1268403E597", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "124638507E9", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "13527496E80", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "1795E302846", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "18604E29375", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_11, "142638507E9", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_12, "13527496E8T0", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "157392E4T608", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "1795E3T20486", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "19E7T5038264", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "1ET907856342", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "1T0E89674523", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "108T6E492735", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "18604T2E3957", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "1648203T5E79", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "142638507T9E", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "12537496E8T0", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "1297E5T30486", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "12806T4E3957", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_12, "124638507T9E", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_13, "12537496E8A0T", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "127593E4A6T80", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "1297E5A3T4068", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "12E9A7T503846", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "12AET90785634", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "12TA0E8967453", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "120T8A6E49375", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "12806T4A3E597", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "1268403T5A7E9", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "124638507T9AE", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "13527496E8A0T", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "1EA9T70583624", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "1T0A8E6947253", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_13, "142638507T9AE", q_s);
		
		addLeadHeadCodes(NumberOfBells.BELLS_14, "13527496E8A0BT", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "157392E4A6B8T0", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "1795E3A2B4T608", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "19E7A5B3T20486", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "1EA9B7T5038264", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "1ABET907856342", c3_j3);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "1BTA0E89674523", d3_k3);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "1T0B8A6E492735", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "108T6B4A2E3957", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "18604T2B3A5E79", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "1648203T5B7A9E", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "142638507T9BEA", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "12537496E8A0BT", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "12AEB9T7058364", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "12TB0A8E694735", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_14, "124638507T9BEA", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_15, "12537496E8A0CTB", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "127593E4A6C8B0T", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "1297E5A3C4B6T80", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "12E9A7C5B3T4068", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "12AEC9B7T503846", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "12CABET90785634", c3_j3);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "12BCTA0E8967453", d3_k3);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "12TB0C8A6E49375", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "120T8B6C4A3E597", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "12806T4B3C5A7E9", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "1268403T5B7C9AE", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "124638507T9BECA", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "13527496E8A0CTB", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "1795E3A2C4B6T80", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "1EA9C7B5T302846", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "1T0B8C6A4E29375", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "18604T2B3C5A7E9", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_15, "142638507T9BECA", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_16, "13527496E8A0CTDB", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "157392E4A6C8D0BT", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "19E7A5C3D2B4T608", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "1CDABET907856342", c4_j4);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "1DBCTA0E89674523", d4_k4);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "108T6B4D2C3A5E79", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "1648203T5B7D9CEA", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "142638507T9BEDAC", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "12537496E8A0CTDB", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "1297E5A3C4D6B8T0", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "12AEC9D7B5T30486", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "12TB0D8C6A4E3957", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "12806T4B3D5C7A9E", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_16, "124638507T9BEDAC", q_s);
		
		addLeadHeadCodes(NumberOfBells.BELLS_17, "12537496E8A0CTFBD", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "127593E4A6C8F0DTB", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "12E9A7C5F3D4B6T80", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "12FCDABET90785634", c4_j4);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "12DFBCTA0E8967453", d4_k4);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "120T8B6D4F3C5A7E9", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "1268403T5B7D9FECA", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "124638507T9BEDAFC", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "13527496E8A0CTFBD", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "1795E3A2C4F6D8B0T", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "1EA9C7F5D3B2T4068", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "1CFADEB9T70583624", p3_r3);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "1DBFTC0A8E6947253", q3_s3);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "1T0B8D6F4C2A3E597", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "18604T2B3D5F7C9AE", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_17, "142638507T9BEDAFC", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_18, "13527496E8A0CTFBGD", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "157392E4A6C8F0GTDB", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1795E3A2C4F6G8D0BT", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "19E7A5C3F2G4D6B8T0", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1EA9C7F5G3D2B4T608", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1ACEF9G7D5B3T20486", c3_j3);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1CFAGED9B7T5038264", c4_j4);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1FGCDABET907856342", c5_j5);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1GDFBCTA0E89674523", d5_k5);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1DBGTF0C8A6E492735", d4_k4);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1BTD0G8F6C4A2E3957", d3_k3);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1T0B8D6G4F2C3A5E79", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "108T6B4D2G3F5C7A9E", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "18604T2B3D5G7F9CEA", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1648203T5B7D9GEFAC", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "142638507T9BEDAGCF", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "12537496E8A0CTFBGD", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "1297E5A3C4F6G8D0BT", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "12AEC9F7G5D3B4T608", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "12FCGADEB9T7058364", p3_r3);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "12DGBFTC0A8E694735", q3_s3);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "12TB0D8G6F4C3A5E79", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "12806T4B3D5G7F9CEA", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_18, "124638507T9BEDAGCF", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_19, "12537496E8A0CTFBHDG", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "127593E4A6C8F0HTGBD", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "1297E5A3C4F6H8G0DTB", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12E9A7C5F3H4G6D8B0T", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12AEC9F7H5G3D4B6T80", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12CAFEH9G7D5B3T4068", c3_j3);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12FCHAGED9B7T503846", c4_j4);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12HFGCDABET90785634", c5_j5);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12GHDFBCTA0E8967453", d5_k5);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12DGBHTF0C8A6E49375", d4_k4);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12BDTG0H8F6C4A3E597", d3_k3);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12TB0D8G6H4F3C5A7E9", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "120T8B6D4G3H5F7C9AE", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "12806T4B3D5G7H9FECA", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "1268403T5B7D9GEHAFC", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "124638507T9BEDAGCHF", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "13527496E8A0CTFBHDG", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "1EA9C7F5H3G2D4B6T80", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "1CFAHEG9D7B5T302846", p3_r3);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "1DBGTH0F8C6A4E29375", q3_s3);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "1T0B8D6G4H2F3C5A7E9", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_19, "142638507T9BEDAGCHF", q_s);
		
		addLeadHeadCodes(NumberOfBells.BELLS_20, "13527496E8A0CTFBHDJG", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "157392E4A6C8F0HTJBGD", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1795E3A2C4F6H8J0GTDB", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "19E7A5C3F2H4J6G8D0BT", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1EA9C7F5H3J2G4D6B8T0", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1ACEF9H7J5G3D2B4T608", c3_j3);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1CFAHEJ9G7D5B3T20486", c4_j4);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1FHCJAGED9B7T5038264", c5_j5);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1HJFGCDABET907856342", c6_j6);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1JGHDFBCTA0E89674523", d6_k6);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1GDJBHTF0C8A6E492735", d5_k5);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1DBGTJ0H8F6C4A2E3957", d4_k4);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1BTD0G8J6H4F2C3A5E79", d3_k3);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1T0B8D6G4J2H3F5C7A9E", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "108T6B4D2G3J5H7F9CEA", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "18604T2B3D5G7J9HEFAC", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "1648203T5B7D9GEJAHCF", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "142638507T9BEDAGCJFH", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "12537496E8A0CTFBHDJG", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "12AEC9F7H5J3G4D6B8T0", p2_r2);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "12FCHAJEG9D7B5T30486", p3_r3);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "12DGBJTH0F8C6A4E3957", q3_s3);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "12TB0D8G6J4H3F5C7A9E", q2_s2);
		addLeadHeadCodes(NumberOfBells.BELLS_20, "124638507T9BEDAGCJFH", q_s);
		
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12537496E8A0CTFBHDKGJ", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "127593E4A6C8F0HTKBJDG", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1297E5A3C4F6H8K0JTGBD", c_j);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12E9A7C5F3H4K6J8G0DTB", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12AEC9F7H5K3J4G6D8B0T", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12CAFEH9K7J5G3D4B6T80", c3_j3);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12FCHAKEJ9G7D5B3T4068", c4_j4);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12HFKCJAGED9B7T503846", c5_j5);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12KHJFGCDABET90785634", c6_j6);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12JKGHDFBCTA0E8967453", d6_k6);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12GJDKBHTF0C8A6E49375", d5_k5);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12DGBJTK0H8F6C4A3E597", d4_k4);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12TB0D8G6J4K3H5F7C9AE", d3_k3);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12TB0D8G6J4K3H5F7C9AE", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "120T8B6D4G3J5K7H9FECA", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "12806T4B3D5G7J9KEHAFC", d_k);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1268403T5B7D9GEJAKCHF", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "124638507T9BEDAGCJFKH", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "13527496E8A0CTFBHDKGJ", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1795E3A2C4F6H8K0JTGBD", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1CFAHEK9J7G5D3B2T4068", p3_r3);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1HKFJCGADEB9T70583624", p4_r4);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1JGKDHBFTC0A8E6947253", q4_s4);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "1DBGTJ0K8H6F4C2A3E597", q3_s3);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "18604T2B3D5G7J9KEHAFC", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_21, "142638507T9BEDAGCJFKH", q_s);

		addLeadHeadCodes(NumberOfBells.BELLS_22, "13527496E8A0CTFBHDKGLJ", a_g);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "157392E4A6C8F0HTKBLDJG", b_h);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "19E7A5C3F2H4K6L8J0GTDB", c1_j1);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1EA9C7F5H3K2L4J6G8D0BT", c2_j2);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1FHCKALEJ9G7D5B3T20486", c5_j5);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1KLHJFGCDABET907856342", c7_j7);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1LJKGHDFBCTA0E89674523", d7_k7);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1GDJBLTK0H8F6C4A2E3957", d5_k5);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1T0B8D6G4J2L3K5H7F9CEA", d2_k2);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "108T6B4D2G3J5L7K9HEFAC", d1_k1);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1648203T5B7D9GEJALCKFH", e_l);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "142638507T9BEDAGCJFLHK", f_m);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "12537496E8A0CTFBHDKGLJ", p_r);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "1297E5A3C4F6H8K0LTJBGD", p1_r1);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "12FCHAKEL9J7G5D3B4T608", p3_r3);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "12KHLFJCGADEB9T7058364", p4_r4);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "12JLGKDHBFTC0A8E694735", q4_s4);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "12DGBJTL0K8H6F4C3A5E79", q3_s3);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "12806T4B3D5G7J9LEKAHCF", q1_s1);
		addLeadHeadCodes(NumberOfBells.BELLS_22, "124638507T9BEDAGCJFLHK", q_s);
	}

    private static LeadHeadCodes addOrderedCode(String near, String extreme) {
        final LeadHeadCodes leadHeadCodes = new LeadHeadCodes(near, extreme);
        orderedLeadHeadCodes.add(leadHeadCodes);
        return leadHeadCodes;
    }

    public static class LeadHeadCodes {

		private final String near;
		private final String extreme;

		LeadHeadCodes(String near, String extreme) {
			this.near = near;
			this.extreme = extreme;
		}

		public String getNear() {
			return near;
		}

		public String getExtreme() {
			return extreme;
		}
	}
}
