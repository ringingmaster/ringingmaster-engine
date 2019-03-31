package org.ringingmaster.engine.parser.assignparsetype;

/**
 * The different types that elements can be parsed to.
 *
 * User: Stephen
 */
public enum ParseType {

	/** space */
	@Deprecated //TODO why do we need whitespace parsing?
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
	/** The detail that defines a variance
	 * 				omit parts 2 and 3 '[-2,3 -]'
	 * 				include in parts 4 and 8 '[+4,8 -]'
	 * 				omit from odd parts '[-o -]'
	 * 				include in even parts  '[+e -]'
	 * 				*/
	VARIANCE_DETAIL,

	/** a bracket for multiplier grouping - open '(' - needs a multiplier */
	MULTIPLIER_GROUP_OPEN,
	/** a bracket for multiplier grouping - close ')' - needs a multiplier */
	MULTIPLIER_GROUP_CLOSE,
	/** multiplier bracket for a multiplier grouping '3(' */
	MULTIPLIER_GROUP_OPEN_MULTIPLIER,
}
