package main.java.zenit.zencodearea;

import java.util.Map;

import org.antlr.v4.tool.Grammar;

/**
 * Describes the syntax for a ZenCodeArea
 */
public class Syntax {
	private Map<String, String> styles;
	private Grammar grammar;

	public Syntax(Grammar grammar, Map<String, String> styleMap){
		this.grammar = grammar;
		styles = styleMap;
	}

	public Map<String, String> getStyles() {
		return styles;
	}

	public Grammar getGrammar() {
		return grammar;
	}
}
