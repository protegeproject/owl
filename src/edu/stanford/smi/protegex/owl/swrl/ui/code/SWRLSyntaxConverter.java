package edu.stanford.smi.protegex.owl.swrl.ui.code;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.JTextComponent;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLTokenizer;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextFormatter;
import edu.stanford.smi.protegex.owl.ui.code.SyntaxConverter;

/**
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class SWRLSyntaxConverter implements SyntaxConverter
{
	private final OWLModel owlModel;
	private static Map<String, String> map = new HashMap<String, String>();

	static {
		map.put("->", "" + SWRLTokenizer.IMP_CHAR);
	}

	public SWRLSyntaxConverter(OWLModel owlModel)
	{
		this.owlModel = owlModel;
	}

	@Override
	public void convertSyntax(JTextComponent textComponent)
	{
		OWLTextFormatter.updateSyntax(textComponent, owlModel, map);
	}
}
