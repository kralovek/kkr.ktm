package kkr.ktm.domains.common.components.parametersformater.template;

import kkr.common.errors.ConfigurationException;
import kkr.ktm.domains.common.components.expressionparser.ExpressionParser;

public abstract class ParametersFormatterTemplateFwk {
	private boolean configured;

	private static final char DEFAULT_BRACKET_OPEN = '[';
	private static final char DEFAULT_BRACKET_CLOSE = ']';
	private static final char DEFAULT_ESCAPE = '\\';
	private static final char DEFAULT_QUOTE = '"';

	protected Character symbolBracketOpen;
	protected Character symbolBracketClose;
	protected Character symbolEscape;
	protected Character symbolQuote;

	protected ExpressionParser expressionParser;

	public void config() throws ConfigurationException {
		configured = false;
		if (symbolBracketOpen == null) {
			symbolBracketOpen = DEFAULT_BRACKET_OPEN;
		}
		if (symbolBracketClose == null) {
			symbolBracketClose = DEFAULT_BRACKET_CLOSE;
		}
		if (symbolEscape == null) {
			symbolEscape = DEFAULT_ESCAPE;
		}
		if (symbolQuote == null) {
			symbolQuote = DEFAULT_QUOTE;
		}
		if (expressionParser == null) {
			// OK
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public Character getSymbolBracketOpen() {
		return symbolBracketOpen;
	}

	public void setSymbolBracketOpen(Character symbolBracketOpen) {
		this.symbolBracketOpen = symbolBracketOpen;
	}

	public Character getSymbolBracketClose() {
		return symbolBracketClose;
	}

	public void setSymbolBracketClose(Character symbolBracketClose) {
		this.symbolBracketClose = symbolBracketClose;
	}

	public Character getSymbolEscape() {
		return symbolEscape;
	}

	public void setSymbolEscape(Character symbolEscape) {
		this.symbolEscape = symbolEscape;
	}

	public Character getSymbolQuote() {
		return symbolQuote;
	}

	public void setSymbolQuote(Character symbolQuote) {
		this.symbolQuote = symbolQuote;
	}

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}
}
