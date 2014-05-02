package edu.stanford.smi.protegex.owl.swrl.parser;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SWRLTokenizer
{
	public final static char AND_CHAR = '\u2227'; // ^
	public final static char IMP_CHAR = '\u2192'; // >
	public final static char RING_CHAR = '\u02da'; // .
	public final static String delimiters = " ?\n\t()[],\"'" + AND_CHAR + IMP_CHAR + RING_CHAR; // Note space.

	private final StringTokenizer internalTokenizer;

	public SWRLTokenizer(String input)
	{
		this.internalTokenizer = new StringTokenizer(input, delimiters, true);
	}

	public boolean hasMoreTokens()
	{
		return this.internalTokenizer.hasMoreTokens();
	}

	public String nextToken(String myDelimiters)
	{
		return this.internalTokenizer.nextToken(myDelimiters);
	}

	public String nextToken() throws NoSuchElementException
	{
		String token = this.internalTokenizer.nextToken(delimiters);
		if (!token.equals("'"))
			return token;

		StringBuffer buffer = new StringBuffer();
		while (this.internalTokenizer.hasMoreTokens() && !(token = this.internalTokenizer.nextToken()).equals("'")) {
			buffer.append(token);
		}
		return buffer.toString();
	}

	public String getNextNonSpaceToken(String noTokenMessage, boolean parseOnly) throws SWRLParseException
	{
		String token = "";
		String errorMessage = "Incomplete rule. " + noTokenMessage;

		if (!hasMoreTokens()) {
			if (parseOnly)
				throw new SWRLIncompleteRuleException(errorMessage);
			else
				throw new SWRLParseException(errorMessage);
		}

		while (hasMoreTokens()) {
			token = nextToken();
			if (!(token.equals(" ") || token.equals("\n") || token.equals("\t")))
				return token;
		}

		if (parseOnly)
			throw new SWRLIncompleteRuleException(errorMessage);
		else
			throw new SWRLParseException(errorMessage); // Should not get here
	}

	public void checkAndSkipToken(String skipToken, String unexpectedTokenMessage, boolean parseOnly)
			throws SWRLParseException
	{
		String token = getNextNonSpaceToken(unexpectedTokenMessage, parseOnly);

		if (!token.equalsIgnoreCase(skipToken))
			throw new SWRLParseException("Expecting " + skipToken + ", got " + token + "; " + unexpectedTokenMessage);
	}

	// TODO: Does not deal with escaped quotation characters.
	public String getNextStringToken(String noTokenMessage, boolean parseOnly) throws SWRLParseException
	{
		String token = "";
		String errorMessage = "Incomplete rule. " + noTokenMessage;

		if (!hasMoreTokens()) {
			if (parseOnly)
				throw new SWRLIncompleteRuleException(errorMessage);
			else
				throw new SWRLParseException(errorMessage);
		}

		while (hasMoreTokens()) {
			token = nextToken("\"");
			if (token.equals("\""))
				token = ""; // Empty string
			else
				checkAndSkipToken("\"", "Expected \" to close string.", parseOnly);
			return token;
		}

		if (parseOnly)
			throw new SWRLIncompleteRuleException(errorMessage);
		else
			throw new SWRLParseException(errorMessage); // Should not get here
	}
}
