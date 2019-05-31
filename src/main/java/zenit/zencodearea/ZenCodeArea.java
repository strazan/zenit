package main.java.zenit.zencodearea;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.concurrent.Task;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.Nodes;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;



public class ZenCodeArea extends CodeArea {
	private ExecutorService executor;
//	private int fontSize;
//	private String font;

	private static final String[] KEYWORDS = new String[] {
		"abstract", "assert", "boolean", "break", "byte",
		"case", "catch", "char", "class", "const",
		"continue", "default", "do", "double", "else",
		"enum", "extends", "false", "final", "finally", "float",
		"for", "goto", "if", "implements", "import",
		"instanceof", "int", "interface", "long", "native",
		"new", "package", "private", "protected", "public",
		"return", "short", "static", "strictfp", "super",
		"switch", "synchronized", "this", "throw", "throws",
		"transient", "true", "try", "void", "volatile", "while"
	};

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

	private static final Pattern PATTERN = Pattern.compile(
		"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
		+ "|(?<PAREN>" + PAREN_PATTERN + ")"
		+ "|(?<BRACE>" + BRACE_PATTERN + ")"
		+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
		+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
		+ "|(?<STRING>" + STRING_PATTERN + ")"
		+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
	);

	public ZenCodeArea() {
		this(14, "Times new Roman");
	}
	
	public ZenCodeArea(int textSize, String font) {
		setParagraphGraphicFactory(LineNumberFactory.get(this));

		multiPlainChanges().successionEnds(
			Duration.ofMillis(100)).subscribe(
				ignore -> setStyleSpans(0, computeHighlighting(getText(
			))));

		executor = Executors.newSingleThreadExecutor();
		setParagraphGraphicFactory(LineNumberFactory.get(this));
		multiPlainChanges().successionEnds(Duration.ofMillis(500)).supplyTask(
			this::computeHighlightingAsync).awaitLatest(multiPlainChanges()).filterMap(t -> {
				if(t.isSuccess()) {
					return Optional.of(t.get());
				} else {
					t.getFailure().printStackTrace();
					return Optional.empty();
				}
		}).subscribe(this::applyHighlighting);
		computeHighlightingAsync();

//		fontSize = textSize;
//		this.font = font;
		setStyle("-fx-font-size: " + textSize +";-fx-font-family: " + font);
	}
	
	public void update() {
		var highlighting = computeHighlighting(getText());
		applyHighlighting(highlighting);
	}
	
//	public int getFontSize() {
//		return fontSize;	
//	}
//	
//	
//	public String getFont() {
//		return font;	
//	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeHighlighting(text);
			}
		};
		executor.execute(task);
		return task;
	}

	
	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		setStyleSpans(0, highlighting);
		InputMap<KeyEvent> im = InputMap.consume(
			EventPattern.keyPressed(KeyCode.TAB), 
			e -> this.replaceSelection("    ")
			);
		Nodes.addInputMap(this, im);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder
		= new StyleSpansBuilder<>();
		while(matcher.find()) {
			String styleClass =
					matcher.group("KEYWORD") != null ? "keyword" :
						matcher.group("PAREN") != null ? "paren" :
							matcher.group("BRACE") != null ? "brace" :
								matcher.group("BRACKET") != null ? "bracket" :
									matcher.group("SEMICOLON") != null ? "semicolon" :
										matcher.group("STRING") != null ? "string" :
											matcher.group("COMMENT") != null ? "comment" :
												null; /* never happens */ 
			assert styleClass != null;
			spansBuilder.add(
					Collections.emptyList(), matcher.start() - lastKwEnd
					);
			spansBuilder.add(
					Collections.singleton(styleClass), matcher.end() - matcher.start()
					);
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

		return spansBuilder.create();
	}

	public void setFontSize(int newFontSize) {
		//fontSize = newFontSize;
		setStyle("-fx-font-size: " + newFontSize);
	}
	
	public void updateAppearance(String fontFamily, int size) {
	//	font = fontFamily;
		setStyle("-fx-font-family: " + fontFamily + ";" + 
				"-fx-font-size: " + size + ";");
	}	
}
