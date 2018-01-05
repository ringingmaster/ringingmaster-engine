package org.ringingmaster.engine.parser;

/**
 * The different types that elements can be parsed to.
 *
 * User: Stephen
 */
public enum ParseType {

	/** space */
	WHITESPACE,
	/** a normal calling position e.g. W B H*/
	CALLING_POSITION,
	/** aggregate calling position. e.g. 1,2 in Steadman */
//TODO	CALLING_POSITION_AGGREGATE,
	/** a number on its own acting as a default call */
	DEFAULT_CALL_MULTIPLIER,
	/** a normal splice letter */
	SPLICE,
	/** splice multiplier */
	SPLICE_MULTIPLIER,
	/** plain lead in lead based */
	PLAIN_LEAD,
	/** multiplier for a plain lead in lead based */
	PLAIN_LEAD_MULTIPLIER,
	/** an actual call */
	CALL,
	/** an ordinary call multiplier */
	CALL_MULTIPLIER,
	/** macro to replace a piece of text with another */
	DEFINITION,
	/** multiplier for macro to replace on piece of text with another */
	DEFINITION_MULTIPLIER,
	/** a block include from right curly brace */
//TODO	BLOCK_DEFINITION,
	/** a block include  multiplier from right curly brace */
//TODO	BLOCK_DEFINITION_MULTIPLIER,
	/** a square variance bracket - open '[' */
	VARIANCE_OPEN,
	/** a square variance bracket - close ']' */
	VARIANCE_CLOSE,
	/** a normal bracket for grouping - open '(' - needs a multiplier */
	GROUP_OPEN,
	/** a normal bracket for grouping - close ')' - needs a multiplier */
	GROUP_CLOSE,
	/** multiplier for a normal bracket for grouping '(' */
	GROUP_OPEN_MULTIPLIER,
}
