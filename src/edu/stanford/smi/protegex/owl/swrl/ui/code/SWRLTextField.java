package edu.stanford.smi.protegex.owl.swrl.ui.code;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLIncompleteRuleException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLTokenizer;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextField;

/**
 * A SymbolTextField with special support for editing SWRL expressions.
 * 
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class SWRLTextField extends SymbolTextField
{

	private final static char[][] charMap = { { '^', SWRLTokenizer.AND_CHAR }, { '.', SWRLTokenizer.RING_CHAR },
	// {'>', SWRLParser.IMP_CHAR}
	};

	public SWRLTextField(OWLModel owlModel, SymbolErrorDisplay errorDisplay)
	{
		super(owlModel, errorDisplay, new SWRLResourceNameMatcher(), new SWRLSyntaxConverter(owlModel));
		initKeymap(this);
	}

	@Override
	protected void checkUniCodeExpression(String uniCodeText) throws Throwable
	{
		SWRLParser parser = new SWRLParser(getOWLModel());

		if (isInSaveTestMode()) {
			parser.parse(uniCodeText);
		} else {
			try {
				parser.parse(uniCodeText);
			} catch (SWRLIncompleteRuleException e) {
				// Ignore incomplete rules during non save mode parsing
			} // try
		} // if
	} // checkUniCodeExpression

	public static void initKeymap(JTextComponent textComponent)
	{
		textComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent evt)
			{
				char ch = evt.getKeyChar();
				for (int i = 0; i < charMap.length; i++) {
					char[] chars = charMap[i];
					if (chars[0] == ch) {
						JTextComponent c = (JTextComponent)evt.getSource();
						try {
							String leftString = c.getDocument().getText(0, c.getCaretPosition()).trim();
							if (leftString.length() == 0 || leftString.endsWith(")"))
								c.getDocument().insertString(c.getCaretPosition(), "" + chars[1], null);
							else
								c.getDocument().insertString(c.getCaretPosition(), "" + ch, null);
							evt.consume();
							return;
						} catch (BadLocationException e) {
						}
					}
				}
			}
		});
	}
}
