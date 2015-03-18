package com.concurrentperformance.ringingmaster.engine.notation.impl;

import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.engine.method.MethodLead;
import com.concurrentperformance.ringingmaster.engine.method.MethodRow;
import com.concurrentperformance.ringingmaster.engine.method.impl.MethodBuilder;
import com.concurrentperformance.ringingmaster.engine.notation.NotationPlace;
import com.concurrentperformance.ringingmaster.engine.notation.NotationRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class LeadHeadCalculator {

	private static Map<MethodRow, LeadHeadCodes> leadHeadCodes = new HashMap<>();

	enum LeadHeadType {
		NEAR,
		FAR,
	}

	public static String calculateLeadHeadCode(MethodLead plainLead, List<NotationRow> normalisedNotationElements) {
		NumberOfBells numberOfBells = plainLead.getNumberOfBells();

		NotationRow lastNotationRow = normalisedNotationElements.get(normalisedNotationElements.size() - 1);
		NotationPlace highestPlace = NotationPlace.valueOf(numberOfBells.getBellCount() -1 ); // -1 converts to zero based call to NotationPlace.valueOf
		boolean lastNotationRowContainsHighestPlace = lastNotationRow.contains(highestPlace);

		MethodRow lastMethodRow = plainLead.getLastRow();

		LeadHeadType leadHeadType;
		if (numberOfBells.isEven()) {
			leadHeadType = lastNotationRowContainsHighestPlace? LeadHeadType.FAR:LeadHeadType.NEAR;
		}
		else {
			leadHeadType = lastNotationRowContainsHighestPlace? LeadHeadType.NEAR:LeadHeadType.FAR;
		}

		String loadHeadCode = lookupLeadHeadCode(lastMethodRow, leadHeadType);
		return loadHeadCode;
	}

	static String lookupLeadHeadCode(MethodRow row, LeadHeadType type) {
		checkNotNull(row);
		LeadHeadCodes leadHeadCode = leadHeadCodes.get(row);

		if (leadHeadCode != null) {
			switch (type) {
				case NEAR:
					return leadHeadCode.getNear();
				case FAR:
					return leadHeadCode.getFar();
				default:
					throw new IllegalArgumentException("Unknown lead head type [" + type + "]");
			}
		}
		else {
			return row.getDisplayString(false);
		}
	}


	private static void addLeadHeadCode(NumberOfBells numberOfBells, String change, String nearCode, String farCode) {
		MethodRow row = MethodBuilder.parse(numberOfBells, change);
		leadHeadCodes.put(row, new LeadHeadCodes(nearCode, farCode));
	}

	static {

		addLeadHeadCode(NumberOfBells.BELLS_4, "1342", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_4, "1423", "f", "m");

		addLeadHeadCode(NumberOfBells.BELLS_5, "12534", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_5, "12453", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_5, "13524", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_5, "14253", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_6, "135264", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_6, "156342", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_6, "164523", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_6, "142635", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_6, "125364", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_6, "124635", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_7, "1253746", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_7, "1275634", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_7, "1267453", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_7, "1246375", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_7, "1352746", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_7, "1426375", "q", "s");
		
		addLeadHeadCode(NumberOfBells.BELLS_8, "13527486", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_8, "15738264", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_8, "17856342", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_8, "18674523", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_8, "16482735", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_8, "14263857", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_8, "12537486", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_8, "12463857", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_9, "125374968", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_9, "127593846", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_9, "129785634", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_9, "128967453", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_9, "126849375", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_9, "124638597", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_9, "135274968", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_9, "179583624", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_9, "186947253", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_9, "142638597", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_10, "1352749608", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1573920486", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1907856342", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1089674523", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1648203957", "e", "m");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1426385079", "f", "n");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1253749608", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1297058364", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1280694735", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_10, "1246385079", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_11, "12537496E80", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_11, "127593E4068", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_11, "12E90785634", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_11, "120E8967453", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_11, "1268403E597", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_11, "124638507E9", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_11, "13527496E80", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_11, "1795E302846", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_11, "18604E29375", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_11, "142638507E9", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_12, "13527496E8T0", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_12, "157392E4T608", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_12, "1795E3T20486", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_12, "19E7T5038264", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_12, "1ET907856342", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_12, "1T0E89674523", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_12, "108T6E492735", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_12, "108T6E492735", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_12, "1648203T5E79", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_12, "142638507T9E", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_12, "12537496E8T0", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_12, "1297E5T30486", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_12, "12806T4E3957", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_12, "124638507T9E", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_13, "12537496E8A0T", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_13, "127593E4A6T80", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_13, "1297E5A3T4068", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_13, "12E9A7T503846", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_13, "12AET90785634", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_13, "12TA0E8967453", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_13, "120T8A6E49375", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_13, "12806T4A3E597", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_13, "1268403T5A7E9", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_13, "124638507T9AE", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_13, "13527496E8A0T", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_13, "1EA9T70583624", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_13, "1T0A8E6947253", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_13, "142638507T9AE", "q", "s");
		
		addLeadHeadCode(NumberOfBells.BELLS_14, "13527496E8A0BT", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_14, "157392E4A6B8T0", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_14, "1795E3A2B4T608", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_14, "19E7A5B3T20486", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_14, "1EA9B7T5038264", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_14, "1ABET907856342", "c3", "j3");
		addLeadHeadCode(NumberOfBells.BELLS_14, "1BTA0E89674523", "d3", "k3");
		addLeadHeadCode(NumberOfBells.BELLS_14, "1T0B8A6E492735", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_14, "108T6B4A2E3957", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_14, "18604T2B3A5E79", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_14, "1648203T5B7A9E", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_14, "142638507T9BEA", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_14, "12537496E8A0BT", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_14, "12AEB9T7058364", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_14, "12TB0A8E694735", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_14, "124638507T9BEA", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_15, "12537496E8A0CTB", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_15, "127593E4A6C8B0T", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_15, "1297E5A3C4B6T80", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_15, "12E9A7C5B3T4068", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_15, "12AEC9B7T503846", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_15, "12CABET90785634", "c3", "j3");
		addLeadHeadCode(NumberOfBells.BELLS_15, "12BCTA0E8967453", "d3", "k3");
		addLeadHeadCode(NumberOfBells.BELLS_15, "12TB0C8A6E49375", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_15, "120T8B6C4A3E597", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_15, "12806T4B3C5A7E9", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_15, "1268403T5B7C9AE", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_15, "124638507T9BECA", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_15, "13527496E8A0CTB", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_15, "1795E3A2C4B6T80", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_15, "1EA9C7B5T302846", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_15, "1T0B8C6A4E29375", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_15, "18604T2B3C5A7E9", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_15, "142638507T9BECA", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_16, "13527496E8A0CTDB", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_16, "157392E4A6C8D0BT", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_16, "19E7A5C3D2B4T608", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_16, "1CDABET907856342", "c4", "j4");
		addLeadHeadCode(NumberOfBells.BELLS_16, "1DBCTA0E89674523", "d4", "k4");
		addLeadHeadCode(NumberOfBells.BELLS_16, "108T6B4D2C3A5E79", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_16, "1648203T5B7D9CEA", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_16, "142638507T9BEDAC", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_16, "12537496E8A0CTDB", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_16, "1297E5A3C4D6B8T0", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_16, "12AEC9D7B5T30486", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_16, "12TB0D8C6A4E3957", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_16, "12806T4B3D5C7A9E", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_16, "124638507T9BEDAC", "q", "s");
		
		addLeadHeadCode(NumberOfBells.BELLS_17, "12537496E8A0CTFBD", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_17, "127593E4A6C8F0DTB", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_17, "12E9A7C5F3D4B6T80", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_17, "12FCDABET90785634", "c4", "j4");
		addLeadHeadCode(NumberOfBells.BELLS_17, "12DFBCTA0E8967453", "d4", "k4");
		addLeadHeadCode(NumberOfBells.BELLS_17, "120T8B6D4F3C5A7E9", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_17, "1268403T5B7D9FECA", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_17, "124638507T9BEDAFC", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_17, "13527496E8A0CTFBD", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_17, "1795E3A2C4F6D8B0T", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_17, "1EA9C7F5D3B2T4068", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_17, "1CFADEB9T70583624", "p3", "r3");
		addLeadHeadCode(NumberOfBells.BELLS_17, "1DBFTC0A8E6947253", "q3", "s3");
		addLeadHeadCode(NumberOfBells.BELLS_17, "1T0B8D6F4C2A3E597", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_17, "18604T2B3D5F7C9AE", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_17, "142638507T9BEDAFC", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_18, "13527496E8A0CTFBGD", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_18, "157392E4A6C8F0GTDB", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1795E3A2C4F6G8D0BT", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_18, "19E7A5C3F2G4D6B8T0", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1EA9C7F5G3D2B4T608", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1ACEF9G7D5B3T20486", "c3", "j3");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1CFAGED9B7T5038264", "c4", "j4");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1FGCDABET907856342", "c5", "j5");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1GDFBCTA0E89674523", "d5", "k5");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1DBGTF0C8A6E492735", "d4", "k4");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1BTD0G8F6C4A2E3957", "d3", "k3");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1T0B8D6G4F2C3A5E79", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_18, "108T6B4D2G3F5C7A9E", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_18, "18604T2B3D5G7F9CEA", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1648203T5B7D9GEFAC", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_18, "142638507T9BEDAGCF", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_18, "12537496E8A0CTFBGD", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_18, "1297E5A3C4F6G8D0BT", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_18, "12AEC9F7G5D3B4T608", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_18, "12FCGADEB9T7058364", "p3", "r3");
		addLeadHeadCode(NumberOfBells.BELLS_18, "12DGBFTC0A8E694735", "q3", "s3");
		addLeadHeadCode(NumberOfBells.BELLS_18, "12TB0D8G6F4C3A5E79", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_18, "12806T4B3D5G7F9CEA", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_18, "124638507T9BEDAGCF", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_19, "12537496E8A0CTFBHDG", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_19, "127593E4A6C8F0HTGBD", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_19, "1297E5A3C4F6H8G0DTB", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12E9A7C5F3H4G6D8B0T", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12AEC9F7H5G3D4B6T80", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12CAFEH9G7D5B3T4068", "c3", "j3");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12FCHAGED9B7T503846", "c4", "j4");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12HFGCDABET90785634", "c5", "j5");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12GHDFBCTA0E8967453", "d5", "k5");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12DGBHTF0C8A6E49375", "d4", "k4");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12BDTG0H8F6C4A3E597", "d3", "k3");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12TB0D8G6H4F3C5A7E9", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_19, "120T8B6D4G3H5F7C9AE", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_19, "12806T4B3D5G7H9FECA", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_19, "1268403T5B7D9GEHAFC", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_19, "124638507T9BEDAGCHF", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_19, "13527496E8A0CTFBHDG", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_19, "1EA9C7F5H3G2D4B6T80", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_19, "1CFAHEG9D7B5T302846", "p3", "r3");
		addLeadHeadCode(NumberOfBells.BELLS_19, "1DBGTH0F8C6A4E29375", "q3", "s3");
		addLeadHeadCode(NumberOfBells.BELLS_19, "1T0B8D6G4H2F3C5A7E9", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_19, "142638507T9BEDAGCHF", "q", "s");
		
		addLeadHeadCode(NumberOfBells.BELLS_20, "13527496E8A0CTFBHDJG", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_20, "157392E4A6C8F0HTJBGD", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1795E3A2C4F6H8J0GTDB", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_20, "19E7A5C3F2H4J6G8D0BT", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1EA9C7F5H3J2G4D6B8T0", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1ACEF9H7J5G3D2B4T608", "c3", "j3");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1CFAHEJ9G7D5B3T20486", "c4", "j4");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1FHCJAGED9B7T5038264", "c5", "j5");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1HJFGCDABET907856342", "c6", "j6");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1JGHDFBCTA0E89674523", "d6", "k6");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1GDJBHTF0C8A6E492735", "d5", "k5");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1DBGTJ0H8F6C4A2E3957", "d4", "k4");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1BTD0G8J6H4F2C3A5E79", "d3", "k3");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1T0B8D6G4J2H3F5C7A9E", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_20, "108T6B4D2G3J5H7F9CEA", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_20, "18604T2B3D5G7J9HEFAC", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_20, "1648203T5B7D9GEJAHCF", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_20, "142638507T9BEDAGCJFH", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_20, "12537496E8A0CTFBHDJG", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_20, "12AEC9F7H5J3G4D6B8T0", "p2", "r2");
		addLeadHeadCode(NumberOfBells.BELLS_20, "12FCHAJEG9D7B5T30486", "p3", "r3");
		addLeadHeadCode(NumberOfBells.BELLS_20, "12DGBJTH0F8C6A4E3957", "q3", "s3");
		addLeadHeadCode(NumberOfBells.BELLS_20, "12TB0D8G6J4H3F5C7A9E", "q2", "s2");
		addLeadHeadCode(NumberOfBells.BELLS_20, "124638507T9BEDAGCJFH", "q", "s");
		
		addLeadHeadCode(NumberOfBells.BELLS_21, "12537496E8A0CTFBHDKGJ", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_21, "127593E4A6C8F0HTKBJDG", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1297E5A3C4F6H8K0JTGBD", "c", "j");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12E9A7C5F3H4K6J8G0DTB", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12AEC9F7H5K3J4G6D8B0T", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12CAFEH9K7J5G3D4B6T80", "c3", "j3");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12FCHAKEJ9G7D5B3T4068", "c4", "j4");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12HFKCJAGED9B7T503846", "c5", "j5");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12KHJFGCDABET90785634", "c6", "j6");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12JKGHDFBCTA0E8967453", "d6", "k6");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12GJDKBHTF0C8A6E49375", "d5", "k5");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12DGBJTK0H8F6C4A3E597", "d4", "k4");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12TB0D8G6J4K3H5F7C9AE", "d3", "k3");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12TB0D8G6J4K3H5F7C9AE", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_21, "120T8B6D4G3J5K7H9FECA", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_21, "12806T4B3D5G7J9KEHAFC", "d", "k");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1268403T5B7D9GEJAKCHF", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_21, "124638507T9BEDAGCJFKH", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_21, "13527496E8A0CTFBHDKGJ", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1795E3A2C4F6H8K0JTGBD", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1CFAHEK9J7G5D3B2T4068", "p3", "r3");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1HKFJCGADEB9T70583624", "p4", "r4");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1JGKDHBFTC0A8E6947253", "q4", "s4");
		addLeadHeadCode(NumberOfBells.BELLS_21, "1DBGTJ0K8H6F4C2A3E597", "q3", "s3");
		addLeadHeadCode(NumberOfBells.BELLS_21, "18604T2B3D5G7J9KEHAFC", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_21, "142638507T9BEDAGCJFKH", "q", "s");

		addLeadHeadCode(NumberOfBells.BELLS_22, "13527496E8A0CTFBHDKGLJ", "a", "g");
		addLeadHeadCode(NumberOfBells.BELLS_22, "157392E4A6C8F0HTKBLDJG", "b", "h");
		addLeadHeadCode(NumberOfBells.BELLS_22, "19E7A5C3F2H4K6L8J0GTDB", "c1", "j1");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1EA9C7F5H3K2L4J6G8D0BT", "c2", "j2");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1FHCKALEJ9G7D5B3T20486", "c5", "j5");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1KLHJFGCDABET907856342", "c7", "j7");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1LJKGHDFBCTA0E89674523", "d7", "k7");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1GDJBLTK0H8F6C4A2E3957", "d5", "k5");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1T0B8D6G4J2L3K5H7F9CEA", "d2", "k2");
		addLeadHeadCode(NumberOfBells.BELLS_22, "108T6B4D2G3J5L7K9HEFAC", "d1", "k1");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1648203T5B7D9GEJALCKFH", "e", "l");
		addLeadHeadCode(NumberOfBells.BELLS_22, "142638507T9BEDAGCJFLHK", "f", "m");
		addLeadHeadCode(NumberOfBells.BELLS_22, "12537496E8A0CTFBHDKGLJ", "p", "r");
		addLeadHeadCode(NumberOfBells.BELLS_22, "1297E5A3C4F6H8K0LTJBGD", "p1", "r1");
		addLeadHeadCode(NumberOfBells.BELLS_22, "12FCHAKEL9J7G5D3B4T608", "p3", "r3");
		addLeadHeadCode(NumberOfBells.BELLS_22, "12KHLFJCGADEB9T7058364", "p4", "r4");
		addLeadHeadCode(NumberOfBells.BELLS_22, "12JLGKDHBFTC0A8E694735", "q4", "s4");
		addLeadHeadCode(NumberOfBells.BELLS_22, "12DGBJTL0K8H6F4C3A5E79", "q3", "s3");
		addLeadHeadCode(NumberOfBells.BELLS_22, "12806T4B3D5G7J9LEKAHCF", "q1", "s1");
		addLeadHeadCode(NumberOfBells.BELLS_22, "124638507T9BEDAGCJFLHK", "q", "s");
	}

	private static class LeadHeadCodes {

		private final String near;
		private final String far;

		LeadHeadCodes(String near, String far) {
			this.near = near;
			this.far = far;
		}

		public String getNear() {
			return near;
		}

		public String getFar() {
			return far;
		}
	}
}
