package zenit.textFlow;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;


public class SyntaxHighlight {

	private CodeArea codeArea;
	private ExecutorService executor;

	private static final String[] KEYWORDS = new String[] {
			"abstract", "assert", "boolean", "break", "byte",
			"case", "catch", "char", "class", "const",
			"continue", "default", "do", "double", "else",
			"enum", "extends", "final", "finally", "float",
			"for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public",
			"return", "short", "static", "strictfp", "super",
			"switch", "synchronized", "this", "throw", "throws",
			"transient", "try", "void", "volatile", "while"
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

	public SyntaxHighlight(CodeArea codeArea) {

		this.codeArea = codeArea;
		String code = codeArea.getText();

		// add line numbers to the left of area
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		// recompute the syntax highlighting 500 ms after user stops editing area
		Subscription cleanupWhenNoLongerNeedIt = codeArea

				// plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
				// multi plain changes = save computation by not rerunning the code multiple times
				//   when making multiple changes (e.g. renaming a method at multiple parts in file)
			.multiPlainChanges()

				// do not emit an event until 500 ms have passed since the last emission of previous stream
			.successionEnds(Duration.ofMillis(100))

				// run the following code block when previous stream emits an event
			.subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText(
		))));

		// when no longer need syntax highlighting and wish to clean up memory leaks
		// run: `cleanupWhenNoLongerNeedIt.unsubscribe();`

		executor = Executors.newSingleThreadExecutor();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		Subscription cleanupWhenDone = codeArea.multiPlainChanges()
				.successionEnds(Duration.ofMillis(500))
				.supplyTask(this::computeHighlightingAsync)
				.awaitLatest(codeArea.multiPlainChanges())
				.filterMap(t -> {
					if(t.isSuccess()) {
						return Optional.of(t.get());
					} else {
						t.getFailure().printStackTrace();
						return Optional.empty();
					}
				})
				.subscribe(this::applyHighlighting);

		// call when no longer need it: `cleanupWhenFinished.unsubscribe();`

		computeHighlightingAsync();
	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = codeArea.getText();
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
		codeArea.setStyleSpans(0, highlighting);
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
}
